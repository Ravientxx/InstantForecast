package com.example.dell.instantforecast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dell on 8/13/2016.
 */
public class WeatherInfoFragment extends Fragment {

    static int BACKGROUND_IMAGE_ID;
    static TextView detailsField, currentTemperatureField, max_temperature, min_temperature, weatherIcon;
    static TextView humidityField, pressureField, windSpeedField, windDegField, sunRiseField, sunSetField;
    static ImageView max_img, min_img;
    static ScrollView mainScrollView;
    static ListView hourlyListView,dailyListView;
    static int screenHeight;
    static ArrayList<Bitmap> blurred_background_image;
    static Bitmap background_image;
    static GoogleMap mGoogleMap;
    static RelativeLayout current_condition_layout;
    static WeatherInfoFragment frag;
    SupportMapFragment mSupportMapFragment;
    static Timer myTimer;
    static List<SingleItem> linkss;
    static ListView listRss;
    SingleItem selectedNewsItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_info_fragment, container, false);
        mSupportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_view);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map_view, mSupportMapFragment).commit();
        }
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                }
            });
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frag = this;

        detailsField = (TextView) view.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) view.findViewById(R.id.current_temperature);
        weatherIcon = (TextView) view.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(Typeface.createFromAsset(MainActivity.mainActivity.getAssets(), "fonts/weathericons-regular-webfont.ttf"));
        max_img = (ImageView) view.findViewById(R.id.max_icon);
        max_temperature = (TextView) view.findViewById(R.id.max_temperature);
        min_img = (ImageView) view.findViewById(R.id.min_icon);
        min_temperature = (TextView) view.findViewById(R.id.min_temperature);
        humidityField = (TextView) view.findViewById(R.id.humidity_field);
        pressureField = (TextView) view.findViewById(R.id.pressure_field);
        windSpeedField = (TextView) view.findViewById(R.id.wind_speed_field);
        windDegField = (TextView) view.findViewById(R.id.wind_deg_field);
        sunRiseField = (TextView) view.findViewById(R.id.sun_rise_field);
        sunSetField = (TextView) view.findViewById(R.id.sun_set_field);
        hourlyListView = (ListView) view.findViewById(R.id.hourly_list_view);
        dailyListView = (ListView) view.findViewById(R.id.daily_list_view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        MainActivity.mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;

        current_condition_layout = (RelativeLayout) view.findViewById(R.id.current_condition_screen);
        blurred_background_image = new ArrayList<>();

        listRss = (ListView)view.findViewById(R.id.myListView);
        listRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedNewsItem = linkss.get(position);
                final Uri storyLink = Uri.parse(selectedNewsItem.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, storyLink);
                startActivity(intent);
            }
        });

//        DownloadRssFeed downloader = new DownloadRssFeed(MainActivity.mainActivity);
  //      downloader.execute("http://tuoitre.vn/rss/tt-the-gioi.rss");


        current_condition_layout = (RelativeLayout) view.findViewById(R.id.current_condition_screen);
        mainScrollView = (ScrollView) view.findViewById(R.id.weather_info_scroll_view);
    }

    static public void loadWeather(final LocationWeatherInfo location, final boolean displayWeatherInfo) {
        long now = System.currentTimeMillis();
        if ((now - location.updateTime) > (1000 * 60 * 10)) {
            GeneralUtils.printDebug("Update weather info");
            if (GeneralUtils.isOnline(MainActivity.mainActivity)) {
                mainScrollView.getViewTreeObserver().removeOnScrollChangedListener(MainActivity.offlineScroll);
                mainScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        int scrollY = mainScrollView.getScrollY(); //for verticalScrollView
                        int stepScreenHeight = screenHeight / 3;
                        if (scrollY <= 0) {
                            background_image = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                            MainActivity.mainActivity.background_image_view.setImageBitmap(background_image);
                        }
                        if (scrollY > stepScreenHeight) {
                            int y = screenHeight / 5;
                            if (scrollY >= stepScreenHeight && scrollY < (stepScreenHeight + y)) {
                                MainActivity.mainActivity.background_image_view.setImageBitmap(blurred_background_image.get(0));
                            }
                            if (scrollY >= (stepScreenHeight + y) && scrollY < (stepScreenHeight + 2 * y)) {
                                MainActivity.mainActivity.background_image_view.setImageBitmap(blurred_background_image.get(1));
                            }
                            if (scrollY >= (stepScreenHeight + 2 * y) && scrollY < (stepScreenHeight + 3 * y)) {
                                MainActivity.mainActivity.background_image_view.setImageBitmap(blurred_background_image.get(2));
                            }
                        }
                        MainActivity.mainActivity.background_image_view.setScaleType(ImageView.ScaleType.CENTER);
                    }
                });
                OpenWeatherMapApiManager.GetCurrentWeatherConditionTask getCurrentWeatherTask = new OpenWeatherMapApiManager.GetCurrentWeatherConditionTask(new OpenWeatherMapApiManager.AsyncResponse() {
                    public void processFinish(LocationWeatherInfo current_locationWeatherInfo) {
                        GoogleTimezoneAPI.getDateTimeByLocationTask getDateTimeTask = new GoogleTimezoneAPI.getDateTimeByLocationTask(new GoogleTimezoneAPI.AsyncResponse() {
                            @Override
                            public void processFinish(String date) {
                                MainActivity.city_time_textview.setText(date);
                            }
                        });
                        getDateTimeTask.execute(String.valueOf(location.lat), String.valueOf(location.lon));
                        current_locationWeatherInfo.timeZone = GoogleTimezoneAPI.current_timezone;
                        current_locationWeatherInfo.updateTime = System.currentTimeMillis();
                        current_locationWeatherInfo.id = location.id;
                        current_locationWeatherInfo.lat = location.lat;
                        current_locationWeatherInfo.lon = location.lon;
                        AppSettingModel appSettingModel = GeneralUtils.loadAppSetting(MainActivity.mainActivity);
                        if(appSettingModel.Unit.equals("F")){
                            current_locationWeatherInfo = GeneralUtils.locationWithFahrenheit(current_locationWeatherInfo);
                        }
                        if (location.id.equals("get_current_location")) {
                            MainActivity.appDataModel.selected_location_index = -1;
                        } else {
                            int locationIndex = -1;
                            for (int i = 0; i < MainActivity.appDataModel.city_list.size(); i++) {
                                if (current_locationWeatherInfo.name.equals(MainActivity.appDataModel.city_list.get(i).name)) {
                                    locationIndex = i;
                                    break;
                                }
                            }
                            System.out.println(locationIndex);
                            if (locationIndex == -1 || location.id.equals("add_location_map")) {
                                MainActivity.appDataModel.selected_location_index = MainActivity.appDataModel.city_list.size();
                                MainActivity.appDataModel.city_list.add(current_locationWeatherInfo);
                                if (EditLocationActivity.editLocationListAdapter != null)
                                    EditLocationActivity.editLocationListAdapter.notifyDataSetChanged();
                                current_locationWeatherInfo.id = "added_location_map";
                            } else {// Update City Info
                                MainActivity.appDataModel.city_list.remove(locationIndex);
                                MainActivity.appDataModel.city_list.add(locationIndex, current_locationWeatherInfo);
                            }
                            MainActivity.navigationMenuListAdapter = new NavigationMenuListAdapter(MainActivity.mainActivity, MainActivity.appDataModel.city_list);
                            MainActivity.navigationMenuList.setAdapter(MainActivity.navigationMenuListAdapter);
                        }
                        current_condition_layout.setMinimumHeight(screenHeight - MainActivity.toolbar.getHeight());

                        if (displayWeatherInfo)
                            displayWeatherInfo(current_locationWeatherInfo);
                    }
                });
                getCurrentWeatherTask.execute(String.valueOf(location.lat), String.valueOf(location.lon));
            } else {
                Toast networkError = Toast.makeText(MainActivity.mainActivity, "Can't connect to internet!!", Toast.LENGTH_LONG);
                networkError.show();
            }
        } else {
            GeneralUtils.printDebug("Load saved weather info");
            displayWeatherInfo(location);
        }
        final Handler updateTimeHandler = new Handler() {
            public void handleMessage(Message msg) {
                DateTimeZone zone = DateTimeZone.forID(location.timeZone);
                DateTime dt = new DateTime(zone);
                MainActivity.city_time_textview.setText(dt.toString("EEE, d MMM yyyy, hh:mm a"));
            }
        };
        if (myTimer != null) {
            myTimer.cancel();
        }
        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                updateTimeHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 60000);
    }

    static public void displayWeatherInfo(LocationWeatherInfo location) {
        //Current condition
        MainActivity.city_name_textview.setText(location.name + "," + location.country);
        detailsField.setText(location.description);
        currentTemperatureField.setText(location.temperature);
        weatherIcon.setText(Html.fromHtml(location.weatherIconText));
        max_img.setImageResource(R.drawable.ic_vertical_align_top_white_24dp);
        min_img.setImageResource(R.drawable.ic_vertical_align_bottom_white_24dp);
        max_temperature.setText(location.max_temperature);
        min_temperature.setText(location.min_temperature);


        //Detail
        humidityField.setText(location.humidity);
        pressureField.setText(location.pressure);
        windSpeedField.setText(location.windSpeed);
        windDegField.setText(GeneralUtils.windDegreeToDirection(location.windDegree));
        DateTimeZone zone = DateTimeZone.forID(location.timeZone);
        DateTime sunRise = new DateTime(Long.parseLong(location.sunRise),zone);
        DateTime sunSet = new DateTime(Long.parseLong(location.sunSet),zone);
        sunRiseField.setText(sunRise.toString("hh:mm a"));
        sunSetField.setText(sunSet.toString("hh:mm a"));

        mainScrollView.setEnabled(false);

        //Hourly
        HourlyListAdapter hourlyListAdapter = new HourlyListAdapter(frag.getContext(), location.hourlyWeatherList,location.timeZone);
        hourlyListView.setFocusable(false);
        hourlyListView.setAdapter(hourlyListAdapter);
        hourlyListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        //Daily
        dailyListView.setFocusable(false);
        DailyListAdapter dailyListAdapter = new DailyListAdapter(frag.getContext(), location.dailyWeatherList,location.timeZone);
        dailyListView.setAdapter(dailyListAdapter);
        dailyListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listRss.setFocusable(false);
        DownloadRssFeed downloader = new DownloadRssFeed(MainActivity.mainActivity);
        downloader.execute("https://news.google.com.vn/news?cf=all&pz=1&ned=uk&output=rss");


        mainScrollView.setEnabled(true);


        BACKGROUND_IMAGE_ID = location.conditionId;
        blurred_background_image.clear();
        Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
        MainActivity.mainActivity.background_image_view.setImageBitmap(bitmap);
        blurred_background_image.add(GeneralUtils.blur(bitmap, 5f));
        bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
        blurred_background_image.add(GeneralUtils.blur(bitmap, 15f));
        bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
        blurred_background_image.add(GeneralUtils.blur(bitmap, 25f));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(location.lat, location.lon))
                .zoom(8)
                .build();

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(location.lat,location.lon));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(options);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                frag.startActivity(new Intent(frag.getContext(), MapLayerActivity.class));
            }
        });
    }

    public static class HourlyListAdapter extends BaseAdapter {
        public Context contextListView;
        ArrayList<LocationWeatherInfo.HourlyWeatherInfo> listModels;
        String timezone;

        HourlyListAdapter(Context context,ArrayList<LocationWeatherInfo.HourlyWeatherInfo> city_list,String timezone){
            contextListView = context;
            listModels = city_list;
            this.timezone = timezone;
        }
        @Override
        public int getCount() {
            return listModels.size();
        }

        @Override
        public Object getItem(int position) {
            return listModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LocationWeatherInfo.HourlyWeatherInfo current_city = listModels.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) contextListView.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.nav_menu_list_item, parent, false);
            }
            final TextView weatherIcon = (TextView) convertView.findViewById(R.id.weather_icon);
            final TextView cityName = (TextView) convertView.findViewById(R.id.city_name);
            final TextView temperature = (TextView) convertView.findViewById(R.id.temperature_field);

            final Typeface weatherFont = Typeface.createFromAsset(convertView.getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
            weatherIcon .setText(Html.fromHtml(current_city.weatherIconText));
            weatherIcon.setTypeface(weatherFont);
            DateTimeZone zone = DateTimeZone.forID(timezone);
            DateTime dt = new DateTime(Long.parseLong(current_city.time),zone);
            cityName.setText(dt.toString("hh:mm a"));
            temperature.setText(current_city.temperature);
            return convertView;
        }
    }

    public static class DailyListAdapter extends BaseAdapter {
        public Context contextListView;
        ArrayList<LocationWeatherInfo.DailyWeatherInfo> listModels;
        String timezone;

        DailyListAdapter(Context context,ArrayList<LocationWeatherInfo.DailyWeatherInfo> city_list,String timezone){
            contextListView = context;
            listModels = city_list;
            this.timezone = timezone;
        }
        @Override
        public int getCount() {
            return listModels.size();
        }

        @Override
        public Object getItem(int position) {
            return listModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LocationWeatherInfo.DailyWeatherInfo current_city = listModels.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) contextListView.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.daily_weather_item, parent, false);
            }
            final TextView weatherIcon = (TextView) convertView.findViewById(R.id.weather_icon);
            final TextView cityName = (TextView) convertView.findViewById(R.id.week_day);
            final TextView min_temperature = (TextView) convertView.findViewById(R.id.min_temperature);
            final TextView max_temperature = (TextView) convertView.findViewById(R.id.max_temperature);
            final Typeface weatherFont = Typeface.createFromAsset(convertView.getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
            weatherIcon .setText(Html.fromHtml(current_city.weatherIconText));
            weatherIcon.setTypeface(weatherFont);
            DateTimeZone zone = DateTimeZone.forID(timezone);
            DateTime dt = new DateTime(Long.parseLong(current_city.weekDay),zone);
            cityName.setText(dt.toString("EEEE"));
            min_temperature.setText(current_city.min_temperature);
            max_temperature.setText(current_city.max_temperature);
            return convertView;
        }
    }
}
