package com.example.rasmus.p9.Other;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rasmus.p9.NavigationMethod.NavigationActivity;
import com.example.rasmus.p9.R;

import java.io.IOException;

public class Introduction extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    String file = "one";
    Button playAgain;
    Button proceedToNav;
    ImageButton callButton;
    boolean calling = false;
    boolean playerRoleReady = false;
    TextView txtView;
    String playerRole;
    android.support.constraint.ConstraintLayout background;
    int counterShowButton = 0;
    Button startGame;
    int counter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        mediaPlayer = new MediaPlayer();

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        playAgain = (Button) findViewById(R.id.playAgain);
        proceedToNav = (Button) findViewById(R.id.proceedToNav);
        callButton = (ImageButton) findViewById(R.id.callButton);
        txtView = (TextView)findViewById(R.id.txtView);
        background = (android.support.constraint.ConstraintLayout) findViewById(R.id.background);
        startGame = (Button) findViewById(R.id.startGame);

        playAgain.setVisibility(View.GONE);
        proceedToNav.setVisibility(View.GONE);
        startGame.setVisibility(View.INVISIBLE);


        callPlayer();
    }

    public void showButton(){
        counterShowButton++;
        if(counterShowButton == 1){
            startGame.setVisibility(View.VISIBLE);
        }

        if(counterShowButton == 2){
            counterShowButton = 0;
            startGame.setVisibility(View.INVISIBLE);
        }
    }

   public void mediaplayerListen(){
       mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

           @Override
           public void onCompletion(MediaPlayer mp) {
               counter ++;
               if(counter >= 2){
                   background.setOnClickListener(new View.OnClickListener()
                   {
                       @Override
                       public void onClick(View v)
                       {
                           showButton();
                       }
                   });

               }
               background.setBackgroundColor(Color.WHITE);
               playAgain.setVisibility(View.VISIBLE);
               proceedToNav.setVisibility(View.VISIBLE);
               if(playerRole .equals("2") || playerRole .equals("3")){
                   navigatorRole();
               }
           }

       });
   };

    public void startNavigation(View v){
        Intent intent = new Intent(Introduction.this, NavigationActivity.class);
        startActivity(intent);
    }

    public void navigatorRole(){
        txtView.setText("Wait for communicators to finish their call");
        proceedToNav.setVisibility(View.GONE);
        background.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showButton();
            }
        });
    }

    public void introFile(View v){
       txtView.setText("");

       playAgain.setVisibility(View.VISIBLE);
       proceedToNav.setVisibility(View.VISIBLE);
       callButton.setVisibility(View.GONE);

       mediaPlayer.stop();
       mediaPlayer.reset();
       mediaPlayer.release();

       mediaPlayer = new MediaPlayer();

       mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
       if(playerRoleReady == false) {
           mediaplayerListen();
           file = "intro";
            background.setBackgroundColor(Color.BLACK);
            playAgain.setVisibility(View.GONE);
            proceedToNav.setVisibility(View.GONE);
       }
       else{
           mediaplayerListen();
           background.setBackgroundColor(Color.BLACK);
           playAgain.setVisibility(View.GONE);
           proceedToNav.setVisibility(View.GONE);
           if(playerRole .equals("1") || playerRole .equals("4")){
               file = "after_roles";
           }
       }
       whichSoundFile();
       mediaPlayer.start();
   }



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

    public void playAgain(View v){
        introFile(null);
    }

    public void proceedToNav(View v){
        if(playerRoleReady == true){
            Intent intent = new Intent(Introduction.this, NavigationActivity.class);
        startActivity(intent);
        }
        playerRoleReady = true;
        callPlayer();
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

    }

    public void callPlayer(){
        if(playerRoleReady == true){
            callButton.setVisibility(View.VISIBLE);
        }

        if(calling != true) {
            txtView.setText("Please accept the call");
            mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
            mediaPlayer.start();
            calling = true;

        }
    }

    public void callPlayer2(View v){
        calling = false;
        playerRoleReady = true;

        if(calling != true && playerRole .equals("1") || playerRole .equals("4")) {
            callButton.setVisibility(View.VISIBLE);
            playAgain.setVisibility(View.GONE);
            proceedToNav.setVisibility(View.GONE);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
            txtView.setText("Please accept the call");
            mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
            mediaPlayer.start();
            calling = true;
        }
    }

}


