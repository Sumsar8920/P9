package com.example.rasmus.p9;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class LiftMeteor extends AppCompatActivity implements SensorEventListener {

    public Sensor mySensor;
    public SensorManager SM;
    ImageButton button1;
    ImageButton button2;
    Boolean player1Ready = false;
    Boolean player2Ready = false;
    int counter = 0;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift_meteor);

        button1 = (ImageButton)findViewById(R.id.button1);
        button2 = (ImageButton)findViewById(R.id.button2);

        // Create sensor manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // MiniGameDrink sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mMediaPlayer = MediaPlayer.create(this, R.raw.lift);

        button1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        player1Ready = true;
                        if (player1Ready == true && player2Ready == true){
                            // Register sensor listener
                            SM.registerListener(LiftMeteor.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // End
                        SM.unregisterListener(LiftMeteor.this);
                        //so the players can start over if someone fails. They just have to release the button and press it again.
                        counter = 0;
                        break;
                }
                return false;
            }
        });

        button2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        player2Ready = true;
                        if (player1Ready == true && player2Ready == true){
                            // Register sensor listener
                            SM.registerListener(LiftMeteor.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // End
                        SM.unregisterListener(LiftMeteor.this);
                        //so the players can start over if someone fails. They just have to release the button and press it again.
                        counter = 0;
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float yFloat = event.values[1];
        float zFloat = event.values[2];
        if(zFloat > 13 && yFloat > -2 && yFloat < 2){
            counter ++;
            if(counter == 1){
                //lifted
                Toast toast = Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT);
                toast.show();
                mMediaPlayer.start();
            }
            if(counter == 2){
                //down
                Toast toast = Toast.makeText(getApplicationContext(), "down", Toast.LENGTH_SHORT);
                toast.show();
                counter = 0;
            }
        }
    }




    }
