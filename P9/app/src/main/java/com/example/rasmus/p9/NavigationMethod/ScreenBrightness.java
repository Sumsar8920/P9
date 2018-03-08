package com.example.rasmus.p9.NavigationMethod;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;

import com.example.rasmus.p9.Minigames.Minigame;

/**
 * Created by Rasmus on 04-10-2017.
 */

public class ScreenBrightness extends Navigation {
    Activity activity;
    float brightness;

    //constructor that takes Activity as a parameter

    public ScreenBrightness(Activity activity) {
        this.activity = activity;
    }

    //method that changes the screen brightness. The maximum value is 1F and minimum is 0.1F
    public void adjustBrightness(Float distance){

        if(distance > 275){
            brightness = 0.0F;
        }
        if(distance <= 275 && distance > 250){
            brightness = 0.1F;
        }
        if(distance <= 250 && distance > 225){
            brightness = 0.2F;
        }
        if(distance <= 225 && distance > 200){
            brightness = 0.3F;
        }
        if(distance <= 200 && distance > 175){
            brightness = 0.4F;
        }
        if(distance <= 175 && distance > 150){
            brightness = 0.5F;
        }
        if(distance <= 150 && distance > 125){
            brightness = 0.6F;
        }
        if(distance <= 125 && distance > 100){
            brightness = 0.7F;
        }
        if(distance <= 100 && distance > 75){
            brightness = 0.8F;
        }
        if(distance <= 75 && distance > 50){
            brightness = 0.9F;
        }
        if(distance <= 50 && distance > 25){
            brightness = 0.9F;
        }
        if(distance <= 25){
            brightness = 1F;
        }



        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
        layout.screenBrightness = brightness;
        activity.getWindow().setAttributes(layout);
    }



}
