package com.example.rasmus.p9.NavigationMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rasmus.p9.Event;
import com.example.rasmus.p9.Minigames.Minigame;
import com.example.rasmus.p9.Player;
import com.example.rasmus.p9.R;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by rasmu on 13-02-2018.
 */

public class Navigation implements SensorEventListener {

    public static SensorManager smAccelerometer;
    public static Sensor accelerometer;
    private float mGZ = 0; //gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 10;
    public static boolean screenDown = false;
    public static Context context;
    public static Activity activity;
    public ScreenBrightness screenBrightness;
    public static boolean gameRunning = false;
    public static boolean minigame1Done = false;
    public static boolean minigame2Done = false;
    public static boolean minigame3Done = false;


    public static float distance;

    public Navigation(){

    }

    public void activateAccelerometer(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        smAccelerometer = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accelerometer = smAccelerometer.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Register sensor listener
        smAccelerometer.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        screenBrightness = new ScreenBrightness(activity);
    }

    @Override
    public void onSensorChanged (SensorEvent event){
        if(gameRunning != true) {
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
                                    //Toast toast = Toast.makeText(context, "Up", Toast.LENGTH_SHORT);
                                    //toast.show();
                                    screenDown = false;
                                    Intent intent = new Intent(context, Flashlight.class);
                                    intent.putExtra("DISTANCE", distance);
                                    context.startService(intent);
                                } else if (gz < 0) {
                                    //Toast toast = Toast.makeText(context, "Down", Toast.LENGTH_SHORT);
                                    //toast.show();
                                    screenDown = true;
                                    screenBrightness.adjustBrightness(distance);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }


    public void calculateDistance(Player player, Event event){
        Location locationA = new Location("point A");

        locationA.setLatitude(player.getLatitude());
        locationA.setLongitude(player.getLongitude());

        Location locationB = new Location("point B");

        locationB.setLatitude(event.getLatitude());
        locationB.setLongitude(event.getLongitude());



        distance = locationA.distanceTo(locationB);
        NavigationActivity.txtDistance.setText(Float.toString(Math.round(distance)));

        if(distance > 250 && gameRunning != true && minigame1Done == false){
            gameRunning = true;
            Minigame minigame1 = new Minigame();
            minigame1.startGame("1", activity);
        }

        /*//minigame 1
        if(distance <= 250 && distance > 225 && gameRunning != true && minigame1Done == false){
            gameRunning = true;
            Minigame minigame1 = new Minigame();
            minigame1.startGame("1", activity);
        } */

        //minigame 2
        if(distance <= 125 && distance > 100 && gameRunning != true && minigame2Done == false){
            gameRunning = true;
            Minigame minigame2 = new Minigame();
            minigame2.startGame("2", activity);
        }

        //minigame 3
        if(distance <= 25 && gameRunning != true && minigame3Done == false){
            gameRunning = true;
            Minigame minigame3 = new Minigame();
            minigame3.startGame("3", activity);
        }

    }

}
