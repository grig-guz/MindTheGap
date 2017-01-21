package com.example.grigorii.mindthegap.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.grigorii.mindthegap.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        String text = "Credits to:\nTfL Open data\nUBC\nAndroid Material Icons\nOSMDroid\nOSMBonusPack";
        TextView textView = (TextView) findViewById(R.id.about_text);
        textView.setText(text);
    }

}
