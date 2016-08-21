package com.example.dell.instantforecast;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class OpenWeatherMapApiManager {

    private static final String CURRENT_WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";
    private static final String HOURLY_WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&units=metric&cnt=12";
    private static final String DAILY_WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%f&lon=%f&units=metric&cnt=7";
    private static final String OPEN_WEATHER_MAP_API = "2a812268824b5687716cb9628b71cf2d";



    public static int setConditionId(String Id) {
        int conditionId = 0;
        switch (Id) {
            case "01d":
                conditionId = R.drawable.clear_sky_day;
                break;
            case "01n":
                conditionId = R.drawable.clear_sky_night;
                break;
            case "02d":
                conditionId = R.drawable.few_cloud_day;
                break;
            case "02n":
                conditionId = R.drawable.few_cloud_night;
                break;
            case "03d":
            case "04d":
                conditionId = R.drawable.scatter_day;
                break;
            case "03n":
            case "04n":
                conditionId = R.drawable.scatter_night;
                break;
            case "09d":
            case "09n":
                conditionId = R.drawable.shower_rain;
                break;
            case "10d":
                conditionId = R.drawable.rain_day;
                break;
            case "10n":
                conditionId = R.drawable.rain_night;
                break;
            case "11d":
            case "11n":
                conditionId = R.drawable.thundstorm;
                break;
            case "13d":
            case "13n":
                conditionId = R.drawable.snow;
                break;
            case "50d":
            case "50n":
                conditionId = R.drawable.mist;
                break;
        }

        return conditionId;
    }

    public interface AsyncResponse {
        void processFinish(LocationWeatherInfo locationWeatherInfo);
    }


    public static class GetCurrentWeatherConditionTask extends AsyncTask<String, Void, Void> {
        public AsyncResponse delegate = null;
        JSONObject current_weather;
        JSONArray hourly_weather;
        JSONArray daily_weather;

        public GetCurrentWeatherConditionTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }
        public void getCurrentWeather(float lat, float lon) {
            try {
                URL currentWeatherURL = new URL(String.format(CURRENT_WEATHER_URL, lat, lon));
                HttpURLConnection connection =
                        (HttpURLConnection) currentWeatherURL.openConnection();
                connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp = "";
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();
                current_weather = new JSONObject(json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void getHourlyWeather(float lat, float lon) {
            try {
                URL currentWeatherURL = new URL(String.format(HOURLY_WEATHER_URL, lat, lon));
                HttpURLConnection connection =
                        (HttpURLConnection) currentWeatherURL.openConnection();
                connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp = "";
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();
                JSONObject jsonObject = new JSONObject(json.toString());
                hourly_weather = jsonObject.getJSONArray("list");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void getDailyWeather(float lat, float lon) {
            try {
                URL currentWeatherURL = new URL(String.format(DAILY_WEATHER_URL, lat, lon));
                HttpURLConnection connection =
                        (HttpURLConnection) currentWeatherURL.openConnection();
                connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp = "";
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();
                JSONObject jsonObject = new JSONObject(json.toString());
                daily_weather = jsonObject.getJSONArray("list");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            getCurrentWeather(Float.parseFloat(params[0]), Float.parseFloat(params[1]));
            getHourlyWeather(Float.parseFloat(params[0]), Float.parseFloat(params[1]));
            getDailyWeather(Float.parseFloat(params[0]), Float.parseFloat(params[1]));
            return null;
        }

        @Override
        protected void onPostExecute(Void json) {
            try {

                if (current_weather != null) {

                    LocationWeatherInfo locationWeatherInfo = new LocationWeatherInfo();

                    //Current
                    JSONObject details = current_weather.getJSONArray("weather").getJSONObject(0);
                    locationWeatherInfo.country = current_weather.getJSONObject("sys").getString("country");
                    locationWeatherInfo.name = current_weather.getString("name");
                    locationWeatherInfo.description = details.getString("description").toUpperCase(Locale.US);
                    locationWeatherInfo.temperature = String.format("%.0f", current_weather.getJSONObject("main").getDouble("temp")) + "°";
                    locationWeatherInfo.humidity = current_weather.getJSONObject("main").getString("humidity") + "%";
                    locationWeatherInfo.pressure = current_weather.getJSONObject("main").getString("pressure") + " hPa";
                    locationWeatherInfo.weatherIconText = GeneralUtils.setWeatherIcon(details.getInt("id"),
                            current_weather.getJSONObject("sys").getLong("sunrise") * 1000,
                            current_weather.getJSONObject("sys").getLong("sunset") * 1000);
                    locationWeatherInfo.mainGroup = details.getString("main");
                    locationWeatherInfo.conditionId = setConditionId(details.getString("icon"));
                    locationWeatherInfo.sunRise = "" + current_weather.getJSONObject("sys").getLong("sunrise") * 1000;
                    locationWeatherInfo.sunSet = "" + current_weather.getJSONObject("sys").getLong("sunset") * 1000;
                    if(current_weather.getJSONObject("wind").has("speed")){
                        locationWeatherInfo.windSpeed = String.valueOf(current_weather.getJSONObject("wind").getDouble("speed")) + "m/s";
                    }
                    if(current_weather.getJSONObject("wind").has("deg")){
                        locationWeatherInfo.windDegree = String.valueOf(current_weather.getJSONObject("wind").getInt("deg")) + "°";
                    }

                    //Hourly
                    locationWeatherInfo.hourlyWeatherList = new ArrayList<>();
                    int maxTemp = -100000,minTemp = 100000;
                    for(int i = 0 ; i < hourly_weather.length() ; i++){
                        JSONObject child = hourly_weather.getJSONObject(i);
                        String time ="" + child.getLong("dt") * 1000;
                        int temperature = (int) child.getJSONObject("main").getDouble("temp");
                        if(temperature > maxTemp)
                            maxTemp = temperature;
                        if(temperature < minTemp)
                            minTemp = temperature;
                        String temp = temperature + "°";
                        String iconText =  GeneralUtils.setWeatherIcon(child.getJSONArray("weather").getJSONObject(0).getInt("id"),0,0);
                        locationWeatherInfo.hourlyWeatherList.add(new LocationWeatherInfo.HourlyWeatherInfo(time, temp ,iconText));
                    }
                    locationWeatherInfo.max_temperature = maxTemp + "°";
                    locationWeatherInfo.min_temperature = minTemp + "°";
                    //Daily
                    locationWeatherInfo.dailyWeatherList = new ArrayList<>();
                    for(int i = 0 ; i < daily_weather.length() ; i++){
                        JSONObject child = daily_weather.getJSONObject(i);
                        String weekDay ="" + child.getLong("dt") * 1000;
                        int temperature = (int) child.getJSONObject("temp").getDouble("min");
                        String min_temp = temperature + "°";
                        temperature = (int) child.getJSONObject("temp").getDouble("max");
                        String max_temp = temperature + "°";
                        String iconText =  GeneralUtils.setWeatherIcon(child.getJSONArray("weather").getJSONObject(0).getInt("id"),0,0);
                        locationWeatherInfo.dailyWeatherList.add(new LocationWeatherInfo.DailyWeatherInfo(weekDay, max_temp, min_temp ,iconText));
                    }

                    delegate.processFinish(locationWeatherInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}