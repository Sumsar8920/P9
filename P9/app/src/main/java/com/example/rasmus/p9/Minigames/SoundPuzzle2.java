package com.example.rasmus.p9.Minigames;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rasmus.p9.BuildConfig;
import com.example.rasmus.p9.NavigationMethod.Navigation;
import com.example.rasmus.p9.NavigationMethod.NavigationActivity;
import com.example.rasmus.p9.Other.GameIntro;
import com.example.rasmus.p9.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.IOException;

public class SoundPuzzle2 extends AppCompatActivity implements SensorEventListener{

    SensorManager sensorManager;
    Sensor magnetometer;
    String playerRole;
    MediaPlayer mediaPlayer;
    String file = "";
    boolean calling = false;
    TextView txtView;
    ImageButton callButton;
    LinearLayout background;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String START_GAME_1 = "start_game_1";
    Handler h;
    int delay = 5000;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_sound_puzzle2);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        h = new Handler();

        txtView = (TextView)findViewById(R.id.txtView);
        callButton = (ImageButton)findViewById(R.id.callButton);
        background = (LinearLayout) findViewById(R.id.background);

        txtView.setText("Place phone on a metal object");
        callButton.setVisibility(View.INVISIBLE);

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        mediaPlayer = new MediaPlayer();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        h.postDelayed(new Runnable() {
            public void run() {
                //do something

                runnable=this;
                fetchData();

                h.postDelayed(runnable, delay);
            }
        }, delay);
    }

    protected void onPause() {
        super.onPause();
        h.removeCallbacks(runnable); //stop handler when activity not visible
        sensorManager.unregisterListener(this);
    }

    public void fetchData() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(NavigationActivity.this, "Fetch Succeeded",
                            //Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                            stopGame();
                        } else {
                            Toast.makeText(SoundPuzzle2.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void stopGame() {

        //checks whether one of the booleans are set to true and starts the corresponding game.
        if (mFirebaseRemoteConfig.getBoolean(START_GAME_1) == false) {
            Navigation.gameRunning = false;
            mediaPlayer.release();
            mediaPlayer = null;
            mediaPlayer = MediaPlayer.create(this, R.raw.tada);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Intent intent = new Intent(SoundPuzzle2.this, NavigationActivity.class);
                    startActivity(intent);
                }

            });
        }

        else {

        }

    }


    @Override
    public void onSensorChanged (SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // get values for each axes X,Y,Z
            float magX = event.values[0];
            float magY = event.values[1];
            float magZ = event.values[2];
            double magnitude = Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));
            // set value on the screen
            //value.setText(DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla");

            if(magnitude > 90){
                txtView.setText("Please accept the call");
                callButton.setVisibility(View.VISIBLE);
                if(playerRole.equals("1")){
                    //set data source to mediaplayer
                    file = "one";
                    callPlayer();
                }
                if(playerRole.equals("2")){
                    //set data source to mediaplayer
                    file = "two";
                    callPlayer();
                }
                if(playerRole.equals("3")){
                    //set data source to mediaplayer
                    file = "three";
                    callPlayer();
                }
                if(playerRole.equals("4")){
                    //set data source to mediaplayer
                    file = "four";
                    callPlayer();
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    public void callPlayer(){
        if(calling != true) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
            mediaPlayer.start();
            calling = true;
        }
    }

    public void playSoundfile(View v){
        background.setBackgroundColor(Color.BLACK);
        callButton.setVisibility(View.INVISIBLE);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            //set datasource to MediaPlayer
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/raw/" + file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                calling = false;
                background.setBackgroundColor(Color.WHITE);
                txtView.setText("Place phone on a metal object");

            }

        });

        mediaPlayer.start();



    }
    @Override
    public void onBackPressed() {
        // your code.
        mediaPlayer.release();
        mediaPlayer = null;
        //smAccelerometer.unregisterListener(this);
        Intent intent = new Intent(SoundPuzzle2.this, NavigationActivity.class);
        startActivity(intent);
    }


}
