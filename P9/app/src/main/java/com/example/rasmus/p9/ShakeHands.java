package com.example.rasmus.p9;


import android.content.DialogInterface;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;

import android.content.Intent;

import android.support.v7.app.AlertDialog;

import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class ShakeHands extends AppCompatActivity implements SensorEventListener {

    public Sensor mySensor;
    public SensorManager SM;
    ImageButton button1;
    ImageButton button2;
    Boolean player1Ready = false;
    Boolean player2Ready = false;
    int counter = 0;
    int imageCounter = 0;
    ImageView middleImage;
    MediaPlayer mediaPlayer;
    TextView txt1, txt2;
    public Handler handler = new Handler();
    public int delay = 1000; //milliseconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreen();
        //setContentView(R.layout.activity_shake_hands);

        CharSequence colors[] = new CharSequence[] {"1", "2"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How many players?");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                if(which == 0){
                    //1 player
                    Intent intent = new Intent(ShakeHands.this, ShakeHands1Player.class);
                    startActivity(intent);
                }
                if (which == 1){
                    // 2 player


                }
            }
        });
        builder.show();

        button1 = (ImageButton)findViewById(R.id.button1);
        button2 = (ImageButton)findViewById(R.id.button2);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);

       /* txt1.setText("Place thumb on marker");
        txt2.setText("Place thumb on marker");*/

        middleImage = (ImageView) findViewById(R.id.animationDown);

        mediaPlayer = MediaPlayer.create(this, R.raw.batterycharge);


        // Create sensor manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // MiniGameDrink sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        changeImage();
        //changeThumbImg();

        button1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        player1Ready = true;
                        button1.setBackgroundResource(R.drawable.greenthumb);
                        if (player1Ready == true && player2Ready == true){
                            // Register sensor listener
                            SM.registerListener(ShakeHands.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                            middleImage.setImageResource(R.drawable.battery11);
                            txt1.setText("SHAKE!");
                            txt2.setText("SHAKE!");
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        // End
                        SM.unregisterListener(ShakeHands.this);
                        counter = 0;
                        txt1.setText("");
                        txt2.setText("");
                        //so the players can start over if someone fails. They just have to release the button and press it again.
                        //txt1.setText("Place thumb on marker");
                        player1Ready = false;
                        button1.setBackgroundResource(R.drawable.thumb_scanner);
                        return true;
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
                        button2.setBackgroundResource(R.drawable.greenthumb);
                        if (player1Ready == true && player2Ready == true){
                            // Register sensor listener
                            SM.registerListener(ShakeHands.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                            middleImage.setImageResource(R.drawable.battery11);
                            txt1.setText("SHAKE!");
                            txt2.setText("SHAKE!");
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        // End
                        SM.unregisterListener(ShakeHands.this);
                        counter = 0;
                        txt1.setText("");
                        txt2.setText("");
                        //so the players can start over if someone fails. They just have to release the button and press it again.
                        //txt2.setText("Place thumb on marker");
                        player2Ready = false;
                        button2.setBackgroundResource(R.drawable.thumb_scanner);
                        return true;
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

        if(zFloat > 16 && yFloat > -2 && yFloat < 2) {
            counter ++;
        }

        if(counter == 4){
            counter ++;
            middleImage.setImageResource(R.drawable.battery21);
            mediaPlayer.start();
        }

        if(counter == 10){
            counter ++;
            middleImage.setImageResource(R.drawable.battery31);
            mediaPlayer.start();
        }

        if(counter == 20){
            counter ++;
            middleImage.setImageResource(R.drawable.battery41);
            mediaPlayer.start();
        }

        if(counter == 30){
            counter ++;
            middleImage.setImageResource(R.drawable.battery51);
            mediaPlayer.start();
            victory();
        }


    }

    public void changeImage(){
        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                imageCounter ++;
                if(player1Ready == false || player2Ready == false) {
                    if (imageCounter == 1) {
                        middleImage.setImageResource(R.drawable.battery_singleplayerdown);
                    }
                    if (imageCounter == 2) {
                        middleImage.setImageResource(R.drawable.battery_singleplayerup);
                        imageCounter = 0;
                    }
                }

                handler.postDelayed(this, delay);
            }
        }, delay);
    }


    public void victory(){
        Intent intent = new Intent(ShakeHands.this, Victory.class);
        startActivity(intent);
    }

    public void fullscreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_shake_hands);
    }


    }
