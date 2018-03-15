package com.example.rasmus.p9.NavigationMethod;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.rasmus.p9.Event;
import com.example.rasmus.p9.Minigames.TreasureHunt;
import com.example.rasmus.p9.Other.Database;
import com.example.rasmus.p9.Other.GameIntro;
import com.example.rasmus.p9.Player;
import com.example.rasmus.p9.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public ConstraintLayout background;
    public TextView text;

    public ScreenBrightness brightness;
    public Flashlight flashlight;
    Player player;
    Event meteor;
    PowerManager manager;
    public MediaPlayer mediaPlayer;

    private static final String START_GAME_1 = "start_game_1";
    private static final String START_GAME_2 = "start_game_2";
    private static final String START_GAME_3 = "start_game_3";

    Handler h;
    int delay = 5000;
    Runnable runnable;
    public FirebaseDatabase database;
    public DatabaseReference rootReference;
    public TextView test;
    public  static String playerRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_navigation);
        context = this;
        mediaPlayer = MediaPlayer.create(NavigationActivity.this, R.raw.nav_closer);

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        background = (ConstraintLayout) findViewById(R.id.backgroundActivity);
        text = (TextView) findViewById(R.id.text);

        if(playerRole.equals("1") || playerRole.equals("4")){
            background.setBackgroundColor(Color.BLACK);
            text.setText("Vent på opkald fra guiden");
            text.setTextColor(Color.WHITE);
        }

        else{
            text.setText("Vend telefonen rundt og start navigationen");
        }

        rootReference = Database.getDatabaseRootReference();
        DatabaseReference gamesReference = rootReference.child("startgames");
        gamesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String value = ds.getValue().toString();
                    if(value.equals("true")){
                        String key = ds.getKey().toString();
                        if(key.equals("minigame1")){
                            Navigation.gameRunning = true;
                            Intent intent = new Intent(NavigationActivity.this, GameIntro.class);
                            intent.putExtra("GAME","1");
                            startActivity(intent);
                        }
                        if(key.equals("minigame2")){
                            Navigation.gameRunning = true;
                            Intent intent = new Intent(NavigationActivity.this, GameIntro.class);
                            intent.putExtra("GAME","2");
                            startActivity(intent);
                        }
                        if(key.equals("minigame3")){
                            Navigation.gameRunning = true;
                            Intent intent = new Intent(NavigationActivity.this, GameIntro.class);
                            intent.putExtra("GAME","3");
                            startActivity(intent);
                        }
                        break;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        DatabaseReference navigationReference = rootReference.child("navigation");
        navigationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey().toString();
                    String value = ds.getValue().toString();
                    if(key.equals("rightway") && value.equals("true")){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(NavigationActivity.this, R.raw.nav_closer);
                        mediaPlayer.start();

                    }

                    if(key.equals("wrongway") && value.equals("true")){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(NavigationActivity.this, R.raw.nav_wrong_way);
                        mediaPlayer.start();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

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

        //readFromDatabase();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

