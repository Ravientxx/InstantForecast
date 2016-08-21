package com.example.dell.instantforecast;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddLocationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    public class LocationInfo {
        String id;
        String name;
        double lon;
        double lat;
        String country;

        public LocationInfo(String id, String name, double lon, double lat, String country) {
            this.id = id;
            this.name = name;
            this.lon = lon;
            this.lat = lat;
            this.country = country;
        }
    }

    SearchView searchView;
    static Context context;
    static ListView listView;
    static ArrayList<String> searchResult;
    static ArrayList<LocationInfo> locationInfos;
    static DatabaseReference mDatabase;
    static String loadingHolder[] = {"Searching location ...."};
    static String noResult[] = {"Location is not available ...."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_add_location);
        searchView = (SearchView) findViewById(R.id.searchCity);
        listView = (ListView) findViewById(R.id.city_list);
        listView.setOnItemClickListener(this);

        int id = searchView.getContext().
                getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = (EditText) searchView.findViewById(id);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.WHITE);

        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(this);

        //getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Add Location");

        context = this;
        searchResult = new ArrayList<>();

        //FirebaseApp.initializeApp(context, FirebaseOptions.fromResource(context));
        mDatabase = FirebaseDatabase.getInstance().getReference();

        locationInfos = new ArrayList<>();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LocationInfo locationInfo = locationInfos.get(position);

        WeatherInfoFragment.loadWeather(new LocationWeatherInfo(locationInfo.id,0,locationInfo.lat,locationInfo.lon),true);
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listView.setAdapter(new ArrayAdapter<>(context, R.layout.search_location_list_item, loadingHolder));
        if (newText.length() > 2) {
            Query query = mDatabase.child("cityList").orderByChild("name").startAt(newText.toLowerCase()).endAt(newText.toLowerCase() + "~");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    locationInfos.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        locationInfos.add(new LocationInfo(
                                        String.valueOf(data.child("_id").getValue(int.class)),
                                        GeneralUtils.toTitleCase(data.child("name").getValue(String.class)),
                                        data.child("lon").getValue(Double.class),
                                        data.child("lat").getValue(Double.class),
                                        data.child("country").getValue(String.class).toUpperCase()
                                )
                        );
                    }
                    searchResult.clear();
                    for (int i = 0; i < locationInfos.size(); i++) {
                        searchResult.add(locationInfos.get(i).name + ", " + locationInfos.get(i).country);
                    }
                    if (searchResult.size() == 0) {
                        listView.setAdapter(new ArrayAdapter<>(context, R.layout.search_location_list_item, noResult));
                    } else {
                        listView.setAdapter(new ArrayAdapter<>(context, R.layout.search_location_list_item, searchResult));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    System.out.println(databaseError.toException());
                }
            });
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //user clicked a menu-item from ActionBar
        int id = item.getItemId();
        switch (id) {
            default:
                finish();
                break;
        }
        return false;
    }
}
