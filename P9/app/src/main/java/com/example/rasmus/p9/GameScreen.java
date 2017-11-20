package com.example.rasmus.p9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GameScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);





    }

    public void liftMeteor(View v){
        Intent intent = new Intent(GameScreen.this, LiftMeteor.class);
        startActivity(intent);
    }

    public void chargeBattery(View v){
        Intent intent = new Intent(GameScreen.this, ShakeHands.class);
        startActivity(intent);
    }

    public void swordFight(View v){
        Intent intent = new Intent(GameScreen.this, SwordFight.class);
        startActivity(intent);
    }

    public void mrMime(View v){
        Intent intent = new Intent(GameScreen.this, MrMime.class);
        startActivity(intent);
    }

    public void scoutGame(View v){
        Intent intent = new Intent(GameScreen.this, ScoutGame.class);
        startActivity(intent);
    }


    public void bombSquad(View v) {
        Intent intent = new Intent(GameScreen.this, BombSquad.class);
        startActivity(intent);
    }

    public void shuffleGame(View v){
        Intent intent = new Intent(GameScreen.this, ShuffleGame.class);
        startActivity(intent);
    }

    public void blowMic(View v){
        Intent intent = new Intent(GameScreen.this, BlowMic.class);
        startActivity(intent);
    }

    public void proximity(View v){
        Intent intent = new Intent(GameScreen.this, Proximity.class);
        startActivity(intent);
    }

    public void drinkingGame(View v){
        Intent intent = new Intent(GameScreen.this, MiniGameDrink.class);
        startActivity(intent);
    }


}
