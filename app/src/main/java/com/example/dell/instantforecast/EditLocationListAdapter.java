package com.example.dell.instantforecast;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Dell on 8/17/2016.
 */
public class EditLocationListAdapter extends BaseAdapter {
    public Context contextListView;
    ArrayList<LocationWeatherInfo> listModels;
    static ArrayList<String> arrInt = new ArrayList<>();
    EditLocationListAdapter(Context context,ArrayList<LocationWeatherInfo> city_list){
        contextListView = context;
        //listModels = new ArrayList<LocationWeatherInfo>();
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
        final LocationWeatherInfo current_city = listModels.get(position);
        //final Boolean isClicked = true;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contextListView.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.edit_location_list_item, parent, false);
        }

        ImageView reorderIcon = (ImageView) convertView.findViewById(R.id.reorder_icon);
        final ImageView deleteIcon = (ImageView) convertView.findViewById(R.id.delete_icon);
        TextView cityName = (TextView) convertView.findViewById(R.id.city_name);
        cityName.setText(current_city.name + ", " + current_city.country);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            Boolean isClicked = true;
            @Override
            public void onClick(View v) {
                if(isClicked) {
                    deleteIcon.setImageResource(R.drawable.ic_highlight_on_white_24dp);
                    isClicked = false;
                    arrInt.add(String.valueOf(position));
                 //   Toast.makeText(contextListView, "add" + String.valueOf(position) + arrInt.size(), Toast.LENGTH_LONG).show();
                }
                else
                {
                    deleteIcon.setImageResource(R.drawable.ic_highlight_off_white_24dp);
                    isClicked = true;
                    arrInt.remove(String.valueOf(position));

                   // Toast.makeText(contextListView, "remove" + String.valueOf(position) + arrInt.size(), Toast.LENGTH_LONG).show();
                }

                if(arrInt.size() > 0)
                    EditLocationActivity.delete.setVisible(true);
                else
                    EditLocationActivity.delete.setVisible(false);
                //listModels.remove(position);
                //notifyDataSetChanged();
            }
        });

        return convertView;
    }

}