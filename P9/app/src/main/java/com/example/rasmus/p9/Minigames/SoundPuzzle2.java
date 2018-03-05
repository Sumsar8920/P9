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
import com.example.rasmus.p9.Other.Database;
import com.example.rasmus.p9.Other.GameIntro;
import com.example.rasmus.p9.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private static final String START_GAME_1 = "start_game_1";
    Handler h;
    int delay = 5000;
    Runnable runnable;
    public FirebaseDatabase database;
    public DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_sound_puzzle2);

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

        //Firebase.setAndroidContext(this);
        //database = FirebaseDatabase.getInstance();
        rootReference = Database.getDatabaseRootReference();
        DatabaseReference soundPuzzleReference = rootReference.child("soundpuzzle");
        soundPuzzleReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey().toString();
                    String value = ds.getValue().toString();
                    if(key.equals("stop") && value.equals("true")){
                            //String key = ds.getKey().toString();
                            stopGame();
                            break;
                    }
                    if(key.equals("soundfile1") && value.equals("true")){
                        //play soundfile1
                        playHelpSoundfile1();
                        break;

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

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

                h.postDelayed(runnable, delay);
            }
        }, delay);
    }

    protected void onPause() {
        super.onPause();
        h.removeCallbacks(runnable); //stop handler when activity not visible
        sensorManager.unregisterListener(this);
    }

    public void playHelpSoundfile1(){
        mediaPlayer.release();
        mediaPlayer = null;
        mediaPlayer = MediaPlayer.create(this, R.raw.tada);
        mediaPlayer.start();
    }

    private void stopGame() {

        //checks whether one of the booleans are set to true and starts the corresponding game.
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
