package com.example.dell.instantforecast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dell on 8/17/2016.
 */
public class GeneralUtils {

    static String APP_SETTING_FILENAME = "AppSettingData";
    public static String setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch (id) {
                case 2:
                    icon = "&#xf01e;";
                    break;
                case 3:
                    icon = "&#xf01c;";
                    break;
                case 7:
                    icon = "&#xf014;";
                    break;
                case 8:
                    icon = "&#xf013;";
                    break;
                case 6:
                    icon = "&#xf01b;";
                    break;
                case 5:
                    icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    public static String windDegreeToDirection(String degree){
        if(degree == null){
            return null;
        }
        String direction [] = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S",
                "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int deg = Integer.parseInt(degree.substring(0,degree.length()-1));
        int index = ((deg+(360/16)/2)%360)/(360/16);
        if(index == 16)
            index--;
        return direction[index];
    }

    public static LocationWeatherInfo getLocationwithName(String name){
        LocationWeatherInfo locationWeatherInfo = null;
        for(int i = 0 ; i < MainActivity.appDataModel.city_list.size() ; i++){
            if(name.equals(MainActivity.appDataModel.city_list.get(i).name)){
                locationWeatherInfo = MainActivity.appDataModel.city_list.get(i);
                break;
            }
        }
        return locationWeatherInfo;
    }

    public static AppSettingModel loadAppSetting(Context context){
        String string1 = "";
        AppSettingModel appSettingModel = null;
        try {
            FileInputStream inputStream = context.openFileInput(APP_SETTING_FILENAME);
            InputStreamReader fin = new InputStreamReader(inputStream, "UTF-8");
            int i = 0;
            while ((i = fin.read()) != -1) {
                string1 += (char) i;
            }
            fin.close();
            appSettingModel = new Gson().fromJson(string1, AppSettingModel.class);
            if(appSettingModel.dailyMorningLocation == null)
                appSettingModel.dailyMorningLocation = "";
            if(appSettingModel.dailyAfternoonLocation == null)
                appSettingModel.dailyAfternoonLocation = "";
            System.out.println(string1);
        } catch (FileNotFoundException e) {
            appSettingModel = new AppSettingModel();
            appSettingModel.isDailyNotificationActivated = false;
            appSettingModel.isOngoingNotificationActivated = false;
            appSettingModel.dailyMorningLocation = "";
            appSettingModel.dailyAfternoonLocation = "";
            appSettingModel.Unit = "C";
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return appSettingModel;
        }
    }

    public static void saveAppSetting(Context context,AppSettingModel appSettingModel){
        String string = new Gson().toJson(appSettingModel, AppSettingModel.class);
        try {
            FileOutputStream out = context.openFileOutput(APP_SETTING_FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter fos = new OutputStreamWriter(out, "UTF-8");
            fos.write(string);
            fos.close();
            printDebug("Saving App Setting");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public LocationWeatherInfo locationWithCelsius(LocationWeatherInfo location){
        LocationWeatherInfo locationWithFahrenheit = new LocationWeatherInfo();
        locationWithFahrenheit.updateTime = location.updateTime;
        locationWithFahrenheit.timeZone = location.timeZone;
        locationWithFahrenheit.id = location.id;
        locationWithFahrenheit.country = location.country;
        locationWithFahrenheit.lat = location.lat;
        locationWithFahrenheit.lon = location.lon;
        locationWithFahrenheit.weatherIconText = location.weatherIconText;
        locationWithFahrenheit.name = location.name;
        locationWithFahrenheit.mainGroup = location.mainGroup;
        locationWithFahrenheit.description = location.description;
        locationWithFahrenheit.humidity = location.humidity;
        locationWithFahrenheit.pressure = location.pressure;
        locationWithFahrenheit.windSpeed = location.windSpeed;
        locationWithFahrenheit.windDegree = location.windDegree;
        locationWithFahrenheit.sunRise = location.sunRise;
        locationWithFahrenheit.sunSet = location.sunSet;
        locationWithFahrenheit.conditionId = location.conditionId;

        locationWithFahrenheit.temperature = Fahrenheit2Celsius(location.temperature);
        locationWithFahrenheit.min_temperature = Fahrenheit2Celsius(location.min_temperature);
        locationWithFahrenheit.max_temperature = Fahrenheit2Celsius(location.max_temperature);
        locationWithFahrenheit.hourlyWeatherList = new ArrayList<>();
        for(int i = 0 ; i < location.hourlyWeatherList.size() ; i++) {
            LocationWeatherInfo.HourlyWeatherInfo hourlyWeatherInfo = location.hourlyWeatherList.get(i);
            hourlyWeatherInfo.temperature = GeneralUtils.Fahrenheit2Celsius(hourlyWeatherInfo.temperature);
            locationWithFahrenheit.hourlyWeatherList.add(hourlyWeatherInfo);
        }
        locationWithFahrenheit.dailyWeatherList = new ArrayList<>();
        for(int i = 0 ; i < location.dailyWeatherList.size() ; i++){
            LocationWeatherInfo.DailyWeatherInfo dailyWeatherInfo = location.dailyWeatherList.get(i);
            dailyWeatherInfo.max_temperature = GeneralUtils.Fahrenheit2Celsius(dailyWeatherInfo.max_temperature);
            dailyWeatherInfo.min_temperature = GeneralUtils.Fahrenheit2Celsius(dailyWeatherInfo.min_temperature);
            locationWithFahrenheit.dailyWeatherList.add(dailyWeatherInfo);
        }
        return locationWithFahrenheit;
    }

    static  public LocationWeatherInfo locationWithFahrenheit(LocationWeatherInfo location){
        LocationWeatherInfo locationWithFahrenheit = new LocationWeatherInfo();
        locationWithFahrenheit.updateTime = location.updateTime;
        locationWithFahrenheit.timeZone = location.timeZone;
        locationWithFahrenheit.id = location.id;
        locationWithFahrenheit.country = location.country;
        locationWithFahrenheit.lat = location.lat;
        locationWithFahrenheit.lon = location.lon;
        locationWithFahrenheit.weatherIconText = location.weatherIconText;
        locationWithFahrenheit.name = location.name;
        locationWithFahrenheit.mainGroup = location.mainGroup;
        locationWithFahrenheit.description = location.description;
        locationWithFahrenheit.humidity = location.humidity;
        locationWithFahrenheit.pressure = location.pressure;
        locationWithFahrenheit.windSpeed = location.windSpeed;
        locationWithFahrenheit.windDegree = location.windDegree;
        locationWithFahrenheit.sunRise = location.sunRise;
        locationWithFahrenheit.sunSet = location.sunSet;
        locationWithFahrenheit.conditionId = location.conditionId;

        locationWithFahrenheit.temperature = Celsius2Fahrenheit(location.temperature);
        locationWithFahrenheit.min_temperature = Celsius2Fahrenheit(location.min_temperature);
        locationWithFahrenheit.max_temperature = Celsius2Fahrenheit(location.max_temperature);
        locationWithFahrenheit.hourlyWeatherList = new ArrayList<>();
        for(int i = 0 ; i < location.hourlyWeatherList.size() ; i++) {
            LocationWeatherInfo.HourlyWeatherInfo hourlyWeatherInfo = location.hourlyWeatherList.get(i);
            hourlyWeatherInfo.temperature = GeneralUtils.Celsius2Fahrenheit(hourlyWeatherInfo.temperature);
            locationWithFahrenheit.hourlyWeatherList.add(hourlyWeatherInfo);
        }
        locationWithFahrenheit.dailyWeatherList = new ArrayList<>();
        for(int i = 0 ; i < location.dailyWeatherList.size() ; i++){
            LocationWeatherInfo.DailyWeatherInfo dailyWeatherInfo = location.dailyWeatherList.get(i);
            dailyWeatherInfo.max_temperature = GeneralUtils.Celsius2Fahrenheit(dailyWeatherInfo.max_temperature);
            dailyWeatherInfo.min_temperature = GeneralUtils.Celsius2Fahrenheit(dailyWeatherInfo.min_temperature);
            locationWithFahrenheit.dailyWeatherList.add(dailyWeatherInfo);
        }
        return locationWithFahrenheit;
    }

    static public String Celsius2Fahrenheit (String oldTemperature){
        double oldTemp = Double.parseDouble(oldTemperature.substring(0,oldTemperature.length()-1));
        double newTemp = oldTemp * 1.8 + 32;
        String newTemperature = String.format("%.0f",newTemp) + "°";
        return newTemperature;
    }

    static public String Fahrenheit2Celsius (String oldTemperature){
        double oldTemp = Double.parseDouble(oldTemperature.substring(0,oldTemperature.length()-1));
        double newTemp = (oldTemp - 32) / 1.8;
        String newTemperature = String.format("%.0f",newTemp) + "°";
        return newTemperature;
    }

    static public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    static public Bitmap blur(Bitmap image,float radius) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(MainActivity.mainActivity);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    static public void printDebug(String str){
        System.out.println("");
        System.out.println("");
        System.out.println(str);
        System.out.println("");
        System.out.println("");
    }
}
