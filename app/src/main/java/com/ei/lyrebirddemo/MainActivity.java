package com.ei.lyrebirddemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ei.lyrebirddemo.customView.CropActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        startActivity(new Intent(this, MainActivity.class));

    }
}
