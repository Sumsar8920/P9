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
import android.widget.TextView;

import com.example.rasmus.p9.R;

import java.io.IOException;

public class SoundPuzzle2 extends AppCompatActivity implements SensorEventListener{

    MediaPlayer mediaPlayer;
    String playerRole = "";
    String file = "";
    SensorManager mSensorManager = null;
    Sensor sensor;
    TextView txtHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_puzzle2);

        txtHeight = (TextView)findViewById(R.id.txtHeight);

        mediaPlayer = new MediaPlayer();

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        //playSoundfile(playerRole);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register listener
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister listener
        mSensorManager.unregisterListener(this);
    }

    //private SensorEventListener mSensorListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // when accuracy changed, this method will be called.
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // when pressure value is changed, this method will be called.
            float pressure_value = 0.0f;
            float height = 0.0f;

            pressure_value = event.values[0];
            height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure_value);

            txtHeight.setText(String.valueOf(height));

        }
    //};


    public void playSoundfile(String playerRole){
        //play the soundfile through the earpiece of the phone
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
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/raw/" + file));
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
