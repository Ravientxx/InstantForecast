package com.example.dell.instantforecast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private PreferenceManager prefManager;
    private LinearLayout scrollView;
    static ArrayList<LocationWeatherInfo> popularLocation;
    static ArrayList<String> selectedLocation;
    static boolean clicked[] = {false,false,false,false,false,false,false};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        prefManager = new PreferenceManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
        }
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome);
        initPopularLocations();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2};

        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.beginFakeDrag();

        final Button btnContinue = (Button) findViewById(R.id.btnContinue);
        if (btnContinue != null) {
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current = getItem(+1);
                    if (current < layouts.length) {
                        btnContinue.setText("Finish");
                        viewPager.setCurrentItem(current);
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        if (selectedLocation.size() == 0) {
                            intent.putExtra("loadFromWelcome", false);
                        } else {
                            intent.putExtra("loadFromWelcome", true);
                        }
                        startActivity(intent);
                        finish();
                        prefManager.setFirstTimeLaunch(false);
                    }
                }
            });
        }
        getSupportActionBar().hide();
    }

    private synchronized void addCity() {
        selectedLocation = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        scrollView = (LinearLayout) findViewById(R.id.liner);
        for (int i = 0; i < 7; i++) {
            View view = inflater.inflate(R.layout.item_city, null, false);
            final ImageView imageView = (ImageView) view.findViewById(R.id.click);
            final TextView textView = (TextView) view.findViewById(R.id.txtCity);
            textView.setText(popularLocation.get(i).name);
            ImageView imageCity = (ImageView) view.findViewById(R.id.imageCity);
            switch (popularLocation.get(i).name) {
                case "New York":
                    imageCity.setImageResource(R.drawable.new_york);
                    break;
                case "London":
                    imageCity.setImageResource(R.drawable.london);
                    break;
                case "Hồ Chí Minh":
                    imageCity.setImageResource(R.drawable.ho_chi_minh);
                    break;
                case "Tokyo":
                    imageCity.setImageResource(R.drawable.tokyo);
                    break;
                case "Paris":
                    imageCity.setImageResource(R.drawable.paris);
                    break;
                case "Sydney":
                    imageCity.setImageResource(R.drawable.sydney);
                    break;
                case "Singapore":
                    imageCity.setImageResource(R.drawable.singapore);
                    break;
            }
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (int)v.getTag();
                    clicked[i] = !clicked[i];
                    if (clicked[i]) {
                        imageView.setVisibility(View.VISIBLE);
                        String str = textView.getText().toString();
                        selectedLocation.add(str);
                    } else {
                        imageView.setVisibility(View.GONE);
                        String str = textView.getText().toString();
                        selectedLocation.remove(str);
                    }
                }
            });
            scrollView.addView(view);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(WelcomeActivity.this, SplashScreen.class);
        intent.putExtra("loadFromWelcome", false);
        startActivity(intent);
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            //addBottomDots(position);
            if (position == 1) {
                addCity();
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void initPopularLocations() {
        popularLocation = new ArrayList<>();
        popularLocation.add(new LocationWeatherInfo("5128638", "New York", 43.000351, -75.499901));
        popularLocation.add(new LocationWeatherInfo("2643743", "London", 51.50853, -0.12574));
        popularLocation.add(new LocationWeatherInfo("1566083", "Hồ Chí Minh", 10.75, 106.666672));
        popularLocation.add(new LocationWeatherInfo("1850147", "Tokyo", 35.689499, 139.691711));
        popularLocation.add(new LocationWeatherInfo("6618607", "Paris", 48.8592, 2.3417));
        popularLocation.add(new LocationWeatherInfo("2147714", "Sydney", -33.867851, 151.207321));
        popularLocation.add(new LocationWeatherInfo("1880252", "Singapore", 1.28967, 103.850067));
    }
}
