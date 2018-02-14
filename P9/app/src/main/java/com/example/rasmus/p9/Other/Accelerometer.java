package com.example.rasmus.p9.Other;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.example.rasmus.p9.MainActivity;
import com.example.rasmus.p9.NavigationMethod.Flashlight;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by Rasmus on 02-11-2017.
 */

public class Accelerometer extends IntentService implements SensorEventListener {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    SensorManager smAccelerometer;
    Sensor accelerometer;
    private float mGZ = 0;//gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 10;
    Intent intent;
    public static Boolean screenUp;
    public static Boolean screenDown;

    public Accelerometer(String name) {
        super(name);
    }

    public Accelerometer(){
        super("");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        intent = new Intent(getApplicationContext(),Flashlight.class);

        // Do work here, based on the contents of dataString
        //initialize sensor manager for accelerometer/navigation method
        smAccelerometer = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = smAccelerometer.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Register sensor listener
        smAccelerometer.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        smAccelerometer.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        //smAccelerometer.unregisterListener(this);
    } */


    @Override
    public void onSensorChanged (SensorEvent event){
        try {
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                float gz = event.values[2];
                if (mGZ == 0) {
                    mGZ = gz;
                } else {
                    if ((mGZ * gz) < 0) {
                        mEventCountSinceGZChanged++;
                        if (mEventCountSinceGZChanged == MAX_COUNT_GZ_CHANGE) {
                            mGZ = gz;
                            mEventCountSinceGZChanged = 0;
                            if (gz > 0) {
                                Log.d(TAG, "now screen is facing up.");
                                Toast toast = Toast.makeText(getApplicationContext(), "Up", Toast.LENGTH_SHORT);
                                toast.show();
                                //start flashlight service
                                //getApplicationContext().startService(intent);
                                MainActivity.flashlightFrequency();
                                screenUp = true;
                                screenDown = false;
                                MainActivity.changeBrightness(screenDown);
                                //flashlightFrequency();
                            } else if (gz < 0) {
                                Log.d(TAG, "now screen is facing down.");
                                Toast toast = Toast.makeText(getApplicationContext(), "Down", Toast.LENGTH_SHORT);
                                toast.show();
                                screenUp = false;
                                screenDown = true;
                                MainActivity.changeBrightness(screenDown);
                            }
                        }
                    } else {
                        if (mEventCountSinceGZChanged > 0) {
                            mGZ = gz;
                            mEventCountSinceGZChanged = 0;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }




}
