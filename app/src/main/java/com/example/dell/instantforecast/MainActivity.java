package com.example.dell.instantforecast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final String FILENAME = "AppCityData";
    static AppDataModel appDataModel;
    static NavigationMenuListAdapter navigationMenuListAdapter;
    boolean firstStart;
    boolean dataFileNotFound;
    static Toolbar toolbar;
    ActionBar actionBar;
    static DrawerLayout drawer;
    NavigationView navigationView;
    ListView navigationMenuList;
    static MainActivity mainActivity;

    static ImageView background_image_view;
    static TextView city_name_textview, city_time_textview;

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
                startActivity(new Intent(MainActivity.this, AddCityActivity.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = GoogleLocationAPI.getLocation();
                if (myLocation != null) {
                    WeatherInfoFragment.loadWeatherInfo(
                            String.valueOf(myLocation.getLatitude()),
                            String.valueOf(myLocation.getLongitude()),
                            true,
                            true
                    );
                }
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

        //Set layout for activity
        setContentView(R.layout.activity_main);

        city_time_textview = (TextView) MainActivity.mainActivity.findViewById(R.id.city_time_textview);
        city_name_textview = (TextView) MainActivity.mainActivity.findViewById(R.id.city_name_textview);

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
        //Init google Location API
        GoogleLocationAPI.initGoogleClient();

        initNavigationMenu();

        background_image_view = (ImageView) findViewById(R.id.background_image_view);
        background_image_view.setImageResource(R.drawable.back);

        firstStart = true;
        dataFileNotFound = false;
    }

    @Override
    protected void onPause() {
        saveAppData();
        super.onPause();
    }

    @Override
    protected void onStop() {
        GoogleLocationAPI.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleLocationAPI.connect();
        loadAppData();
        if (firstStart == true && dataFileNotFound == false) {
            WeatherInfoFragment.loadWeatherInfo(
                    appDataModel.current_city.lat,
                    appDataModel.current_city.lon,
                    false,
                    false
            );
            firstStart = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; add items to the action bar
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
                //clearAppData();
                //updateNavigationMenuList();
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

    private void saveAppData() {
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

    private void loadAppData() {
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
            appDataModel = new AppDataModel();
            appDataModel.city_list = new ArrayList<CityNowWeatherInfo>();
            appDataModel.current_city = new CityNowWeatherInfo("Ho Chi Minh","VN","","","10.75","106.666672");
            dataFileNotFound = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            updateNavigationMenu();
        }
    }
    public void updateNavigationMenu(){
        navigationMenuListAdapter = new NavigationMenuListAdapter(MainActivity.this, appDataModel.city_list);
        navigationMenuList.setAdapter(navigationMenuListAdapter);
    }
    private void clearAppData() {
        appDataModel.city_list.clear();
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



}
