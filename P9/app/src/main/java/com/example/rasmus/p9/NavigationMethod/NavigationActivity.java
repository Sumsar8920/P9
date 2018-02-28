package com.example.rasmus.p9.NavigationMethod;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rasmus.p9.BuildConfig;
import com.example.rasmus.p9.Event;
import com.example.rasmus.p9.Minigames.ChargeBattery;
import com.example.rasmus.p9.Minigames.SoundPuzzle2;
import com.example.rasmus.p9.Minigames.TreasureHunt;
import com.example.rasmus.p9.Other.GameIntro;
import com.example.rasmus.p9.Player;
import com.example.rasmus.p9.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class NavigationActivity extends AppCompatActivity {

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
    PowerManager manager;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private static final String START_GAME_1 = "start_game_1";
    private static final String START_GAME_2 = "start_game_2";
    private static final String START_GAME_3 = "start_game_3";

    Handler h;
    int delay = 5000;
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        context = this;


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        h = new Handler();

        manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Your Tag");

        Navigation navigation = new Navigation();
        navigation.activateAccelerometer(this, this);

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

    @Override
    protected void onResume() {
        //start handler as activity become visible

              h.postDelayed(new Runnable() {
            public void run() {
                //do something

                runnable=this;
                fetchData();

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }


    public void fetchData() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(NavigationActivity.this, "Fetch Succeeded",
                                    //Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                            startGame();
                        } else {
                            Toast.makeText(NavigationActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void startGame() {

        //checks whether one of the booleans are set to true and starts the corresponding game.
        if (mFirebaseRemoteConfig.getBoolean(START_GAME_1) == true) {
            Navigation.gameRunning = true;
            Intent intent = new Intent(NavigationActivity.this, GameIntro.class);
            intent.putExtra("GAME","1");
            startActivity(intent);
        }
        if (mFirebaseRemoteConfig.getBoolean(START_GAME_2)) {
            Navigation.gameRunning = true;
            Intent intent = new Intent(NavigationActivity.this, GameIntro.class);
            intent.putExtra("GAME","2");
            startActivity(intent);
        }
        if (mFirebaseRemoteConfig.getBoolean(START_GAME_3)) {
            Navigation.gameRunning = true;
            Intent intent = new Intent(NavigationActivity.this, GameIntro.class);
            intent.putExtra("GAME","3");
            startActivity(intent);
        }

        else {
            //mWelcomeTextView.setAllCaps(false);
            //Toast.makeText(NavigationActivity.this, "False",
              //      Toast.LENGTH_SHORT).show();
        }

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
