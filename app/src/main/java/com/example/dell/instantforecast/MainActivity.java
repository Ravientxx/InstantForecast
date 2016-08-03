package com.example.dell.instantforecast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    Toolbar toolbar;
    ActionBar actionBar;
    static DrawerLayout drawer;
    NavigationView navigationView;
    ListView navigationMenuList;
    static MainActivity mainActivity;
    TextView detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon;
    TextView customTitle, customSubtitle;
    Typeface weatherFont;

    public void loadCurrentWeather(final String Lat, final String Lon, final boolean doAddCity, final boolean doAddCurrentLocation) {
        if (isOnline()) {
            OpenWeatherMapApiManager.placeIdTask getCurrentWeatherTask = new OpenWeatherMapApiManager.placeIdTask(new OpenWeatherMapApiManager.AsyncResponse() {
                public void processFinish(String weather_country, String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                    CityNowWeatherInfo current_cityNowWeatherInfo = new CityNowWeatherInfo(
                            weather_city,
                            weather_country,
                            weather_iconText,
                            weather_temperature,
                            Lat,
                            Lon
                    );
                    if (doAddCity) {
                        if (doAddCurrentLocation) {
                            System.out.println("abc");
                            MainActivity.appDataModel.current_city = current_cityNowWeatherInfo;
                        } else {
                            boolean cityExisted = false;
                            for(int i = 0; i < MainActivity.appDataModel.city_list.size(); i++){
                                if(current_cityNowWeatherInfo.name.equals(MainActivity.appDataModel.city_list.get(i).name)){
                                    cityExisted = true;
                                    break;
                                }
                            }
                            if(!cityExisted){
                                MainActivity.appDataModel.city_list.add(current_cityNowWeatherInfo);
                            }
                            else{// Update City Info

                            }
                            updateNavigationMenuList();
                        }
                    }
                    customTitle.setText(weather_city + "," + weather_country);
                    detailsField.setText(weather_description);
                    currentTemperatureField.setText(weather_temperature);
                    //humidity_field.setText("Humidity: "+weather_humidity);
                    //pressure_field.setText("Pressure: "+weather_pressure);
                    weatherIcon.setText(Html.fromHtml(weather_iconText));
                }
            });
            getCurrentWeatherTask.execute(Lat, Lon);

            GoogleTimezoneAPI.getDateTimeByLocationTask getDateTimeTask = new GoogleTimezoneAPI.getDateTimeByLocationTask(new GoogleTimezoneAPI.AsyncResponse() {
                @Override
                public void processFinish(String date) {
                    customSubtitle.setText(date);
                }
            });
            getDateTimeTask.execute(Lat, Lon);
        } else {
            Toast networkError = Toast.makeText(MainActivity.mainActivity, "Can't connect to internet!!", Toast.LENGTH_LONG);
            networkError.show();
        }
    }

    private void updateNavigationMenuList() {
        navigationMenuListAdapter = new NavigationMenuListAdapter(MainActivity.this, appDataModel.city_list);
        navigationMenuList.setAdapter(navigationMenuListAdapter);
    }

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
                    loadCurrentWeather(
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

        //Set layout for activity
        setContentView(R.layout.activity_main);

        mainActivity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set custom view for action bar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle("");
            actionBar.setSubtitle("");
        }

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup)findViewById(R.id.main_view);
        inflater.inflate(R.layout.weather_info_scroll_view, parent);

        loadLayoutItem();
        //Init Joda-Time library
        JodaTimeAndroid.init(this);
        //Init google Location API
        GoogleLocationAPI.initGoogleClient();

        initNavigationMenu();

        firstStart = true;

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
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
        GoogleLocationAPI.connect();
        loadAppData();
        updateNavigationMenuList();
        super.onStart();
        if (firstStart == true) {
            loadCurrentWeather(
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
                clearAppData();
                updateNavigationMenuList();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAppData() {
        appDataModel.city_list.clear();
    }

    public void loadLayoutItem() {
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        pressure_field = (TextView) findViewById(R.id.pressure_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        customSubtitle = (TextView) findViewById(R.id.custom_subtitle);
        customTitle = (TextView) findViewById(R.id.custom_title);
    }
}
