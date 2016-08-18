package com.example.dell.instantforecast;

/**
 * Created by Dell on 7/16/2016.
 */
public class LocationWeatherInfo {
    String id;
    String country;
    String lat;
    String lon;
    String weatherIconText;
    String name;
    String temperature;
    String description;

    LocationWeatherInfo(String id , String cityName, String country, String weatherIconText, String temperature,String description, String lat, String lon){
        this.id = id;
        this.country = country;
        this.name = cityName;
        this.temperature = temperature;
        this.weatherIconText = weatherIconText;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
    }
}
