package com.example.rasmus.p9;

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

   /* public Flashlight(Context context, int blinkDelay){
        this.context = context;
        this.blinkDelay = blinkDelay;
    } */
   Boolean stop = false;

    //CameraManager camManager;
    //String cameraId = null; // Usually front camera is at 0 position.

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public Flashlight(String name) {
        super(name);
    }

    public Flashlight(){
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        /*camManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } */
        startLight(2000, "0101010101");
    }

    public void startLight (int blinkDelay, String myString){
        //Context context;
        //int blinkDelay; //Delay in ms


        String cameraId ="";
        CameraManager camManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i <= myString.length(); i++) {
                Boolean screenDown = Accelerometer.screenDown;
                if(screenDown){
                    try {
                        camManager.setTorchMode(cameraId, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                if (i == myString.length()) {
                        startLight(blinkDelay, "0101010101");
                    }

                if (myString.charAt(i) == '0') {
                    try {
                        camManager.setTorchMode(cameraId, true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        camManager.setTorchMode(cameraId, false);
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
