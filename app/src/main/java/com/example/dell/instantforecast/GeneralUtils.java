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

/**
 * Created by Dell on 8/17/2016.
 */
public class GeneralUtils {

    static String APP_SETTING_FILENAME = "AppSettingData";
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
            System.out.println(string1);
        } catch (FileNotFoundException e) {
            appSettingModel = new AppSettingModel();
            appSettingModel.isDailyNotificationActivated = false;
            appSettingModel.isOngoingNotificationActivated = false;
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

    static public double Celsius2Fahrenheit (double C){
        double F = C * 1.8 + 32;
        return F;
    }

    static public double Fahrenheit2Celsius (double F){
        double C = (F - 32) / 1.8;
        return C;
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
