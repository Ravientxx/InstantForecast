package com.example.dell.instantforecast;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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
    static Typeface weatherFont;
    static ScrollView mainScrollView;
    static int screenHeight;
    static ArrayList<Bitmap> blurred_background_image;
    static Bitmap background_image;
    RelativeLayout mapl, clickMap;
    static GoogleMap map;
    MapView mapView;
    Button clickl;
    static RelativeLayout current_condition_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.weather_info_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weatherFont = Typeface.createFromAsset(MainActivity.mainActivity.getAssets(), "fonts/weathericons-regular-webfont.ttf");
        detailsField = (TextView) view.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) view.findViewById(R.id.current_temperature);
        weatherIcon = (TextView) view.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        max_img = (ImageView) view.findViewById(R.id.max_icon);
        max_temperature = (TextView) view.findViewById(R.id.max_temperature);
        min_img = (ImageView) view.findViewById(R.id.min_icon);
        min_temperature = (TextView) view.findViewById(R.id.min_temperature);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        MainActivity.mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        clickl = (Button)view.findViewById(R.id.button);
        clickl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });
        mapView = (MapView)view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

            }
        });

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

    static public void loadWeatherInfo(final String locationId, final String Lat, final String Lon) {
        if (GeneralUtils.isOnline()) {
            OpenWeatherMapApiManager.placeIdTask getCurrentWeatherTask = new OpenWeatherMapApiManager.placeIdTask(new OpenWeatherMapApiManager.AsyncResponse() {
                public void processFinish(String weather_country, String weather_city, String weather_description, String weather_temperature, String weather_humidity,
                                          String weather_pressure, String weather_updatedOn, String weather_iconText, int conditionId, String sun_rise) {
                    LocationWeatherInfo current_locationWeatherInfo = new LocationWeatherInfo(
                            locationId,
                            weather_city,
                            weather_country,
                            weather_iconText,
                            weather_temperature,
                            weather_description,
                            Lat,
                            Lon
                    );

                    MainActivity.appDataModel.current_city = current_locationWeatherInfo;

                    //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(MainActivity.appDataModel.current_city.lat), Double.parseDouble(MainActivity.appDataModel.current_city.lon)), 13);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(MainActivity.appDataModel.current_city.lat),
                            Double.parseDouble(MainActivity.appDataModel.current_city.lon)))
                            .zoom(15)
                            .tilt(40)
                            .build();

                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    map.getUiSettings().setAllGesturesEnabled(true);
                    map.getUiSettings().setZoomControlsEnabled(true);
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
                            EditLocationActivity.editLocationListAdapter.notifyDataSetChanged();
                            current_locationWeatherInfo.id = "added_location_map";
                        } else {// Update City Info
                            MainActivity.appDataModel.city_list.remove(locationIndex);
                            MainActivity.appDataModel.city_list.add(locationIndex, current_locationWeatherInfo);
                        }
                        MainActivity.navigationMenuListAdapter.notifyDataSetChanged();
                    }


                    current_condition_layout.setMinimumHeight(screenHeight - MainActivity.toolbar.getHeight());

                    displayWeatherInfo(current_locationWeatherInfo);

                    BACKGROUND_IMAGE_ID = conditionId;
                    blurred_background_image.clear();
                    Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                    MainActivity.mainActivity.background_image_view.setImageBitmap(bitmap);
                    blurred_background_image.add(GeneralUtils.blur(bitmap, 5f));
                    bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                    blurred_background_image.add(GeneralUtils.blur(bitmap, 15f));
                    bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                    blurred_background_image.add(GeneralUtils.blur(bitmap, 25f));
                }
            });
            getCurrentWeatherTask.execute(Lat, Lon);

            GoogleTimezoneAPI.getDateTimeByLocationTask getDateTimeTask = new GoogleTimezoneAPI.getDateTimeByLocationTask(new GoogleTimezoneAPI.AsyncResponse() {
                @Override
                public void processFinish(String date) {
                    MainActivity.city_time_textview.setText(date);
                }
            });
            getDateTimeTask.execute(Lat, Lon);
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
