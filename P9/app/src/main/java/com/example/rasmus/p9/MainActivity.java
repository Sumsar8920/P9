package com.example.rasmus.p9;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create lightsensor object and pass this activity to it
        LightSensor lightSensor = new LightSensor(this);
        //call method from LightSensor Class that changes the screen brightness
        lightSensor.lightIntensity();

        //create vibrator object and pass the context
        Vibration vibrator = new Vibration(this);
        //call method that vibrates the phone
        vibrator.vibrate();

    }
}
