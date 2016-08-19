package com.example.dell.instantforecast;

import android.os.AsyncTask;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dell on 7/23/2016.
 */
public class GoogleTimezoneAPI {

    private static final String API_URL = "https://maps.googleapis.com/maps/api/timezone/json?location=%s,%s&timestamp=0&key=%s";
    private static final String API_KEY = "AIzaSyBNUiwT7NkDS9Neeq0-0AokMCs4ajndisU";
    static public String current_timezone;
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public static class getDateTimeByLocationTask extends AsyncTask<String, Void, String> {

        public AsyncResponse delegate = null;//Call back interface

        public getDateTimeByLocationTask (AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }

        @Override
        protected String doInBackground(String... params) {

            String jsonWeather = null;
            jsonWeather = getTimezoneID(params[0], params[1]);
            return jsonWeather;
        }

        @Override
        protected void onPostExecute(String timezone) {
            if (timezone != null) {
                current_timezone = timezone;
                DateTimeZone zone = DateTimeZone.forID(timezone);
                DateTime dateTime = new DateTime(zone);
                delegate.processFinish(dateTime.toString("EEE, d MMM yyyy, HH:mm a"));
            }
        }
    }


    static public String getTimezoneID(String Lat, String Lon) {
        String result = "";
        try {
            URL url = new URL(String.format(API_URL, Lat, Lon, API_KEY));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            result  = data.getString("timeZoneId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
