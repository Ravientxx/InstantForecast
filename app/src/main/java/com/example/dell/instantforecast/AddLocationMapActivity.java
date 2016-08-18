package com.example.dell.instantforecast;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by TONITUAN on 8/17/2016.
 */
public class AddLocationMapActivity extends AppCompatActivity{

    GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinate);

        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                getCurrentLocation();
                addEvent();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Choose location");
    }

    public void addEvent(){
        if(map == null) return;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                moveToNewPlace(latLng);
                WeatherInfoFragment.loadWeatherInfo("add_location_map", String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
                finish();
            }
        });
    }

    public void moveToNewPlace(LatLng latLng){
        if(latLng != null){
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude))
                    .zoom(10)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(latLng.latitude, latLng.longitude));
            options.title("Your location");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            Marker marker = map.addMarker(options);
            marker.showInfoWindow();
        }
    }

    public void getCurrentLocation() throws SecurityException{
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        LatLng lng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToNewPlace(lng);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return false;
    }
}
