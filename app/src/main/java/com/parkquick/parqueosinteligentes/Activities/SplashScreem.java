package com.parkquick.parqueosinteligentes.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;

import com.parkquick.parqueosinteligentes.R;

public class SplashScreem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screem);
        //getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(SplashScreem.this, MainActivity.class);
                startActivity(intent);

            }
       }, 5000);
    }
}