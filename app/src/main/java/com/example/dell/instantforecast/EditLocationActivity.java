package com.example.dell.instantforecast;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

/**
 * Created by Dell on 8/17/2016.
 */
public class EditLocationActivity extends AppCompatActivity {

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
        final DragSortListView dragSortListView = (DragSortListView) findViewById(R.id.dragable_list_view);
        editLocationListAdapter = new EditLocationListAdapter(EditLocationActivity.this,MainActivity.appDataModel.city_list);
        dragSortListView.setAdapter(editLocationListAdapter);

        dragSortListView.setDropListener(new DragSortListView.DropListener() {
            @Override public void drop(int from, int to) {
                CityNowWeatherInfo item = editLocationListAdapter.listModels.get(from);
                editLocationListAdapter.listModels.remove(from);
                if (from > to) --from;
                editLocationListAdapter.listModels.add(to, item);
                editLocationListAdapter.notifyDataSetChanged();
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
