package com.example.rasmus.p9.NavigationMethod;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.rasmus.p9.Event;
import com.example.rasmus.p9.Minigames.ChargeBattery;
import com.example.rasmus.p9.Player;
import com.example.rasmus.p9.R;

public class NavigationActivity extends AppCompatActivity /*implements SensorEventListener*/ {

    public static float distance;
    public String orientation;
    SensorManager smAccelerometer;
    Sensor accelerometer;
    private float mGZ = 0; //gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 10;
    public static boolean screenDown = false;
    LocationManager mLocationManager;
    static TextView txtDistance;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static Activity context = null;

    public ScreenBrightness brightness;
    public Flashlight flashlight;
    Player player;
    Event meteor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        context = this;

        Navigation navigation = new Navigation();
        navigation.activateAccelerometer(this, this);

        /*smAccelerometer = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = smAccelerometer.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Register sensor listener
        smAccelerometer.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_GAME); */

        //creates Screenbrightness and Flashlight object
        //brightness = new ScreenBrightness(this);
        //flashlight = new Flashlight();

        //creates player object
        player = new Player();

        //ask for location permission
        checkLocationPermission();

        //starts location background service if permission is granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            startService(new Intent(this, UserLocation.class));

        }

        txtDistance = (TextView)findViewById(R.id.distance);


    }

    /*@Override
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
                                //Toast toast = Toast.makeText(getApplicationContext(), "Up", Toast.LENGTH_SHORT);
                                //toast.show();
                                screenDown = false;
                                Intent intent = new Intent(this,Flashlight.class);
                                intent.putExtra("DISTANCE",distance);
                                this.startService(intent);
                            } else if (gz < 0) {
                                //Toast toast = Toast.makeText(getApplicationContext(), "Down", Toast.LENGTH_SHORT);
                                //toast.show();
                                screenDown = true;
                                brightness.adjustBrightness(smAccelerometer, accelerometer, distance);
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
    } */

    /*public static void calculateDistance(Location location){
        Location locationA = new Location("point A");

        locationA.setLatitude(location.getLatitude());
        locationA.setLongitude(location.getLongitude());

        Location locationB = new Location("point B");

        locationB.setLatitude(57.043879);
        locationB.setLongitude(9.937943);

        distance = locationA.distanceTo(locationB);

        txtDistance.setText(Float.toString(Math.round(distance)));
    } */

    public void startGame(Activity activity , String game){
        /*
        Step 1: unregister sensoreventlistener (accelerometer)
        Step 2: set screen brightness to full
        Step 3: kill flashlight
        Step 4: start intent to game
         */
        //smAccelerometer.unregisterListener(this);
        if(game.equals("1")){
            try {
                Intent intent = new Intent(activity, ChargeBattery.class);
                activity.startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void stopGame(){

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Allow location")
                        .setMessage("Do you want to allow the device to get your current location?")
                        .setPositiveButton("Hell yeah!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(NavigationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);

                        //start background service if permission is granted!!
                        startService(new Intent(this, UserLocation.class));

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }



}
