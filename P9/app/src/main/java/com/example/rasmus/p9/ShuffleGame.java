package com.example.rasmus.p9;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import static com.google.android.gms.internal.zzs.TAG;

public class ShuffleGame extends AppCompatActivity implements SensorEventListener {

    SensorManager smAccelerometer;
    Sensor accelerometer;
    private float mGZ = 0;//gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 1;
    MediaPlayer media;
    int counter = 0;
    String playerRole;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle_game);

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        //initialize sensor manager for accelerometer/navigation method
        smAccelerometer = (SensorManager) getSystemService(SENSOR_SERVICE);
        // MiniGameDrink sensor
        accelerometer = smAccelerometer.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Register sensor listener
        smAccelerometer.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        image = (ImageView) findViewById(R.id.image);

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
                                Log.d(TAG, "now screen is facing up.");
                                Toast toast = Toast.makeText(getApplicationContext(), "Up", Toast.LENGTH_SHORT);
                                toast.show();
                                displayImage();


                            } else if (gz < 0) {
                                Log.d(TAG, "now screen is facing down.");
                                Toast toast = Toast.makeText(getApplicationContext(), "Down", Toast.LENGTH_SHORT);
                                toast.show();
                                counter ++;


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

    public void displayImage(){

        if(playerRole.equals("1")) {
            if (counter == 1) {
                image.setBackgroundResource(R.drawable.player1_8);
            }

            if (counter == 2) {
                image.setBackgroundResource(R.drawable.player1_5);
            }

            if (counter == 3) {
                image.setBackgroundResource(R.drawable.player1_3);

            }

            if (counter == 4) {
                image.setBackgroundResource(R.drawable.player1_2);
                counter = 0;

            }
        }

        if(playerRole.equals("2")) {
            if (counter == 1) {
                image.setBackgroundResource(R.drawable.player2_2);
            }

            if (counter == 2) {
                image.setBackgroundResource(R.drawable.player2_5);
            }

            if (counter == 3) {
                image.setBackgroundResource(R.drawable.player2_8);

            }

            if (counter == 4) {
                image.setBackgroundResource(R.drawable.player2_3);
                counter = 0;

            }
        }

        if(playerRole.equals("3")) {
            if (counter == 1) {
                image.setBackgroundResource(R.drawable.player3_5);
            }

            if (counter == 2) {
                image.setBackgroundResource(R.drawable.player3_3);
            }

            if (counter == 3) {
                image.setBackgroundResource(R.drawable.player3_2);

            }

            if (counter == 4) {
                image.setBackgroundResource(R.drawable.player3_8);
                counter = 0;

            }
        }

        if(playerRole.equals("4")) {
            if (counter == 1) {
                image.setBackgroundResource(R.drawable.player4_2);
            }

            if (counter == 2) {
                image.setBackgroundResource(R.drawable.player4_3);
            }

            if (counter == 3) {
                image.setBackgroundResource(R.drawable.player4_5);

            }

            if (counter == 4) {
                image.setBackgroundResource(R.drawable.player4_8);
                counter = 0;

            }
        }





    }

}