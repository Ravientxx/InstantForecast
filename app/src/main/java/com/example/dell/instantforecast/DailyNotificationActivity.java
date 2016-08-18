package com.example.dell.instantforecast;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Dell on 8/18/2016.
 */
public class DailyNotificationActivity extends AppCompatActivity {

    Button dailyButton;
    boolean isActivated = false;
    public static RelativeLayout morning_time_layout,morning_location_layout,afternoon_time_layout,afternoon_location_layout;
    public static TextView morning_time,morning_time_text, morning_location,morning_location_text,
            afternoon_time,afternoon_time_text, afternoon_location,afternoon_location_text;
    public static DailyNotificationActivity dailyNotificationActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_daily_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Daily notification");

        morning_location_layout = (RelativeLayout) findViewById(R.id.morning_location_picker);
        morning_time_layout = (RelativeLayout) findViewById(R.id.morning_time_picker);
        afternoon_location_layout = (RelativeLayout) findViewById(R.id.afternoon_location_picker);
        afternoon_time_layout = (RelativeLayout) findViewById(R.id.afternoon_time_picker);
        dailyButton = (Button) findViewById(R.id.daily_notification_button);
        morning_time = (TextView) findViewById(R.id.morning_time);
        morning_time_text = (TextView) findViewById(R.id.morning_time_text);
        morning_location = (TextView) findViewById(R.id.morning_location);
        morning_location_text = (TextView) findViewById(R.id.morning_location_text);
        afternoon_time = (TextView) findViewById(R.id.afternoon_time);
        afternoon_time_text = (TextView) findViewById(R.id.afternoon_time_text);
        afternoon_location = (TextView) findViewById(R.id.afternoon_location);
        afternoon_location_text = (TextView) findViewById(R.id.afternoon_location_text);
        dailyButton.setBackgroundResource(R.color.switch_button_off);
        morning_time.setTextColor(Color.GRAY);
        morning_time_text.setTextColor(Color.GRAY);
        morning_location.setTextColor(Color.GRAY);
        morning_location_text.setTextColor(Color.GRAY);
        afternoon_time.setTextColor(Color.GRAY);
        afternoon_time_text.setTextColor(Color.GRAY);
        afternoon_location.setTextColor(Color.GRAY);
        afternoon_location_text.setTextColor(Color.GRAY);
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActivated = !isActivated;
                AppSettingActivity.appSetting.isDailyNotificationActivated = isActivated;
                if(isActivated){
                    dailyButton.setBackgroundResource(R.color.switch_button_on);
                    dailyButton.setText("On");
                    morning_location_layout.setClickable(true);
                    morning_time_layout.setClickable(true);
                    afternoon_location_layout.setClickable(true);
                    afternoon_time_layout.setClickable(true);
                    morning_time.setTextColor(Color.WHITE);
                    morning_time_text.setTextColor(Color.WHITE);
                    morning_location.setTextColor(Color.WHITE);
                    morning_location_text.setTextColor(Color.WHITE);
                    afternoon_time.setTextColor(Color.WHITE);
                    afternoon_time_text.setTextColor(Color.WHITE);
                    afternoon_location.setTextColor(Color.WHITE);
                    afternoon_location_text.setTextColor(Color.WHITE);

                    morning_time_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerFragment newFragment = new TimePickerFragment();
                            newFragment.isAM = true;
                            newFragment.show(getSupportFragmentManager(), "timePicker");
                        }
                    });
                    afternoon_time_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerFragment newFragment = new TimePickerFragment();
                            newFragment.isAM = false;
                            newFragment.show(getSupportFragmentManager(), "timePicker");
                        }
                    });
                    morning_location_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LocationPickerFragment locationPickerFragment = new LocationPickerFragment();
                            locationPickerFragment.show(getSupportFragmentManager(),"locationPicker");
                        }
                    });
                }else{
                    dailyButton.setBackgroundResource(R.color.switch_button_off);
                    dailyButton.setText("Off");
                    morning_location_layout.setClickable(false);
                    morning_time_layout.setClickable(false);
                    afternoon_location_layout.setClickable(false);
                    afternoon_time_layout.setClickable(false);
                    morning_time.setTextColor(Color.GRAY);
                    morning_time_text.setTextColor(Color.GRAY);
                    morning_location.setTextColor(Color.GRAY);
                    morning_location_text.setTextColor(Color.GRAY);
                    afternoon_time.setTextColor(Color.GRAY);
                    afternoon_time_text.setTextColor(Color.GRAY);
                    afternoon_location.setTextColor(Color.GRAY);
                    afternoon_location_text.setTextColor(Color.GRAY);

                    morning_time_layout.setOnClickListener(null);
                    afternoon_time_layout.setOnClickListener(null);
                    morning_location_layout.setOnClickListener(null);
                }
            }
        });
        if(AppSettingActivity.appSetting.isDailyNotificationActivated)
            dailyButton.callOnClick();
        morning_time.setText(AppSettingActivity.appSetting.dailyMorningTime);
        morning_location.setText(AppSettingActivity.appSetting.dailyMorningLocation);
        afternoon_time.setText(AppSettingActivity.appSetting.dailyAfternoonTime);
        afternoon_location.setText(AppSettingActivity.appSetting.dailyAfternoonLocation);
        dailyNotificationActivity = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(AppSettingActivity.appSetting.isDailyNotificationActivated){
            isActivated = false;
        }
        else{
            isActivated = true;
        }
        dailyButton.callOnClick();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppSettingActivity.appSetting.dailyMorningTime = morning_time.getText().toString();
        AppSettingActivity.appSetting.dailyAfternoonTime = afternoon_time.getText().toString();
        AppSettingActivity.appSetting.dailyMorningLocation = morning_location.getText().toString();
        AppSettingActivity.appSetting.dailyAfternoonLocation= afternoon_location.getText().toString();
        AppSettingActivity.currentSetting.saveSetting();
    }

    public static class LocationPickerFragment extends DialogFragment{
        ListView mylist;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.location_picker_dialog_fragment, null, false);
            mylist = (ListView) view.findViewById(R.id.list);
            getDialog().setTitle("Choose one location");
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            ArrayList<String> locations = new ArrayList<>();
            for(LocationWeatherInfo locationWeatherInfo : MainActivity.appDataModel.city_list){
                locations.add(locationWeatherInfo.name +", " + locationWeatherInfo.country);
            }

            super.onActivityCreated(savedInstanceState);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, locations);

            mylist.setAdapter(adapter);

        }
    }
    public static class TimePickerFragment extends DialogFragment{
        boolean isAM;
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = 6;
            int minute = 0;
            if(isAM){
                hour = 6;
                minute = 0;
            }
            else{
                hour = 18;
                minute = 0;
            }
            TimePickerDialog timePickerDialog  = new TimePickerDialog(dailyNotificationActivity, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                    if(isAM){
                        if(hourOfDay > 12)
                            hourOfDay-=12;
                        if(minute >= 10)
                            morning_time.setText(hourOfDay+":"+minute+" AM");
                        else
                            morning_time.setText(hourOfDay+":0"+minute+" AM");
                    }
                    else{
                        if(hourOfDay<12)
                            hourOfDay+=12;
                        if(minute >= 10)
                            afternoon_time.setText((hourOfDay-12)+":"+minute+" PM");
                        else
                            afternoon_time.setText((hourOfDay-12)+":0"+minute+" PM");
                    }
                    System.out.println(hourOfDay);
                }
            }, hour, minute,false);
            return timePickerDialog;
        }
    }
}
