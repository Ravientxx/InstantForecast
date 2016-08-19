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
    String updateTime;
    String sunRise;
    String sunSet;
    int conditionId;

    LocationWeatherInfo(){

    }
    LocationWeatherInfo(String id , String cityName, String country, String weatherIconText, String temperature,String description, double lat, double lon){
        this.id = id;
        this.country = country;
        this.name = cityName;
        this.temperature = temperature;
        this.weatherIconText = weatherIconText;
        this.description = description;
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
