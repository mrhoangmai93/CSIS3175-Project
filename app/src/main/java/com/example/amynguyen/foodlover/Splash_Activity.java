package com.example.amynguyen.foodlover;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class Splash_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_);

        TimerTask newTask = new TimerTask() {
            @Override
            public void run() {
                //start MainActivity
                startActivity(new Intent(Splash_Activity.this, MainActivity.class));

            }
        };

        Timer myTimer = new Timer();
        myTimer.schedule(newTask, 3000);
    }
}
