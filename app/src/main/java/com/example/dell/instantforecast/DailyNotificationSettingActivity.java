package com.example.dell.instantforecast;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
public class DailyNotificationSettingActivity extends AppCompatActivity {

    Button dailyButton;
    boolean isActivated = false;
    public static RelativeLayout morning_time_layout, morning_location_layout, afternoon_time_layout, afternoon_location_layout;
    public static TextView morning_time, morning_time_text, morning_location, morning_location_text,
            afternoon_time, afternoon_time_text, afternoon_location, afternoon_location_text;
    public static Button btnClearSelection;
    public static DailyNotificationSettingActivity dailyNotificationSettingActivity;
    static AlarmService alarmService;
    static AppSettingModel appSettingModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_daily_notification);

        dailyNotificationSettingActivity = this;
        alarmService = new AlarmService();
        appSettingModel = GeneralUtils.loadAppSetting(this);
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
        btnClearSelection = (Button) findViewById(R.id.btnClearSelection);
        btnClearSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morning_time.setText("");
                afternoon_time.setText("");
                morning_location.setText("");
                afternoon_location.setText("");
                SaveSetting();
                alarmService.stopAlarm();
            }
        });
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActivated = !isActivated;
                appSettingModel.isDailyNotificationActivated = isActivated;
                onActivateNotification(isActivated);
                if(isActivated){
                    alarmService.startAlarm();
                }
                else{
                    alarmService.stopAlarm();
                }
            }
        });
        morning_time.setText(appSettingModel.dailyMorningTime);
        afternoon_time.setText(appSettingModel.dailyAfternoonTime);

        int i1 = -1,i2 = -1;
        String name1 = "";
        String name2 = "";
        if(appSettingModel.dailyMorningLocation.length() > 0){
            name1 =  appSettingModel.dailyMorningLocation.substring(0,appSettingModel.dailyMorningLocation.indexOf(","));
        }
        if(appSettingModel.dailyAfternoonLocation.length() > 0){
            name2 = appSettingModel.dailyAfternoonLocation.substring(0,appSettingModel.dailyAfternoonLocation.indexOf(","));
        }
        for(int i = 0 ; i < MainActivity.appDataModel.city_list.size(); i++){
            if(name1.equals(MainActivity.appDataModel.city_list.get(i).name)){
                i1 = i;
            }
            if(name2.equals(MainActivity.appDataModel.city_list.get(i).name)){
                i2 = i;
            }
        }
        if(i1 == -1){
            morning_location.setText("");
            alarmService.stopAlarm();
        }else{
            morning_location.setText(appSettingModel.dailyMorningLocation);
        }
        if(i2 == -1){
            afternoon_location.setText("");
            alarmService.stopAlarm();
        }else{
            afternoon_location.setText(appSettingModel.dailyAfternoonLocation);
        }
    }

    public void onActivateNotification(boolean isActivated){
        if (isActivated) {
            dailyButton.setBackgroundResource(R.color.switch_button_on);
            btnClearSelection.setBackgroundResource(R.color.switch_button_on);
            btnClearSelection.setEnabled(true);
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
                    locationPickerFragment.isAm = true;
                    locationPickerFragment.show(getSupportFragmentManager(), "locationPicker");
                }
            });
            afternoon_location_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocationPickerFragment locationPickerFragment = new LocationPickerFragment();
                    locationPickerFragment.isAm = false;
                    locationPickerFragment.show(getSupportFragmentManager(), "locationPicker");
                }
            });
        } else {
            dailyButton.setBackgroundResource(R.color.switch_button_off);
            btnClearSelection.setBackgroundResource(R.color.switch_button_off);
            btnClearSelection.setEnabled(false);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (appSettingModel.isDailyNotificationActivated) {
            isActivated = true;
        } else {
            isActivated = false;
        }
        onActivateNotification(isActivated);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appSettingModel.dailyMorningTime = morning_time.getText().toString();
        appSettingModel.dailyAfternoonTime = afternoon_time.getText().toString();
        appSettingModel.dailyMorningLocation = morning_location.getText().toString();
        appSettingModel.dailyAfternoonLocation = afternoon_location.getText().toString();
       GeneralUtils.saveAppSetting(this,appSettingModel);
    }

    public static class LocationPickerFragment extends DialogFragment {
        ListView mylist;
        boolean isAm;
        LocationPickerFragment locationPickerFragment;

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
            final ArrayList<String> locations = new ArrayList<>();
            for (LocationWeatherInfo locationWeatherInfo : MainActivity.appDataModel.city_list) {
                locations.add(locationWeatherInfo.name + ", " + locationWeatherInfo.country);
            }

            super.onActivityCreated(savedInstanceState);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, locations);

            mylist.setAdapter(adapter);

            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isAm) {
                        morning_location.setText(locations.get(position));
                        getDialog().dismiss();
                    } else {
                        afternoon_location.setText(locations.get(position));
                        getDialog().dismiss();
                    }
                    SaveSetting();
                    alarmService.startAlarm();
                }
            });
        }
    }

    public static class TimePickerFragment extends DialogFragment {
        boolean isAM;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = 6;
            int minute = 0;
            if (isAM) {
                hour = 6;
                minute = 0;
            } else {
                hour = 18;
                minute = 0;
            }
            TimePickerDialog timePickerDialog = new TimePickerDialog(dailyNotificationSettingActivity, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                    if (isAM) {
                        if (hourOfDay > 12)
                            hourOfDay -= 12;
                        if (minute >= 10)
                            morning_time.setText(hourOfDay + ":" + minute + " AM");
                        else
                            morning_time.setText(hourOfDay + ":0" + minute + " AM");
                    } else {
                        if (hourOfDay < 12)
                            hourOfDay += 12;
                        if (minute >= 10)
                            afternoon_time.setText((hourOfDay - 12) + ":" + minute + " PM");
                        else
                            afternoon_time.setText((hourOfDay - 12) + ":0" + minute + " PM");
                    }
                    SaveSetting();
                    alarmService.startAlarm();
                }
            }, hour, minute, false);
            return timePickerDialog;
        }
    }

    public static void SaveSetting(){
        appSettingModel.dailyMorningTime = morning_time.getText().toString();
        appSettingModel.dailyAfternoonTime = afternoon_time.getText().toString();
        appSettingModel.dailyMorningLocation = morning_location.getText().toString();
        appSettingModel.dailyAfternoonLocation = afternoon_location.getText().toString();
        GeneralUtils.saveAppSetting(dailyNotificationSettingActivity,appSettingModel);
    }
    public class AlarmService {
        public void startAlarm() {
            stopAlarm();
            Calendar calendar = Calendar.getInstance();
            AlarmManager am = (AlarmManager) dailyNotificationSettingActivity.getSystemService(Context.ALARM_SERVICE);

            String morningTime = morning_time.getText().toString();
            String afternoonTime= afternoon_time.getText().toString();
            String morningLocation = morning_location.getText().toString();
            String afternoonLocation = afternoon_location.getText().toString();

            if(morningTime.length() != 0 && morningLocation.length() != 0){
                int minute,hour;
                morningTime = morningTime.substring(0,morningTime.length()-3);
                if(morningTime.length() == 5){
                    hour = Integer.parseInt(morningTime.substring(0,2));
                    minute = Integer.parseInt(morningTime.substring(3));
                }else{
                    hour = Integer.parseInt(morningTime.substring(0,1));
                    minute = Integer.parseInt(morningTime.substring(2));
                }
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                Intent morningIntent = new Intent(dailyNotificationSettingActivity, MorningNotificationReceiver.class);
                String name = morningLocation.substring(0,morningLocation.indexOf(","));
                LocationWeatherInfo locationWeatherInfo = GeneralUtils.getLocationwithName(name);
                morningIntent.putExtra("locationLat",locationWeatherInfo.lat);
                morningIntent.putExtra("locationLon",locationWeatherInfo.lon);
                morningIntent.putExtra("locationId",locationWeatherInfo.id);
                morningIntent.putExtra("locationName",name);
                PendingIntent morningSender = PendingIntent.getBroadcast(dailyNotificationSettingActivity, 2205, morningIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, morningSender);
                System.out.println("Morning alarm started : " + name);
            }

            if(afternoonTime.length() != 0 && afternoonLocation.length() != 0){
                int minute,hour;
                afternoonTime = afternoonTime.substring(0,afternoonTime.length()-3);
                if(afternoonTime.length() == 5){
                    hour = Integer.parseInt(afternoonTime.substring(0,2)) + 12;
                    minute = Integer.parseInt(afternoonTime.substring(3));
                }else{
                    hour = Integer.parseInt(afternoonTime.substring(0,1)) + 12;
                    minute = Integer.parseInt(afternoonTime.substring(2));
                }
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                Intent afternoonIntent = new Intent(dailyNotificationSettingActivity, AfternoonNotificationReceiver.class);
                String name = afternoonLocation.substring(0,afternoonLocation.indexOf(","));
                LocationWeatherInfo locationWeatherInfo = GeneralUtils.getLocationwithName(name);
                afternoonIntent.putExtra("locationLat",locationWeatherInfo.lat);
                afternoonIntent.putExtra("locationLon",locationWeatherInfo.lon);
                afternoonIntent.putExtra("locationId",locationWeatherInfo.id);
                afternoonIntent.putExtra("locationName",name);
                PendingIntent afternoonSender = PendingIntent.getBroadcast(dailyNotificationSettingActivity, 2206, afternoonIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, afternoonSender);
                System.out.println("Afternoon alarm started : " + name);
            }
        }

        public void stopAlarm() {
            AlarmManager alarmManager = (AlarmManager) dailyNotificationSettingActivity.getSystemService(Context.ALARM_SERVICE);

            Intent morningIntent = new Intent(dailyNotificationSettingActivity, MorningNotificationReceiver.class);
            PendingIntent morningSender = PendingIntent.getBroadcast(dailyNotificationSettingActivity, 2205, morningIntent, 0);
            alarmManager.cancel(morningSender);
            System.out.println("Morning Alarm stopped");

            Intent afternoonIntent = new Intent(dailyNotificationSettingActivity, AfternoonNotificationReceiver.class);
            PendingIntent afternoonSender = PendingIntent.getBroadcast(dailyNotificationSettingActivity, 2206, afternoonIntent, 0);
            alarmManager.cancel(afternoonSender);
            System.out.println("Afternoon Alarm stopped");
        }
    }
}
