package com.example.dell.instantforecast;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Dell on 7/23/2016.
 */
public class GoogleLocationAPI {

    static GoogleApiClient googleClient;

    static public void initGoogleClient() {
        googleClient =  new GoogleApiClient.Builder(MainActivity.mainActivity)
                .addApi(LocationServices.API)
                .build();
    }

    static public Location getLocation() {
        try {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(googleClient);
            return loc;
        }
        catch(SecurityException se) {}
        return null;
    }

    static public void connect(){
        googleClient.connect();
    }
    static public void disconnect(){
        googleClient.disconnect();
    }
}