package com.example.dell.instantforecast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 8/13/2016.
 */
public class WeatherInfoFragment extends Fragment {

    static int BACKGROUND_IMAGE_ID;
    static TextView detailsField, currentTemperatureField, max_temperature, min_temperature, weatherIcon;
    static ImageView max_img, min_img;
    static ScrollView mainScrollView;
    static int screenHeight;
    static ArrayList<Bitmap> blurred_background_image;
    static Bitmap background_image;
    static GoogleMap mGoogleMap;
    static String tileType = "clouds";
    static TileOverlay tileOverlay;
    static RelativeLayout current_condition_layout, chart2;
    static WeatherInfoFragment frag;
    SupportMapFragment mSupportMapFragment;
    static List<SingleItem> headLines, linkss;
    static ListView listRss;
    SingleItem selectedNewsItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_info_fragment, container, false);

        mSupportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_view);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map_view, mSupportMapFragment).commit();
        }
        if (mSupportMapFragment != null)
        {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            });
        }
        return v;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frag = this;
        linkss = new ArrayList<SingleItem>();
        detailsField = (TextView) view.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) view.findViewById(R.id.current_temperature);
        weatherIcon = (TextView) view.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(Typeface.createFromAsset(MainActivity.mainActivity.getAssets(), "fonts/weathericons-regular-webfont.ttf"));

        max_img = (ImageView) view.findViewById(R.id.max_icon);
        max_temperature = (TextView) view.findViewById(R.id.max_temperature);
        min_img = (ImageView) view.findViewById(R.id.min_icon);
        min_temperature = (TextView) view.findViewById(R.id.min_temperature);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        MainActivity.mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        chart2 = (RelativeLayout)view.findViewById(R.id.chart);
        chart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Demo.class);
                startActivity(intent);
            }
        });

        mChart = (CombinedChart) view.findViewById(R.id.chart1);
        showChart();


        listRss = (ListView)view.findViewById(R.id.myListView);
        listRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedNewsItem = linkss.get(position);
                final Uri storyLink = Uri.parse(selectedNewsItem.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, storyLink);
                startActivity(intent);
            }
        });

        DownloadRssFeed downloader = new DownloadRssFeed(MainActivity.mainActivity);
        downloader.execute("http://tuoitre.vn/rss/tt-the-gioi.rss");

        current_condition_layout = (RelativeLayout) view.findViewById(R.id.current_condition_screen);
        blurred_background_image = new ArrayList<>();

        current_condition_layout = (RelativeLayout) view.findViewById(R.id.current_condition_screen);
        mainScrollView = (ScrollView) view.findViewById(R.id.weather_info_scroll_view);
        mainScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mainScrollView.getScrollY(); //for verticalScrollView
                int stepScreenHeight = screenHeight / 3;
                if (scrollY <= 0) {
                    background_image = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                    MainActivity.mainActivity.background_image_view.setImageBitmap(background_image);
                }
                if (scrollY > stepScreenHeight) {
                    int y = screenHeight / 5;
                    if (scrollY >= stepScreenHeight && scrollY < (stepScreenHeight + y)) {
                        MainActivity.mainActivity.background_image_view.setImageBitmap(blurred_background_image.get(0));
                    }
                    if (scrollY >= (stepScreenHeight + y) && scrollY < (stepScreenHeight + 2 * y)) {
                        MainActivity.mainActivity.background_image_view.setImageBitmap(blurred_background_image.get(1));
                    }
                    if (scrollY >= (stepScreenHeight + 2 * y) && scrollY < (stepScreenHeight + 3 * y)) {
                        MainActivity.mainActivity.background_image_view.setImageBitmap(blurred_background_image.get(2));
                    }
                }
                MainActivity.mainActivity.background_image_view.setScaleType(ImageView.ScaleType.CENTER);
            }
        });


    }

    static public void loadWeatherInfo(final String locationId, final double Lat, final double Lon,final boolean displayWeather) {
        if (GeneralUtils.isOnline()) {
            OpenWeatherMapApiManager.GetWeatherInfoTask getCurrentWeatherTask = new OpenWeatherMapApiManager.GetWeatherInfoTask(new OpenWeatherMapApiManager.AsyncResponse() {
                public void processFinish(final LocationWeatherInfo current_locationWeatherInfo) {
                    GoogleTimezoneAPI.getDateTimeByLocationTask getDateTimeTask = new GoogleTimezoneAPI.getDateTimeByLocationTask(new GoogleTimezoneAPI.AsyncResponse() {
                        @Override
                        public void processFinish(String date) {
                            MainActivity.city_time_textview.setText(date);

                            current_locationWeatherInfo.updateTime = date;
                            current_locationWeatherInfo.id = locationId;
                            current_locationWeatherInfo.lat = Lat;
                            current_locationWeatherInfo.lon = Lon;
                            MainActivity.appDataModel.current_city = current_locationWeatherInfo;

                            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                    new LatLng(MainActivity.appDataModel.current_city.lat,MainActivity.appDataModel.current_city.lon))
                                    .zoom(8)
                                    .build();

                            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
                            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                            tileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(createTileProvider()).transparency(0.5f));
                            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    frag.startActivity(new Intent(frag.getContext(),MapLayerActivity.class));
                                }
                            });
                            if(!locationId.equals("get_current_location")){
                                int locationIndex = -1;
                                for (int i = 0; i < MainActivity.appDataModel.city_list.size(); i++) {
                                    if (current_locationWeatherInfo.id.equals(MainActivity.appDataModel.city_list.get(i).id)) {
                                        locationIndex = i;
                                        break;
                                    }
                                }
                                if (locationIndex == -1 || locationId.equals("add_location_map")) {
                                    MainActivity.appDataModel.city_list.add(current_locationWeatherInfo);
                                    if(EditLocationActivity.editLocationListAdapter != null)
                                        EditLocationActivity.editLocationListAdapter.notifyDataSetChanged();
                                    current_locationWeatherInfo.id = "added_location_map";
                                } else {// Update City Info
                                    MainActivity.appDataModel.city_list.remove(locationIndex);
                                    MainActivity.appDataModel.city_list.add(locationIndex, current_locationWeatherInfo);
                                }
                                MainActivity.navigationMenuListAdapter = new NavigationMenuListAdapter(MainActivity.mainActivity, MainActivity.appDataModel.city_list);
                                MainActivity.navigationMenuList.setAdapter(MainActivity.navigationMenuListAdapter);
                            }
                            current_condition_layout.setMinimumHeight(screenHeight - MainActivity.toolbar.getHeight());

                            if(displayWeather){
                                displayWeatherInfo(current_locationWeatherInfo);

                                BACKGROUND_IMAGE_ID = current_locationWeatherInfo.conditionId;
                                blurred_background_image.clear();
                                Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                                MainActivity.mainActivity.background_image_view.setImageBitmap(bitmap);
                                blurred_background_image.add(GeneralUtils.blur(bitmap, 5f));
                                bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                                blurred_background_image.add(GeneralUtils.blur(bitmap, 15f));
                                bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), BACKGROUND_IMAGE_ID);
                                blurred_background_image.add(GeneralUtils.blur(bitmap, 25f));
                            }
                        }
                    });
                    getDateTimeTask.execute(String.valueOf(Lat), String.valueOf(Lon));
                }
            });
            getCurrentWeatherTask.execute(String.valueOf(Lat), String.valueOf(Lon));
        } else {
            Toast networkError = Toast.makeText(MainActivity.mainActivity, "Can't connect to internet!!", Toast.LENGTH_LONG);
            networkError.show();
        }
    }

    static public void displayWeatherInfo(LocationWeatherInfo location){
        //Current condition
        MainActivity.city_name_textview.setText(location.name + "," + location.country);
        detailsField.setText(location.description);
        currentTemperatureField.setText(location.temperature);
        weatherIcon.setText(Html.fromHtml(location.weatherIconText));
        max_img.setImageResource(R.drawable.ic_vertical_align_top_white_24dp);
        min_img.setImageResource(R.drawable.ic_vertical_align_bottom_white_24dp);
        max_temperature.setText("30°");
        min_temperature.setText("24°");

        //Hourly


        //Daily
        //humidity_field.setText("Humidity: "+weather_humidity);
        //pressure_field.setText("Pressure: "+weather_pressure);

    }
    protected Typeface mTfLight;
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
    private CombinedChart mChart;
    private final int itemcount = 12;

    void showChart(){

        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMaxValue(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
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

        data.setData(generateLineData());
        data.setData(generateBarData());
        data.setData(generateBubbleData());
        data.setData(generateScatterData());
        data.setData(generateCandleData());
        data.setValueTypeface(mTfLight);

        xAxis.setAxisMaxValue(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();

    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(index + 0.5f, getRandom(15, 5)));

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
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
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int index = 0; index < itemcount; index++) {
            entries1.add(new BarEntry(0, getRandom(25, 25)));

            // stacked
            entries2.add(new BarEntry(0, new float[]{getRandom(13, 12), getRandom(13, 12)}));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Bar 1");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

        BarDataSet set2 = new BarDataSet(entries2, "");
        set2.setStackLabels(new String[]{"Stack 1", "Stack 2"});
        set2.setColors(new int[]{Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)});
        set2.setValueTextColor(Color.rgb(61, 165, 255));
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

        return d;
    }

    protected ScatterData generateScatterData() {

        ScatterData d = new ScatterData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (float index = 0; index < itemcount; index += 0.5f)
            entries.add(new Entry(index + 0.25f, getRandom(10, 55)));

        ScatterDataSet set = new ScatterDataSet(entries, "Scatter DataSet");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setScatterShapeSize(7.5f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        d.addDataSet(set);

        return d;
    }

    protected CandleData generateCandleData() {

        CandleData d = new CandleData();

        ArrayList<CandleEntry> entries = new ArrayList<CandleEntry>();

        for (int index = 0; index < itemcount; index += 2)
            entries.add(new CandleEntry(index + 1f, 90, 70, 85, 75f));

        CandleDataSet set = new CandleDataSet(entries, "Candle DataSet");
        set.setDecreasingColor(Color.rgb(142, 150, 175));
        set.setShadowColor(Color.DKGRAY);
        set.setBarSpace(0.3f);
        set.setValueTextSize(10f);
        set.setDrawValues(false);
        d.addDataSet(set);

        return d;
    }

    protected BubbleData generateBubbleData() {

        BubbleData bd = new BubbleData();

        ArrayList<BubbleEntry> entries = new ArrayList<BubbleEntry>();

        for (int index = 0; index < itemcount; index++) {
            float y = getRandom(10, 105);
            float size = getRandom(100, 105);
            entries.add(new BubbleEntry(index + 0.5f, y, size));
        }

        BubbleDataSet set = new BubbleDataSet(entries, "Bubble DataSet");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.WHITE);
        set.setHighlightCircleWidth(1.5f);
        set.setDrawValues(true);
        bd.addDataSet(set);

        return bd;
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    public static TileProvider createTileProvider() {
        TileProvider tileProvider = new UrlTileProvider(512, 512) {

            @Override
            public synchronized URL getTileUrl(int i, int i1, int i2) {
                int reversedY = (1 << i2) - i1 - 1;
                String fUrl = String.format(TransparentTileOWM.OWM_TILE_URL, tileType == null ? "clouds" : tileType, i2, i, reversedY);
                URL url = null;
                try {
                    url = new URL(fUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return url;
            }
        };

        return tileProvider;
    }

}
