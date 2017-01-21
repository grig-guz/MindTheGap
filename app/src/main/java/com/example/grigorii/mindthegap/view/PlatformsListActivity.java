package com.example.grigorii.mindthegap.view;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.grigorii.mindthegap.R;


public class PlatformsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platforms_list);

        ActionBar bar = getSupportActionBar();
        String stationName = getIntent().getStringExtra("Station");
        bar.setTitle(stationName);
        bar.setSubtitle("Arrivals by Line");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_platform_list, new PlatformListFragment())
                    .commit();
        }
    }
}
