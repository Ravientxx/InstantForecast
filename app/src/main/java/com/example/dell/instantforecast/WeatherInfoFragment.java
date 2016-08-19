package com.example.dell.instantforecast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Dell on 8/13/2016.
 */
public class WeatherInfoFragment extends Fragment {

    static int BACKGROUND_IMAGE_ID;
    static TextView detailsField, currentTemperatureField, max_temperature, min_temperature, weatherIcon;
    static ImageView max_img, min_img;
    static ScrollView mainScrollView;
    static int screenHeight;
    static ArrayList<Bitmap> blurred_background_image;
    static Bitmap background_image;
    static GoogleMap mGoogleMap;
    static RelativeLayout current_condition_layout;
    static WeatherInfoFragment frag;
    SupportMapFragment mSupportMapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_info_fragment, container, false);
        mSupportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_view);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map_view, mSupportMapFragment).commit();
        }
        if (mSupportMapFragment != null)
        {
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

        DisplayMetrics displaymetrics = new DisplayMetrics();
        MainActivity.mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;

        current_condition_layout = (RelativeLayout) view.findViewById(R.id.current_condition_screen);
        blurred_background_image = new ArrayList<>();

        current_condition_layout = (RelativeLayout) view.findViewById(R.id.current_condition_screen);
        mainScrollView = (ScrollView) view.findViewById(R.id.weather_info_scroll_view);
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
    }

    static public void loadWeatherInfo(final String locationId, final double Lat, final double Lon,final boolean displayWeather) {
        if (GeneralUtils.isOnline()) {
            OpenWeatherMapApiManager.GetWeatherInfoTask getCurrentWeatherTask = new OpenWeatherMapApiManager.GetWeatherInfoTask(new OpenWeatherMapApiManager.AsyncResponse() {
                public void processFinish(final LocationWeatherInfo current_locationWeatherInfo) {
                    GoogleTimezoneAPI.getDateTimeByLocationTask getDateTimeTask = new GoogleTimezoneAPI.getDateTimeByLocationTask(new GoogleTimezoneAPI.AsyncResponse() {
                        @Override
                        public void processFinish(String date) {
                            MainActivity.city_time_textview.setText(date);

                            current_locationWeatherInfo.updateTime = date;
                            current_locationWeatherInfo.id = locationId;
                            current_locationWeatherInfo.lat = Lat;
                            current_locationWeatherInfo.lon = Lon;
                            MainActivity.appDataModel.current_city = current_locationWeatherInfo;

                            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                    new LatLng(MainActivity.appDataModel.current_city.lat,MainActivity.appDataModel.current_city.lon))
                                    .zoom(8)
                                    .build();

                            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
                            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    frag.startActivity(new Intent(frag.getContext(),MapLayerActivity.class));
                                }
                            });
                            if(!locationId.equals("get_current_location")){
                                int locationIndex = -1;
                                for (int i = 0; i < MainActivity.appDataModel.city_list.size(); i++) {
                                    if (current_locationWeatherInfo.id.equals(MainActivity.appDataModel.city_list.get(i).id)) {
                                        locationIndex = i;
                                        break;
                                    }
                                }
                                if (locationIndex == -1 || locationId.equals("add_location_map")) {
                                    MainActivity.appDataModel.city_list.add(current_locationWeatherInfo);
                                    if(EditLocationActivity.editLocationListAdapter != null)
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

                            if(displayWeather){
                                displayWeatherInfo(current_locationWeatherInfo);

                                BACKGROUND_IMAGE_ID = current_locationWeatherInfo.conditionId;
                                blurred_background_image.clear();
                                Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                                MainActivity.mainActivity.background_image_view.setImageBitmap(bitmap);
                                blurred_background_image.add(GeneralUtils.blur(bitmap, 5f));
                                bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                                blurred_background_image.add(GeneralUtils.blur(bitmap, 15f));
                                bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                                blurred_background_image.add(GeneralUtils.blur(bitmap, 25f));
                            }
                        }
                    });
                    getDateTimeTask.execute(String.valueOf(Lat), String.valueOf(Lon));
                }
            });
            getCurrentWeatherTask.execute(String.valueOf(Lat), String.valueOf(Lon));
        } else {
            Toast networkError = Toast.makeText(MainActivity.mainActivity, "Can't connect to internet!!", Toast.LENGTH_LONG);
            networkError.show();
        }
    }

    static public void displayWeatherInfo(LocationWeatherInfo location){
        //Current condition
        MainActivity.city_name_textview.setText(location.name + "," + location.country);
        detailsField.setText(location.description);
        currentTemperatureField.setText(location.temperature);
        weatherIcon.setText(Html.fromHtml(location.weatherIconText));
        max_img.setImageResource(R.drawable.ic_vertical_align_top_white_24dp);
        min_img.setImageResource(R.drawable.ic_vertical_align_bottom_white_24dp);
        max_temperature.setText("30°");
        min_temperature.setText("24°");

        //Hourly


        //Daily
        //humidity_field.setText("Humidity: "+weather_humidity);
        //pressure_field.setText("Pressure: "+weather_pressure);

    }
}
