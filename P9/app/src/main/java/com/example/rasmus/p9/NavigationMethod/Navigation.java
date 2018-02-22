package com.example.rasmus.p9.NavigationMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.support.constraint.ConstraintLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rasmus.p9.Event;
import com.example.rasmus.p9.Minigames.Minigame;
import com.example.rasmus.p9.Other.GameIntro;
import com.example.rasmus.p9.Player;
import com.example.rasmus.p9.PlayerRole;
import com.example.rasmus.p9.R;

import java.io.IOException;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by rasmu on 13-02-2018.
 */

public class Navigation implements SensorEventListener {

    public static SensorManager smAccelerometer;
    public static Sensor accelerometer;
    public static PowerManager manager;
    public static PowerManager.WakeLock wl;
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
    public float oldDistance;
    public ConstraintLayout background;
    MediaPlayer mediaPlayer;
    String file = "";


    public Navigation(){

    }

    public void activateAccelerometer(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        background = (ConstraintLayout)activity.findViewById(R.id.backgroundActivity);

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
                                    background.setBackgroundColor(Color.BLACK);
                                    Intent intent = new Intent(context, Flashlight.class);
                                    intent.putExtra("DISTANCE", distance);
                                    context.startService(intent);
                                } else if (gz < 0) {
                                    //Toast toast = Toast.makeText(context, "Down", Toast.LENGTH_SHORT);
                                    //toast.show();

                                    screenDown = true;
                                    background.setBackgroundColor(Color.WHITE);
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


    public void calculateDistance(Player player, Event event, String playerRole){
        background = (ConstraintLayout)activity.findViewById(R.id.backgroundActivity);

        Location locationA = new Location("point A");

        locationA.setLatitude(player.getLatitude());
        locationA.setLongitude(player.getLongitude());

        Location locationB = new Location("point B");

        locationB.setLatitude(event.getLatitude());
        locationB.setLongitude(event.getLongitude());



        distance = locationA.distanceTo(locationB);
        if(oldDistance != 0.0f && screenDown == true){
            if(oldDistance > distance){
               background.setBackgroundColor(Color.GREEN);
            }

            else{
                background.setBackgroundColor(Color.RED);
            }

        }
        oldDistance = distance;
        NavigationActivity.txtDistance.setText(Float.toString(Math.round(distance)));

        //minigame 1
        if(distance <= 250 && distance > 225 && gameRunning != true && minigame1Done == false){
            gameRunning = true;
            Intent intent = new Intent(activity, GameIntro.class);
            activity.startActivity(intent);
        }

        //minigame 2
        if(distance <= 125 && distance > 100 && gameRunning != true && minigame2Done == false){
            gameRunning = true;
            Intent intent = new Intent(activity, GameIntro.class);
            activity.startActivity(intent);
        }

        //minigame 3
        if(distance <= 25 && gameRunning != true && minigame3Done == false){
            gameRunning = true;
            Intent intent = new Intent(activity, GameIntro.class);
            activity.startActivity(intent);
        }


    }

    public void playSoundfile(String playerRole){
        //play the soundfile through the earpiece of the phone
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

        if(playerRole.equals("1")){
            file = "one";
        }
        if(playerRole.equals("2")){
            file = "two";
        }
        if(playerRole.equals("3")){
            file = "three";
        }
        if(playerRole.equals("4")){
            file = "four";
        }

        try {
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

    }


}
