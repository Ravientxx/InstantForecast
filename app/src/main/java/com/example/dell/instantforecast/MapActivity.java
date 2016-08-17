package com.example.dell.instantforecast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by TONITUAN on 8/16/2016.
 */
public class MapActivity extends AppCompatActivity{

    private GoogleMap map;
    String tileType = "clouds";
    TileOverlay tileOverlay;
    Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String[] tileName = new String[]{"Clouds", "Temperature", "Precipitations", "Snow", "Rain", "Wind", "Sea level press."};
        ArrayAdapter<String> adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tileName);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(adpt);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        tileType = "clouds";
                        break;
                    case 1:
                        tileType = "temp";
                        break;
                    case 2:
                        tileType = "precipitation";
                        break;
                    case 3:
                        tileType = "snow";
                        break;
                    case 4:
                        tileType = "rain";
                        break;
                    case 5:
                        tileType = "wind";
                        break;
                    case 6:
                        tileType = "pressure";
                        break;
                }

                if (map != null) {
                    tileOverlay.remove();
                    setUpMap();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void setUpMapIfNeeded(){
        if(map != null){
            SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    if(map != null){
                        setUpMap();
                    }
                }
            });
        }
    }

    private void setUpMap(){
        tileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(createTransparentTileProvider()));
    }

    private TileProvider createTransparentTileProvider(){
        return new TransparentTileOWM(tileType);
    }

    public TileProvider createTileProvider(){
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int i, int i1, int i2) {
                String fUrl = String.format(TransparentTileOWM.OWM_TILE_URL, tileType == null ? "clouds" : tileType, i2, i, i1);
                URL url = null;
                try{
                    url = new URL(fUrl);
                }
                catch (MalformedURLException e){
                    e.printStackTrace();
                }
                return null;
            }
        };

        return tileProvider;
    }
}
