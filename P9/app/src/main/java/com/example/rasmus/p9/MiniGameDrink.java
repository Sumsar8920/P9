package com.example.rasmus.p9;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Imports for TextView
import android.widget.Button;
import android.widget.TextView;

// Imports for Sensors
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MiniGameDrink extends AppCompatActivity implements SensorEventListener {

    public TextView xText, yText, zText;
    public Sensor mySensor;
    public SensorManager SM;

    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        // Create sensor manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // MiniGameDrink sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Assign TextViews
        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);

        // Assign button
        button = (Button)findViewById(R.id.button);


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);

        float xFloat = event.values[0];
        float yFloat = event.values[1];
        float zFloat = event.values[2];

        if(xFloat> 2.5 || xFloat < -2.5 || yFloat > 2.5 || yFloat < -2.5){
            button.setBackgroundColor(Color.RED);

        }

        else{
            button.setBackgroundColor(Color.GREEN);
        }


    }
}
