package com.example.rasmus.p9.Minigames;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.rasmus.p9.Other.GameScreen;
import com.example.rasmus.p9.R;

public class SoundPuzzle extends AppCompatActivity implements SensorEventListener {

    SensorManager smAccelerometer;
    Sensor accelerometer;
    private float mGZ = 0;//gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 1;
    MediaPlayer media;
    String playerRole;
    public Handler handler = new Handler();
    public int delay = 1000; //milliseconds
    int imageCounter = 0;
    ImageView scoutImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreen();
        //setContentView(R.layout.activity_scout_game);

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        //initialize sensor manager for accelerometer/navigation method
        smAccelerometer = (SensorManager) getSystemService(SENSOR_SERVICE);
        // MiniGameDrink sensor
        accelerometer = smAccelerometer.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Register sensor listener
        smAccelerometer.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        scoutImage = (ImageView) findViewById(R.id.scoutImage);

        changeImage();

    }

    @Override
    public void onSensorChanged (SensorEvent event){
        try {
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                float gz = event.values[2];
                if (mGZ == 0) {
                    mGZ = gz;
                } else {
                    if ((mGZ * gz) < 0) {
                        mEventCountSinceGZChanged++;
                        if (mEventCountSinceGZChanged == MAX_COUNT_GZ_CHANGE) {
                            mGZ = gz;
                            mEventCountSinceGZChanged = 0;
                            if (gz > 0) {
                                //Log.d(TAG, "now screen is facing up.");
                                //Toast toast = Toast.makeText(getApplicationContext(), "Up", Toast.LENGTH_SHORT);
                                //toast.show();
                            } else if (gz < 0) {
                                //Log.d(TAG, "now screen is facing down.");
                                //Toast toast = Toast.makeText(getApplicationContext(), "Down", Toast.LENGTH_SHORT);
                                //toast.show();

                                if (playerRole.equals("1")){
                                    media = MediaPlayer.create(this, R.raw.one);
                                    media.start();
                                }

                                if (playerRole.equals("2")){
                                    media = MediaPlayer.create(this, R.raw.two);
                                    media.start();
                                }

                                if (playerRole.equals("3")){
                                    media = MediaPlayer.create(this, R.raw.third);
                                    media.start();
                                }

                                if (playerRole.equals("4")){
                                    media = MediaPlayer.create(this, R.raw.four);
                                    media.start();
                                }
                            }
                        }
                    } else {
                        if (mEventCountSinceGZChanged > 0) {
                            mGZ = gz;
                            mEventCountSinceGZChanged = 0;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    public void changeImage(){
        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                imageCounter ++;
                    if (imageCounter == 1) {
                        scoutImage.setImageResource(R.drawable.scoutfront);
                    }
                    if (imageCounter == 2) {
                        scoutImage.setImageResource(R.drawable.scoutback);
                        imageCounter = 0;
                    }

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public void fullscreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scout_game);
    }

    @Override
    public void onBackPressed() {
        // your code.
        media.release();
        media = null;
        smAccelerometer.unregisterListener(this);
        Intent intent = new Intent(SoundPuzzle.this, GameScreen.class);
        startActivity(intent);
    }

}
