package com.example.rasmus.p9.Other;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.rasmus.p9.R;

public class Countdown extends AppCompatActivity {

    TextView txt;
    int counter = 10;
    MediaPlayer media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        txt = (TextView) findViewById(R.id.textView);
        txt.setText(String.valueOf(counter));

        media = MediaPlayer.create(this, R.raw.batterycharge);

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                counter --;
                txt.setText(String.valueOf(counter));
                playSound();

                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    public void playSound(){
        if (counter == 7){
            media.start();
        }

        if (counter == 4){
            media.start();
        }



    }
}
