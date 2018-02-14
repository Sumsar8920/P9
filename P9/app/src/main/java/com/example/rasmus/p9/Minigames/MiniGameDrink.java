package com.example.rasmus.p9.Minigames;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Imports for TextView
import android.view.View;
import android.widget.TextView;

// Imports for Sensors
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Toast;

import com.example.rasmus.p9.Other.GameScreen;
import com.example.rasmus.p9.Other.Victory;
import com.example.rasmus.p9.R;

public class MiniGameDrink extends AppCompatActivity implements SensorEventListener {

    public TextView xText, yText, zText;
    public TextView greenbar1, greenbar2, greenbar3;
    public TextView timer;
    public Sensor mySensor;
    public SensorManager SM;
    public CountDownTimer countDownTimer;
    public boolean timerRunning;
    public MediaPlayer drunkMediaPlayer;
    public Toast toast;
    int counter = 0;
    boolean isPlaying = false;

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
        //xText = (TextView) findViewById(R.id.xText);
        //yText = (TextView) findViewById(R.id.yText);
        //zText = (TextView) findViewById(R.id.zText);
        //timer = (TextView) findViewById(R.id.timer);

        // Set timer to not running
        timerRunning = false;

        // Assign background filling
        greenbar1 = (TextView) findViewById(R.id.greenbar1);
        greenbar2 = (TextView) findViewById(R.id.greenbar2);
        greenbar3 = (TextView) findViewById(R.id.greenbar3);

        // Assign sounds
        drunkMediaPlayer = MediaPlayer.create(this, R.raw.drink1);

        // Assign text to toast
        toast = Toast.makeText(getApplicationContext(), "You completed the minigame!", Toast.LENGTH_LONG);


    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float xFloat = event.values[0];
        float yFloat = event.values[1];
        float zFloat = event.values[2];

        //xText.setText("X: " + event.values[0]);
        //yText.setText("Y: " + event.values[1]);
        //zText.setText("Z: " + event.values[2]);


        if(zFloat > 7){
          //  timer.setText("10");
            timerRunning = false;
            greenbar1.setVisibility(View.VISIBLE);
            greenbar2.setVisibility(View.VISIBLE);
            greenbar3.setVisibility(View.VISIBLE);
            cancel();
            if(isPlaying == true) {
                drunkMediaPlayer.pause();
                drunkMediaPlayer.seekTo(0);
                isPlaying = false;
            }
            counter = 0;

        }

        if(zFloat < 7){
            if(timerRunning == false){
                startTimer();
                if(counter == 0) {
                    isPlaying = true;
                    drunkMediaPlayer.start();
                }
                counter ++;

                }

            }
        }


    public void startTimer(){

        timerRunning = true;
        countDownTimer = new CountDownTimer(10 * 1000, 1000){
        @Override
            public void onTick(long millisUntilFinished){
           // timer.setText("" + millisUntilFinished);

            if(millisUntilFinished / 1000 == 7) {
                greenbar1.setVisibility(View.INVISIBLE);

            }

           if(millisUntilFinished / 1000 == 4) {
                greenbar1.setVisibility(View.INVISIBLE);
                greenbar2.setVisibility(View.INVISIBLE);

            }

            if(millisUntilFinished / 1000 == 1) {
                greenbar1.setVisibility(View.INVISIBLE);
                greenbar2.setVisibility(View.INVISIBLE);
                greenbar3.setVisibility(View.INVISIBLE);
                drunkMediaPlayer.stop();
                Intent intent = new Intent(MiniGameDrink.this, Victory.class);
                startActivity(intent);
            }


        }
        @Override
            public void onFinish(){
            greenbar1.setVisibility(View.INVISIBLE);
            greenbar2.setVisibility(View.INVISIBLE);
            greenbar3.setVisibility(View.INVISIBLE);

        }
        };
        countDownTimer.start();

    }

    public void cancel(){
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }

    }

    @Override
    public void onBackPressed() {
        // your code.
        drunkMediaPlayer.stop();
        Intent intent = new Intent(MiniGameDrink.this, GameScreen.class);
        startActivity(intent);
    }



}
