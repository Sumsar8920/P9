package com.example.rasmus.p9.NavigationMethod;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.Nullable;

/**
 * Created by Rasmus on 04-10-2017.
 */

public class Flashlight extends IntentService {

    Boolean stop = false;
    int frequency = 2000;
    String cameraId;
    CameraManager camManager;
    int infinite = 1000000000;

    public Flashlight(String name) {
        super(name);
    }

    public Flashlight(){
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        float distance = intent.getFloatExtra("DISTANCE", 0);

        if(distance > 250){
            frequency = 2200;
        }
        if(distance <= 250 && distance > 225){
            frequency = 2000;
        }
        if(distance <= 225 && distance > 200){
            frequency = 1800;
        }
        if(distance <= 200 && distance > 175){
            frequency = 1600;
        }
        if(distance <= 175 && distance > 150){
            frequency = 1400;
        }
        if(distance <= 150 && distance > 125){
            frequency = 1200;
        }
        if(distance <= 125 && distance > 100){
            frequency = 1000;
        }
        if(distance <= 100 && distance > 75){
            frequency = 800;
        }
        if(distance <= 75 && distance > 50){
            frequency = 600;
        }
        if(distance <= 50 && distance > 25){
            frequency = 400;
        }
        if(distance <= 25){
            frequency = 200;
        }

        cameraId ="";
        camManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        startLight(frequency, "01010101");
    }

    public void startLight (int blinkDelay, String myString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i <= infinite; i++) {

                //checks if number is even
                if ((i%2)==0) {
                    try {
                        camManager.setTorchMode(cameraId, true);
                        if(Navigation.screenDown){
                            camManager.setTorchMode(cameraId, false);
                            stopSelf();
                            return;
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        camManager.setTorchMode(cameraId, false);
                        if(Navigation.screenDown){
                            stopSelf();
                            return;
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(blinkDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }


    }

   public void stopLight(){
          stop = true;
    }


}
