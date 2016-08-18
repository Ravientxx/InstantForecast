package com.example.dell.instantforecast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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

import com.google.gson.Gson;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Dell on 7/23/2016.
 */
public class AppSettingActivity extends AppCompatActivity {

    final String FILENAME = "AppSettingData";
    static AppSettingActivity currentSetting;
    static boolean usingCelcius;
    final int DAILY_NOTIFICATION_ID = 1507;
    final int ONGOING_NOTIFICATION_ID = 2205;

    Button C_Button,F_Button;
    static AppSetting appSetting;
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
                    appSetting.Unit = "C";
                }
            });
            F_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    C_Button.setBackgroundColor(getResources().getColor(R.color.switch_button_off));
                    F_Button.setBackgroundColor(getResources().getColor(R.color.switch_button_on));
                    usingCelcius = false;
                    appSetting.Unit = "F";
                }
            });
        }
        TextView dailyNotification = (TextView) findViewById(R.id.daily_notification);
        if (dailyNotification != null) {
            dailyNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    NotificationManager notificationManager =
//                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    notificationManager.cancel(DAILY_NOTIFICATION_ID);
                    startActivity(new Intent(AppSettingActivity.this,DailyNotificationActivity.class));
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
        loadSetting();
        if(appSetting.Unit.equals("C")){
            C_Button.callOnClick();
        }
        else{
            F_Button.callOnClick();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSetting();
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

        notificationManager.notify(DAILY_NOTIFICATION_ID, n);
    }

    public void saveSetting(){
        String string = new Gson().toJson(appSetting, AppSetting.class);
        try {
            FileOutputStream out = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter fos = new OutputStreamWriter(out, "UTF-8");
            fos.write(string);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadSetting(){
        String string1 = "";
        try {
            FileInputStream inputStream = openFileInput(FILENAME);
            InputStreamReader fin = new InputStreamReader(inputStream, "UTF-8");
            int i = 0;
            while ((i = fin.read()) != -1) {
                string1 += (char) i;
            }
            fin.close();
            appSetting = new Gson().fromJson(string1, AppSetting.class);
            System.out.println(string1);
        } catch (FileNotFoundException e) {
            appSetting = new AppSetting();
            appSetting.isDailyNotificationActivated = false;
            appSetting.isOngoingNotificationActivated = false;
            appSetting.Unit = "C";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
