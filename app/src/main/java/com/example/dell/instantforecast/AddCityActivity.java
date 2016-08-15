package com.example.dell.instantforecast;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SearchView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddCityActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    TextView txtMsg;
    String[] items = {"Data-0", "Data-1", "Data-2", "Data-3", "Data-4", "Data-5", "Data-6", "Data-7"};
    SearchView searchView;
    ListView listView;
    ArrayList<String> cityName;
    ArrayList<String> cityCountry;
    ArrayList<String> cityLon;
    ArrayList<String> cityLat;
    ArrayList<String> searchResult;
    boolean doneLoadCityList;

    public class LoadCityTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            loadCityList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            doneLoadCityList = true;
        }
    }
    void loadCityList() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("city_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray m_jArray = obj.getJSONArray("cityList");
            cityName = new ArrayList<String>();
            cityCountry = new ArrayList<String>();
            cityLon = new ArrayList<String>();
            cityLat = new ArrayList<String>();

            for (int i = 0; i < m_jArray.length(); i++) {
                JSONObject jo_inside = m_jArray.getJSONObject(i);
                String name = jo_inside.getString("name");
                cityName.add(name);
                String country = jo_inside.getString("country");
                cityCountry.add(country);
                String lon = jo_inside.getString("lon");
                cityLon.add(lon);
                String lat = jo_inside.getString("lat");
                cityLat.add(lat);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_add_city);
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
        getSupportActionBar().hide();
        doneLoadCityList = false;

        LoadCityTask loadCityTask =  new LoadCityTask();
        loadCityTask.execute();

        searchResult = new ArrayList<String>();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String result = searchResult.get(position);
        int i = cityName.indexOf(result);
        WeatherInfoFragment.loadWeatherInfo(cityLat.get(i), cityLon.get(i), true ,false);
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(doneLoadCityList) {
            if (newText.length() > 0) {
                searchResult.clear();
                System.out.println(newText);
                for (int i = 0; i < cityName.size(); i++) {
                    if (cityName.get(i).toLowerCase().contains(newText.toLowerCase())) {
                        searchResult.add(cityName.get(i));
                    }
                }
                listView.setAdapter(new ArrayAdapter<String>(this, R.layout.search_city_list_item, searchResult));
            }
        }
        return false;
    }
}
