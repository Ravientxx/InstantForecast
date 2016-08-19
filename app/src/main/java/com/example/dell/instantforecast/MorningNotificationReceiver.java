package com.example.dell.instantforecast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

/**
 * Created by Dell on 8/19/2016.
 */
public class MorningNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        if (GeneralUtils.isOnline(context)) {
            final double Lat = intent.getDoubleExtra("locationLat", 0);
            final double Lon = intent.getDoubleExtra("locationLon", 0);
            final String locationId = intent.getStringExtra("locationId");
            final String locationName = intent.getStringExtra("locationName");
            OpenWeatherMapApiManager.GetCurrentWeatherConditionTask getCurrentWeatherTask = new OpenWeatherMapApiManager.GetCurrentWeatherConditionTask(
                    new OpenWeatherMapApiManager.AsyncResponse() {
                        public void processFinish(final LocationWeatherInfo current_locationWeatherInfo) {
                            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_notification);
                            Drawable iCon = new IconicsDrawable(context)
                                    .icon(WeatherIcons.Icon.wic_cloudy)
                                    .color(Color.WHITE)
                                    .sizeDp(50);
                            AppSettingModel appSettingModel = GeneralUtils.loadAppSetting(context);
                            String temperature = current_locationWeatherInfo.temperature;
                            if(appSettingModel != null){
                                if(appSettingModel.Unit.equals("F")){
                                    double oldTemp =Double.parseDouble(temperature.substring(0,temperature.length()-1));
                                    temperature = String.format("%.0f", GeneralUtils.Celsius2Fahrenheit(oldTemp)) + "°";
                                }
                            }
                            remoteViews.setImageViewBitmap(R.id.weather_image, GeneralUtils.drawableToBitmap(iCon));
                            remoteViews.setTextViewText(R.id.city_name, current_locationWeatherInfo.name);
                            remoteViews.setTextViewText(R.id.temperature_field, temperature);
                            remoteViews.setTextViewText(R.id.details_field, "Morning " + current_locationWeatherInfo.description);


                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_cloud_white_24dp)
                                    .setContent(remoteViews)
                                    .setSound(alarmSound)
                                    .setAutoCancel(true);
                            Intent pintent = new Intent(context, MainActivity.class);
                            pintent.putExtra("loadFromNotification",true);
                            pintent.putExtra("locationLatFromNotification",Lat);
                            pintent.putExtra("locationLonFromNotification",Lon);
                            pintent.putExtra("locationIdFromNotification",locationId);
                            PendingIntent pi = PendingIntent.getActivity(context, 1571, pintent, Intent.FILL_IN_ACTION);
                            mBuilder.setContentIntent(pi);
                            NotificationManager mNotificationManager =
                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(AppSettingActivity.MORNING_NOTIFICATION_ID, mBuilder.build());

                            System.out.println("Morning Notification Started : " + locationName);
                        }
                    }
            );
            getCurrentWeatherTask.execute(String.valueOf(Lat), String.valueOf(Lon));
        }
        wl.release();
    }
}
