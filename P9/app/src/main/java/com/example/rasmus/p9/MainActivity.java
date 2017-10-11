package com.example.rasmus.p9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    public Vibration vibrator;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create lightsensor object and pass this activity to it
        // LightSensor lightSensor = new LightSensor(this);
        //call method from LightSensor Class that changes the screen brightness
        // lightSensor.lightIntensity();

        button = (Button)findViewById(R.id.button2);

        //create vibrator object and pass the context
        Vibration vibrator = new Vibration(this);
        //call method that vibrates the phone
        // vibrator.vibrate();

        //Flashlight flashlight = new Flashlight(this);
        // flashlight.startLight();



    }

    public void Start (View view){

        Intent intent = new Intent(this, Accelerometer.class);
        startActivity(intent);

    };


}
