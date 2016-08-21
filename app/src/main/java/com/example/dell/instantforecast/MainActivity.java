package com.example.dell.instantforecast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final String FILENAME = "AppLocationData";
    static AppDataModel appDataModel = new AppDataModel();
    static NavigationMenuListAdapter navigationMenuListAdapter;
    boolean firstStart = true;
    static MainActivity mainActivity;
    static GPSTracker gpsTracker;
    static Toolbar toolbar;
    ActionBar actionBar;
    static DrawerLayout drawer;
    NavigationView navigationView;
    static ListView navigationMenuList;
    static ImageView background_image_view;
    static TextView city_name_textview, city_time_textview;
    static boolean loadFromWelcome;
    static boolean loadFromNotification;
    static double locationLatFromNotification, locationLonFromNotification;
    static String locationIdFromNotification;
    static ViewTreeObserver.OnScrollChangedListener offlineScroll;

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
                WeatherInfoFragment.loadWeather(
                        new LocationWeatherInfo("get_current_location", 0, myLocation.getLatitude(), myLocation.getLongitude()),
                        true);
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
        loadFromWelcome = intent.getBooleanExtra("loadFromWelcome", false);
        loadFromNotification = intent.getBooleanExtra("loadFromNotification", false);
        if (loadFromNotification) {
            locationLatFromNotification = intent.getDoubleExtra("locationLatFromNotification", 0);
            locationLonFromNotification = intent.getDoubleExtra("locationLonFromNotification", 0);
            locationIdFromNotification = intent.getStringExtra("locationIdFromNotification");
        }
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

        offlineScroll = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                WeatherInfoFragment.mainScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        };
        if(!GeneralUtils.isOnline(mainActivity)){
            Toast networkError = Toast.makeText(MainActivity.mainActivity, "Can't connect to internet!!", Toast.LENGTH_LONG);
            networkError.show();
            WeatherInfoFragment.mainScrollView.getViewTreeObserver().addOnScrollChangedListener(offlineScroll);
        }
    }

    @Override
    protected void onPause() {
        saveAppData();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firstStart == true) {
            loadAppData();
            if (loadFromNotification) {
                WeatherInfoFragment.loadWeather(
                        new LocationWeatherInfo("get_current_location", 0, locationLatFromNotification, locationLonFromNotification),
                        true);

                loadFromNotification = false;
            } else {
                Location location = gpsTracker.getLocation();
                WeatherInfoFragment.loadWeather(
                        new LocationWeatherInfo("get_current_location", 0, location.getLatitude(), location.getLongitude()),
                        true);
                firstStart = false;
                if (loadFromWelcome) {
                    for (int i = 0; i < WelcomeActivity.selectedLocation.size(); i++) {
                        for (int j = 0; j < WelcomeActivity.popularLocation.size(); j++) {
                            if (WelcomeActivity.selectedLocation.get(i).equals(WelcomeActivity.popularLocation.get(j).name)) {
                                LocationWeatherInfo locationInfo = WelcomeActivity.popularLocation.get(j);
                                WeatherInfoFragment.loadWeather( new LocationWeatherInfo(
                                        locationInfo.id,
                                        0,
                                        locationInfo.lat,
                                        locationInfo.lon),
                                        false
                                );
                                break;
                            }
                        }
                    }
                    WelcomeActivity.selectedLocation.clear();
                    WelcomeActivity.popularLocation.clear();
                    loadFromWelcome = false;
                }
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
                saveBitmap(takeScreenshot());
                shareIt();
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

    File imagePath;

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.destroyDrawingCache();
        return bitmap;
    }

    public void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt() {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "In Tweecher, My highest score with screen shot";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Tweecher score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
