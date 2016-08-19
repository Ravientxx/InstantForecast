package com.example.dell.instantforecast;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import org.joda.time.Months;

import java.util.ArrayList;

/**
 * Created by TONITUAN on 8/19/2016.
 */
public class ChartActivity extends AppCompatActivity {

    private CombinedChart chart;
    private final int itemCount = 12;
    private Typeface mTfLight;
    private String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        chart = (CombinedChart)findViewById(R.id.chart1);
        chart.setDescription("");
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        //draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{ CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR});

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0f);// this replaces setStartAtZero(true)

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinValue(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateBarData());
        data.setData(generateLineData());
        data.setValueTypeface(mTfLight);

        xAxis.setAxisMaxValue(data.getXMax() + 0.25f);
        chart.setData(data);
        chart.invalidate();
    }

    private LineData generateLineData(){
        LineData data = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();

        for(int i = 0; i < 12; i++){
            entries.add(new Entry(i + 0.5f, getRandom(15, 5)));
        }

        LineDataSet set = new LineDataSet(entries, "Line data");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        data.addDataSet(set);
        return data;
    }

    private BarData generateBarData(){
        BarData data = new BarData();
        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int index = 0; index < itemCount; index++) {
            entries1.add(new BarEntry(0, getRandom(25, 25)));
            // stacked
            entries2.add(new BarEntry(0, new float[]{getRandom(13, 12), getRandom(13, 12)}));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Bar 1");
        //set1.setColor(Color.rgb(60, 220, 78));
        set1.setColor(Color.rgb(255, 0, 0));
        set1.setValueTextColor(Color.rgb(255, 0, 0));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

        BarDataSet set2 = new BarDataSet(entries2, "");
        set2.setStackLabels(new String[]{"Stack 1", "Stack 2"});
        set2.setColors(new int[]{Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)});
        set2.setValueTextColor(Color.rgb(255, 0, 0));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return data;
    }

    private float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return false;
    }
}