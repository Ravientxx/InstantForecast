package com.example.dell.instantforecast;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.GravityCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dell on 7/25/2016.
 */
public class NavigationMenuListAdapter extends BaseAdapter {
    public Context contextListView;
    ArrayList<CityNowWeatherInfo> listModels;

    NavigationMenuListAdapter(Context context,ArrayList<CityNowWeatherInfo> city_list){
        contextListView = context;
        listModels = new ArrayList<CityNowWeatherInfo>();
        listModels.addAll(city_list);
    }
    @Override
    public int getCount() {
        return listModels.size();
    }

    @Override
    public Object getItem(int position) {
        return listModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CityNowWeatherInfo current_city = listModels.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contextListView.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nav_menu_list_item, parent, false);
        }
        TextView weatherIcon = (TextView) convertView.findViewById(R.id.weather_icon);
        TextView cityName = (TextView) convertView.findViewById(R.id.city_name);
        TextView temperature = (TextView) convertView.findViewById(R.id.temperature_field);

        Typeface weatherFont = Typeface.createFromAsset(convertView.getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon .setText(Html.fromHtml(current_city.weatherIconText));
        weatherIcon.setTypeface(weatherFont);
        cityName.setText(current_city.name);
        temperature.setText(current_city.temperature);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawer.closeDrawer(GravityCompat.START);
                MainActivity.mainActivity.loadCurrentWeather(current_city.lat,current_city.lon,false,false);
            }
        });
        return convertView;
    }
}
