package com.example.dell.instantforecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mobeta.android.dslv.DragSortListView;

/**
 * Created by Dell on 8/17/2016.
 */
public class EditLocationActivity extends AppCompatActivity {

    DragSortListView dragSortListView;
    static MenuItem delete;
    static public EditLocationListAdapter editLocationListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_location);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Edit Locations");

        LinearLayout add_location = (LinearLayout) findViewById(R.id.linear_layout_add_location);
        add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditLocationActivity.this,AddLocationActivity.class));
            }
        });

        LinearLayout add_location_map = (LinearLayout) findViewById(R.id.linear_layout_add_location_map);
        add_location_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditLocationActivity.this, AddLocationMapActivity.class));
            }
        });

        dragSortListView = (DragSortListView) findViewById(R.id.dragable_list_view);
        editLocationListAdapter = new EditLocationListAdapter(EditLocationActivity.this,MainActivity.appDataModel.city_list);
        dragSortListView.setAdapter(editLocationListAdapter);
        dragSortListView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                LocationWeatherInfo item = editLocationListAdapter.listModels.get(from);
                editLocationListAdapter.listModels.remove(from);
                if (from > to) --from;
                editLocationListAdapter.listModels.add(to, item);
                editLocationListAdapter.notifyDataSetChanged();
                MainActivity.navigationMenuListAdapter.notifyDataSetChanged();
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.mainActivity.saveAppData();
    }
}
