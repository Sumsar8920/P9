package com.example.rasmus.p9.Other;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.rasmus.p9.NavigationMethod.NavigationActivity;
import com.example.rasmus.p9.NavigationMethod.NavigationActivity;
import com.example.rasmus.p9.R;

import java.io.IOException;

public class Introduction extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    String file = "one";
    Button button1;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        mediaPlayer = new MediaPlayer();
        introFile();

        button1 = (Button) findViewById(R.id.playAgain);
        button2 = (Button) findViewById(R.id.proceedToNav);

        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
    }

   public void introFile(){
       mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
       file = "intro";
       whichSoundFile();
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

    public void playAgain(View v){
        mediaPlayer.seekTo(0);
    }

    public void proceedToNav(View v){
        mediaPlayer.stop();
        Intent intent = new Intent(Introduction.this, NavigationActivity.class);
        startActivity(intent);
    }
}


