package com.example.rasmus.p9.Other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.rasmus.p9.Minigames.Minigame;
import com.example.rasmus.p9.NavigationMethod.Navigation;
import com.example.rasmus.p9.R;

import java.io.IOException;

public class GameIntro extends AppCompatActivity {

    public TextView txtView;
    int counter = 10;
    public boolean run = false;
    String playerRole;
    MediaPlayer mediaPlayer;
    String game;
    String file = "one";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_intro);

        Intent intent = new Intent();
        game = intent.getStringExtra("GAME");

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));
        mediaPlayer = new MediaPlayer();
        callCommunicators();



        /*txtView = (TextView) findViewById(R.id.countdown);

        startCountdown();*/

    }

    public void callCommunicators(){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        if(playerRole .equals("1") || playerRole .equals("3")){
            if(game.equals("1")){
                file = "intro";
                whichSoundFile();
            }
            if(game.equals("2")){
                mediaPlayer.reset();
                whichSoundFile();
            }
            if(game.equals("3")){
                mediaPlayer.reset();
                whichSoundFile();
            }
        }
        mediaPlayer.start();
    };

    public void whichSoundFile(){
        try {
            //set datasource to MediaPlayer
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/raw/" + file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void startCountdown(){
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(run != true) {
                    counter--;
                    txtView.setText(String.valueOf(counter));
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                }

                if(counter == 0) {
                    counter++;
                    run = true;
                    if (Navigation.minigame1Done == true) {
                        //start minigame 2
                        Minigame minigame2 = new Minigame();
                        minigame2.startGame("2", GameIntro.this);
                    }

                    if (Navigation.minigame2Done == true) {
                        //start minigame 3
                        Minigame minigame3 = new Minigame();
                        minigame3.startGame("3", GameIntro.this);
                    } else {
                        //start minigame 1
                        Minigame minigame1 = new Minigame();
                        minigame1.startGame("1", GameIntro.this);
                    }
                }

                handler.postDelayed(this, delay);
            }
        }, delay);



    }*/
}
