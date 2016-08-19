package com.example.dell.instantforecast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

/**
 * Created by Dell on 7/23/2016.
 */
public class AppSettingActivity extends AppCompatActivity {
    static AppSettingActivity currentSetting;
    static boolean usingCelcius;
    static public int MORNING_NOTIFICATION_ID = 1570;
    static public int AFTERNOON_NOTIFICATION_ID = 1571;
    final int ONGOING_NOTIFICATION_ID = 2205;

    Button C_Button,F_Button;
    static public AppSettingModel appSettingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_app_setting);

        currentSetting = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Settings");

        C_Button = (Button) findViewById(R.id.celcius_button);
        F_Button = (Button) findViewById(R.id.fahrenheit_button);
        if (C_Button != null && F_Button != null) {
            C_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    C_Button.setBackgroundColor(getResources().getColor(R.color.switch_button_on));
                    F_Button.setBackgroundColor(getResources().getColor(R.color.switch_button_off));
                    usingCelcius = true;
                    appSettingModel.Unit = "C";
                }
            });
            F_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    C_Button.setBackgroundColor(getResources().getColor(R.color.switch_button_off));
                    F_Button.setBackgroundColor(getResources().getColor(R.color.switch_button_on));
                    usingCelcius = false;
                    appSettingModel.Unit = "F";
                }
            });
        }
        TextView dailyNotification = (TextView) findViewById(R.id.daily_notification);
        if (dailyNotification != null) {
            dailyNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AppSettingActivity.this,DailyNotificationSettingActivity.class));
                }
            });
        }

        TextView ongoingNotification = (TextView) findViewById(R.id.ongoing_notification);
        if (ongoingNotification != null) {
            ongoingNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activateOngoingNotification();
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        appSettingModel = GeneralUtils.loadAppSetting(this);
        if(appSettingModel.Unit.equals("C")){
            C_Button.callOnClick();
        }
        else{
            F_Button.callOnClick();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GeneralUtils.saveAppSetting(this,appSettingModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //user clicked a menu-item from ActionBar
        int id = item.getItemId();
        switch (id) {
            default:
                finish();
                break;
        }
        return false;
    }

    public void activateOngoingNotification() {
        Intent intent = new Intent(AppSettingActivity.this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(AppSettingActivity.this, (int) System.currentTimeMillis(), intent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.weather_notification);
        Drawable iCon = new IconicsDrawable(AppSettingActivity.this)
                .icon(WeatherIcons.Icon.wic_cloudy)
                .color(Color.WHITE)
                .sizeDp(50);
        remoteViews.setImageViewBitmap(R.id.weather_image, GeneralUtils.drawableToBitmap(iCon));
        remoteViews.setTextViewText(R.id.city_name, "Ho Chi Minh");
        remoteViews.setTextViewText(R.id.temperature_field, "29°");
        Notification n = new NotificationCompat.Builder(AppSettingActivity.this)
                .setContent(remoteViews)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //notificationManager.notify(DAILY_NOTIFICATION_ID, n);
    }
}
