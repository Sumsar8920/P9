package com.example.rasmus.p9.Minigames;

import android.app.Activity;
import android.content.Intent;

import com.example.rasmus.p9.NavigationMethod.NavigationActivity;

/**
 * Created by rasmu on 12-02-2018.
 */

public class Minigame {
    /*
    Set static boolean gameRunning to false and minigameDone1,2 and 3 to true after each game!!!
     */


    public void startGame(String game, Activity activity){

        if(game.equals("1")) {
            Intent intent = new Intent(activity, ChargeBattery.class);
            activity.startActivity(intent);

        }

        if(game.equals("2")) {
            Intent intent = new Intent(activity, TreasureHunt.class);
            activity.startActivity(intent);
        }

        if(game.equals("3")) {
            Intent intent = new Intent(activity, ChargeBattery.class);
            activity.startActivity(intent);
        }



    }


}
