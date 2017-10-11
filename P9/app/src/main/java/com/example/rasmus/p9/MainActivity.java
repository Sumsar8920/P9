package com.example.rasmus.p9;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Color;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;



import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;
    private Button mAddGeofencesButton;

    public Vibration vibrator;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        //create lightsensor object and pass this activity to it
        // LightSensor lightSensor = new LightSensor(this);
        //call method from LightSensor Class that changes the screen brightness
        // lightSensor.lightIntensity();

        button = (Button)findViewById(R.id.button2);

        //create vibrator object and pass the context
        Vibration vibrator = new Vibration(this);
        //call method that vibrates the phone
        // vibrator.vibrate();

        //Flashlight flashlight = new Flashlight(this);
        // flashlight.startLight();

        Flashlight flashlight = new Flashlight(this);
        flashlight.startLight(); */

        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();


        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION }, 1);

    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Location.LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Location.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Location.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            try {
                mGoogleApiClient.connect();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do something with result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    public void addGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Toast.makeText(this, "Oops!", Toast.LENGTH_SHORT).show();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(this, "Oops!", Toast.LENGTH_SHORT).show();
            // Get the status code for the error and log it using a user-friendly message.
            //String errorMessage = GeofenceErrorMessages.getErrorString(this,
              //     status.getStatusCode());
        }
    }

    public void Start (View view){

        Intent intent = new Intent(this, Accelerometer.class);
        startActivity(intent);

    };


}
