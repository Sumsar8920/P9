package com.example.rasmus.p9;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

/**
 * Created by Rasmus on 04-10-2017.
 */

public class LightSensor {
    Activity activity;

    //constructor that takes Activity as a parameter
    public LightSensor(Activity activity){
        this.activity = activity;
    }

    //method that changes the screen brightness. The maximum value is 1F and minimum is 0.1F
    public void lightIntensity(Float brightness){
        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
        layout.screenBrightness = brightness;
        activity.getWindow().setAttributes(layout);


    }


}
