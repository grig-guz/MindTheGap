package com.example.grigorii.mindthegap.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.grigorii.mindthegap.R;

public class ArrivalsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrivals_list);

        String stationName = getIntent().getStringExtra("Station");
        ActionBar bar = getSupportActionBar();
        bar.show();
        bar.setTitle(stationName);
        bar.setSubtitle("Arrivals");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_arrivals_list, new ArrivalsListFragment())
                    .commit();
        }

    }

}
