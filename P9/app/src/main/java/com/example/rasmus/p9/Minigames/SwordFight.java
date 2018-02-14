package com.example.rasmus.p9.Minigames;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.example.rasmus.p9.R;

public class SwordFight extends AppCompatActivity implements SensorEventListener {

    public Sensor mySensor;
    public SensorManager SM;
    ImageButton button1;
    Boolean player1Ready = false;
    int counter = 0;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sword_fight);

        button1 = (ImageButton)findViewById(R.id.button1);

        // Create sensor manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // MiniGameDrink sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mMediaPlayer = MediaPlayer.create(this, R.raw.batterycharge);

        button1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        player1Ready = true;
                        if (player1Ready == true){
                            // Register sensor listener
                            SM.registerListener(SwordFight.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // End
                        SM.unregisterListener(SwordFight.this);
                        //so the players can start over if someone fails. They just have to release the button and press it again.
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

        float xFloat = event.values[0];
        float yFloat = event.values[1];
        float zFloat = event.values[2];

        if(xFloat > -1 && xFloat < 1 && yFloat > 2 && zFloat > 9 && zFloat < 11) {
            mMediaPlayer.start();
        }


    }

}
