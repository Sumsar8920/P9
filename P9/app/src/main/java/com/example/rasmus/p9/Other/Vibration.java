package com.example.rasmus.p9.Other;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rasmus on 04-10-2017.
 */

public class Vibration {

    Context context;

    //constructor that takes the context as a parameter
    public Vibration(Context context){
        this.context = context;
    }

    //method that vibrates phone. The duration 500 is in milliseconds
    public void vibrate(){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
            v.vibrate(500);

    }

}
