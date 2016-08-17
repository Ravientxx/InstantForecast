package com.example.dell.instantforecast;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.GravityCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dell on 8/17/2016.
 */
public class EditLocationListAdapter extends BaseAdapter {
    public Context contextListView;
    ArrayList<CityNowWeatherInfo> listModels;

    EditLocationListAdapter(Context context,ArrayList<CityNowWeatherInfo> city_list){
        contextListView = context;
        //listModels = new ArrayList<CityNowWeatherInfo>();
        //listModels.addAll(city_list);
        listModels = city_list;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CityNowWeatherInfo current_city = listModels.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contextListView.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.edit_location_list_item, parent, false);
        }
        ImageView reorderIcon = (ImageView) convertView.findViewById(R.id.reorder_icon);
        ImageView deleteIcon = (ImageView) convertView.findViewById(R.id.delete_icon);
        TextView cityName = (TextView) convertView.findViewById(R.id.city_name);
        cityName.setText(current_city.name);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listModels.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}