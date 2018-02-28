package com.example.rasmus.p9.Minigames;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rasmus.p9.NavigationMethod.Navigation;
import com.example.rasmus.p9.NavigationMethod.NavigationActivity;
import com.example.rasmus.p9.Other.Vibration;
import com.example.rasmus.p9.R;

public class TreasureHuntVerification extends AppCompatActivity {

    EditText editTxt1, editTxt2, editTxt3, editTxt4;
    String txt1, txt2, txt3, txt4;
    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt_verification);

        editTxt1 = (EditText)findViewById(R.id.editTxt1);
        editTxt2 = (EditText)findViewById(R.id.editTxt2);
        editTxt3 = (EditText)findViewById(R.id.editTxt3);
        editTxt4 = (EditText)findViewById(R.id.editTxt4);

        editTxt1.requestFocus();
    }

    public void verifyCode(View v){

        txt1 = editTxt1.getText().toString();
        txt2 = editTxt2.getText().toString();
        txt3 = editTxt3.getText().toString();
        txt4 = editTxt4.getText().toString();

        if(txt1.equals("2") && txt2.equals("5") && txt3.equals("3") && txt4.equals("8")){
            Navigation.minigame2Done = true;
            Navigation.gameRunning = false;
            mediaPlayer = MediaPlayer.create(this, R.raw.tada);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Intent intent = new Intent(TreasureHuntVerification.this, NavigationActivity.class);
                    startActivity(intent);
                }
            });
        }

        else{
            Vibration vibrate = new Vibration(this);
            vibrate.vibrate();
            //guide says it's the wrong code
        }


    }
}
