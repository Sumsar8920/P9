package com.example.rasmus.p9;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class Proximity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mProximity;
    private static final int SENSOR_SENSITIVITY = 4;
    TextView test;
    int counter = 0;
    String playerRole;
    ConstraintLayout background;
    Boolean near = false;
    Vibration vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);

        test = (TextView) findViewById(R.id.test);
        background = (ConstraintLayout) findViewById(R.id.background);

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        vibration = new Vibration(this);
        timer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                //near
                near = true;
                changeColor();
                test.setText("near");
            } else {
                //far
                near = false;
                test.setText("far");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void timer(){

            final Handler handler = new Handler();
            final int delay = 5000; //milliseconds

            handler.postDelayed(new Runnable(){
                public void run(){
                    //do something
                    counter ++;
                    changeColor();

                    handler.postDelayed(this, delay);
                }
            }, delay);
        }

    public void changeColor(){

        if(counter == 1){
            if(playerRole.equals("1")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }

            if(playerRole.equals("3") ){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }
                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }
        }
        if(counter == 2){
            if(playerRole.equals("2")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }

            if(playerRole.equals("4")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }
        }
        if(counter == 3){
            if(playerRole.equals("3")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }

            if(playerRole.equals("4")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }
        }
        if(counter == 4){
            if(playerRole.equals("1")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }

            if(playerRole.equals("4")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }
        }
        if(counter == 5){
            if(playerRole.equals("2")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }

            if(playerRole.equals("3")){
                if(near == false) {
                    background.setBackgroundColor(Color.GREEN);
                }

                if(near == true){
                    background.setBackgroundColor(Color.WHITE);
                    vibration.vibrate();
                }
            }


        }


    }

}
