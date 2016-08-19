package com.example.dell.instantforecast;

/**
 * Created by Dell on 7/16/2016.
 */
public class LocationWeatherInfo {
    String id;
    String country;
    double lat;
    double lon;
    String weatherIconText;
    String name;
    String temperature;
    String description;
    String humidity;
    String pressure;
    long updateTime;
    String sunRise;
    String sunSet;
    int conditionId;
    String timeZone;

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
