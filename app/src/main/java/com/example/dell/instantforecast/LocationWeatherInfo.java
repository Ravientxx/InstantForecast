package com.example.dell.instantforecast;

import java.util.ArrayList;

/**
 * Created by Dell on 7/16/2016.
 */
public class LocationWeatherInfo {

    public static class HourlyWeatherInfo{
        String time;
        String temperature;
        String weatherIconText;

        HourlyWeatherInfo(String time, String temperature,String weatherIconText){
            this.time = time;
            this.temperature = temperature;
            this.weatherIconText = weatherIconText;
        }
    }
    public static class DailyWeatherInfo{
        String weekDay;
        String max_temperature;
        String min_temperature;
        String weatherIconText;

        DailyWeatherInfo(String weekDay, String max_temperature,String min_temperature,String weatherIconText){
            this.weekDay = weekDay;
            this.min_temperature = min_temperature;
            this.max_temperature = max_temperature;
            this.weatherIconText = weatherIconText;
        }
    }

    long updateTime;
    String timeZone;
    //Current Condition
    String id;
    String country;
    double lat;
    double lon;
    String weatherIconText;
    String name;
    String mainGroup;
    String description;
    String temperature;
    String humidity; //%
    String pressure; //hPa
    String windSpeed; //m/s
    String windDegree; //
    String cloudiness; //%
    String rainVolume; //
    String sunRise;
    String sunSet;
    String uvIndex;
    int conditionId;


    //Hourly Weather
    String min_temperature;
    String max_temperature;
    ArrayList<HourlyWeatherInfo> hourlyWeatherList;


    //Daily
    ArrayList<DailyWeatherInfo> dailyWeatherList;

    LocationWeatherInfo(){

    }
    LocationWeatherInfo(String id , long updateTime, double lat, double lon){
        this.id = id;
        this.updateTime = updateTime;
        this.lat = lat;
        this.lon = lon;
    }

    LocationWeatherInfo(String id , String cityName, double lat, double lon){
        this.id = id;
        this.name = cityName;
        this.lat = lat;
        this.lon = lon;
    }
}
