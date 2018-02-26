package com.example.rasmus.p9.Minigames;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rasmus.p9.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_puzzle2);

        txtView = (TextView)findViewById(R.id.txtView);
        callButton = (ImageButton)findViewById(R.id.callButton);

        txtView.setText("Place phone on a metal object");
        callButton.setVisibility(View.INVISIBLE);

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        mediaPlayer = new MediaPlayer();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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
        mediaPlayer.start();
        calling = false;
        txtView.setText("Place phone on a metal object");
        callButton.setVisibility(View.INVISIBLE);

    }


}
