package com.example.dell.instantforecast;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final String FILENAME = "AppLocationData";
    static AppDataModel appDataModel = new AppDataModel();
    static NavigationMenuListAdapter navigationMenuListAdapter;
    boolean firstStart = true;
    static MainActivity mainActivity;
    GPSTracker gpsTracker;
    static Toolbar toolbar;
    ActionBar actionBar;
    static DrawerLayout drawer;
    NavigationView navigationView;
    static ListView navigationMenuList;
    static ImageView background_image_view;
    static TextView city_name_textview, city_time_textview;
    boolean selectFromWelcome;
    GoogleMap map;

    private void initNavigationMenu() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    int id = item.getItemId();
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }
        navigationMenuList = (ListView) findViewById(R.id.nav_menu_list);

        ImageView editLocation = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.editLocation);
        ImageView myLocation = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.myLocation);
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditLocationActivity.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = gpsTracker.getLocation();
                WeatherInfoFragment.loadWeatherInfo(
                        "get_current_location",
                        myLocation.getLatitude(),
                        myLocation.getLongitude(),
                        true
                );
                drawer.closeDrawer(GravityCompat.START);
            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainActivity = this;
        gpsTracker = new GPSTracker(this);
        //Set layout for activity
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        selectFromWelcome = intent.getBooleanExtra("selectFromWelcome", false);
        System.out.println(selectFromWelcome);
        city_time_textview = (TextView) findViewById(R.id.city_time_textview);
        city_name_textview = (TextView) findViewById(R.id.city_name_textview);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //Set custom view for action bar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle("");
            actionBar.setSubtitle("");
            actionBar.setElevation(0);
        }

        //Init Joda-Time library
        JodaTimeAndroid.init(this);

        initNavigationMenu();

        background_image_view = (ImageView) findViewById(R.id.background_image_view);
    }

    @Override
    protected void onPause() {
        saveAppData();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAppData();
        if (firstStart == true) {
            Location location = gpsTracker.getLocation();
            WeatherInfoFragment.loadWeatherInfo(
                    "get_current_location",
                    location.getLatitude(),
                    location.getLongitude(),
                    true
            );
            firstStart = false;
            if (selectFromWelcome) {
                appDataModel.city_list = new ArrayList<LocationWeatherInfo>();
                for (int i = 0; i < WelcomeActivity.selectedLocation.size(); i++) {
                    for (int j = 0; j < WelcomeActivity.popularLocation.size(); j++) {
                        if (WelcomeActivity.selectedLocation.get(i).equals(WelcomeActivity.popularLocation.get(j).name)) {
                            LocationWeatherInfo locationInfo = WelcomeActivity.popularLocation.get(j);
                            WeatherInfoFragment.loadWeatherInfo(
                                    locationInfo.id,
                                    locationInfo.lat,
                                    locationInfo.lon,
                                    false
                            );
                            break;
                        }
                    }
                }
                WelcomeActivity.selectedLocation.clear();
                WelcomeActivity.popularLocation.clear();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //user clicked a menu-item from ActionBar
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, AppSettingActivity.class));
                return true;
            case R.id.action_abouts:
                shareImage();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void saveAppData() {
        String string = new Gson().toJson(appDataModel, AppDataModel.class);
        try {
            FileOutputStream out = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter fos = new OutputStreamWriter(out, "UTF-8");
            fos.write(string);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAppData() {
        String string1 = "";
        try {
            FileInputStream inputStream = openFileInput(FILENAME);
            InputStreamReader fin = new InputStreamReader(inputStream, "UTF-8");
            int i = 0;
            while ((i = fin.read()) != -1) {
                string1 += (char) i;
            }
            fin.close();
            appDataModel = new Gson().fromJson(string1, AppDataModel.class);
            System.out.println(string1);
        } catch (FileNotFoundException e) {
            appDataModel.city_list = new ArrayList<LocationWeatherInfo>();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            navigationMenuListAdapter = new NavigationMenuListAdapter(MainActivity.this, appDataModel.city_list);
            navigationMenuList.setAdapter(navigationMenuListAdapter);
        }
    }
    
    public void shareImage(){
        String path=Environment.getExternalStorageDirectory()+File.separator+"Screenshot.jpeg";
        File imageFile=new File(path);
        // create bitmap screen capture
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        View v = this.getWindow().getDecorView().findViewById(R.id.capture).getRootView();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);
        // v1.setDrawingCacheEnabled( false);
        OutputStream fout = null ;
        try {
            fout = new FileOutputStream(imageFile);
            returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 90 , fout);
            fout.flush();
            fout.close();
            Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
        } catch ( FileNotFoundException e) {
            // TODO Auto-generated catch block
            Toast.makeText(this,"File not found!", Toast.LENGTH_SHORT).show();
            // e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "IO Exception!", Toast.LENGTH_SHORT).show();
            // e.printStackTrace();
        }

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile( new File (path)));

        this.startActivity(i);
    }

}
