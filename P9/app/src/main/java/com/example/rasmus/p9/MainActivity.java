package com.example.rasmus.p9;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity
        implements
        /*SensorEventListener,*/
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    public static float distance;
    public float oldDistance;

    private TextView textLat, textLong;
    TextView textDistance;

    private MapFragment mapFragment;

    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, MainActivity.class );
        intent.putExtra( NOTIFICATION_MSG, msg );
        return intent;
    }

    private static final float GEOFENCE_RADIUS2 = 500.0f;
    String latitude;
    String longitude;
    String radius;
    Double latitudeDouble;
    Double longitudeDouble;
    Float radiusFloat;
    String playerRole;
    public Sensor accelerometer;
    public SensorManager smAccelerometer;
    Boolean screenDown = false;
    Boolean screenUp = true;
    String cameraId;
    CameraManager camManager;
    Boolean booStatus = false;
    private static Activity contextTest = null;
    LinearLayout background;


    //new
    private float mGZ = 0;//gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextTest = this;

        background = (LinearLayout)findViewById(R.id.layout);

        textLat = (TextView) findViewById(R.id.lat);
        textLong = (TextView) findViewById(R.id.lon);
        textDistance = (TextView) findViewById(R.id.distance);

        //Intent mServiceIntent = new Intent(this, Accelerometer.class);
        //this.startService(mServiceIntent);

        //Get latitude and longitude from former activity
        Intent intent = getIntent();
        latitude = intent.getStringExtra("LATITUDE");
        longitude = intent.getStringExtra("LONGITUDE");
        radius = intent.getStringExtra("RADIUS");
        latitudeDouble = Double.parseDouble(latitude);
        longitudeDouble = Double.parseDouble(longitude);
        radiusFloat = Float.parseFloat(radius);

        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        // create GoogleApiClient
        createGoogleApi();
        //start accelerometer and run flashlight as background service
        //startAccelerometer();

    }

    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.geofence: {
                startGeofence();
                return true;
            }
            case R.id.clear: {
                clearGeofence();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private final int REQ_PERMISSION = 999;

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
        // TODO close app and warn user
    }


    // Callback called when Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick("+latLng +")");
        //markerForGeofence(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  1000;
    private final int FASTEST_INTERVAL = 900;

    // Start location Updates
    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
        //calculate distance to meteor, when user's location is changed.
        calculateDistanceToMeteor();
    }

    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
        //recoverGeofenceMarker();
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    public void calculateDistanceToMeteor(){

        Location locationA = new Location("User location");

        locationA.setLatitude(lastLocation.getLatitude());
        locationA.setLongitude(lastLocation.getLongitude());

        Location locationB = new Location("Meteor location");

        locationB.setLatitude(latitudeDouble);
        locationB.setLongitude(longitudeDouble);

        distance = locationA.distanceTo(locationB);
        textDistance.setText(Float.toString(distance));
        //check if oldDistance is not default value. Check if it's initialized
        if(oldDistance != 0.0f) {
            if (Math.round(distance) < Math.round(oldDistance)) {
                background.setBackgroundColor(Color.GREEN);
            } else {
                background.setBackgroundColor(Color.RED);
            }
        }
        oldDistance = distance;
    }

    public void storeUserLocation(){
        final Handler handler = new Handler();
        final int delay = 15000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                new AsyncStoreCoordinates(playerRole,lastLocation.getLatitude(),lastLocation.getLongitude()).execute();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public static void changeBrightness(Boolean screenDown){
        if(screenDown == true) {

            int distanceInt = Math.round(distance);
            float brightness = 0.0f;

            if (distanceInt < 200 && distanceInt > 180) {
                brightness = 0.1f;
            }
            if (distanceInt < 180 && distanceInt > 160) {
                brightness = 0.2f;
            }
            if (distanceInt < 160 && distanceInt > 140) {
                brightness = 0.3f;
            }
            if (distanceInt < 140 && distanceInt > 120) {
                brightness = 0.4f;
            }
            if (distanceInt < 120 && distanceInt > 100) {
                brightness = 0.5f;
            }
            if (distanceInt < 100 && distanceInt > 80) {
                brightness = 0.6f;
            }
            if (distanceInt < 80 && distanceInt > 60) {
                brightness = 0.7f;
            }
            if (distanceInt < 60 && distanceInt > 40) {
                brightness = 0.8f;
            }
            if (distanceInt < 40 && distanceInt > 20) {
                brightness = 0.9f;
            }
            if (distanceInt < 20) {
                brightness = 1f;
            }
            LightSensor obj = new LightSensor(contextTest);
            obj.lightIntensity(brightness);
        }
        if(screenDown == false){
            LightSensor obj = new LightSensor(contextTest);
            obj.lightIntensity(1f);
        }
    }

    public static void flashlightFrequency(){

        int distanceInt = Math.round(distance);
        int frequency = 2000;

        if(distanceInt < 200 && distanceInt > 180){
            frequency = 1800;
        }
        if(distanceInt < 180 && distanceInt > 160){
            frequency = 1600;
        }
        if(distanceInt < 160 && distanceInt > 140){
            frequency = 1400;
        }
        if(distanceInt < 140 && distanceInt > 120){
            frequency = 1200;
        }
        if(distanceInt < 120 && distanceInt > 100){
            frequency = 1000;
        }
        if(distanceInt < 100 && distanceInt > 80){
            frequency = 800;
        }
        if(distanceInt < 80 && distanceInt > 60){
            frequency = 600;
        }
        if(distanceInt < 60 && distanceInt > 40){
            frequency = 400;
        }
        if(distanceInt < 40 && distanceInt > 20){
            frequency = 200;
        }
        if(distanceInt < 20){
            frequency = 50;
        }
        Intent intent = new Intent(contextTest,Flashlight.class);
        intent.putExtra("FREQUENCY",frequency);
        contextTest.startService(intent);

    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    private void writeActualLocation(Location location) {
        textLat.setText( "Lat: " + location.getLatitude() );
        textLong.setText( "Long: " + location.getLongitude() );

        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private Marker locationMarker;
    private void markerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if ( map!=null ) {
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
        }
    }

    // Start Geofence creation process
    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        //if( geoFenceMarker != null ) {
        Geofence geofence = createGeofence(radiusFloat);
        GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
        addGeofence( geofenceRequest );
        //} else {
        Log.e(TAG, "Geofence marker is null");
        //}
    }

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters

    // Create a Geofence
    private Geofence createGeofence(float radius ) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latitudeDouble, longitudeDouble, radius)
                //.setCircularRegion(57.046595, 9.928749,radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }

    public void stopLight(View v){
        Intent intent = new Intent(MainActivity.this, MiniGameDrink.class);
        startActivity(intent);
    }

    public void liftMeteor(View v){
        Intent intent = new Intent(MainActivity.this, LiftMeteor.class);
        startActivity(intent);
    }

    public void chargeBattery(View v){
        Intent intent = new Intent(MainActivity.this, ShakeHands.class);
        startActivity(intent);
    }

    public void swordFight(View v){
        Intent intent = new Intent(MainActivity.this, SwordFight.class);
        startActivity(intent);
    }

    public void mrMime(View v){
        Intent intent = new Intent(MainActivity.this, MrMime.class);
        startActivity(intent);
    }

    public void scoutGame(View v){
        Intent intent = new Intent(MainActivity.this, ScoutGame.class);
        startActivity(intent);
    }


    public void bombSquad(View v) {
        Intent intent = new Intent(MainActivity.this, BombSquad.class);
        startActivity(intent);
    }

    public void shuffleGame(View v){
        Intent intent = new Intent(MainActivity.this, ShuffleGame.class);
        startActivity(intent);
    }

    public void blowMic(View v){
        Intent intent = new Intent(MainActivity.this, BlowMic.class);
        startActivity(intent);
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTransitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            try{LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);}

            catch(Exception e){
                e.printStackTrace();
            }
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            storeUserLocation();
            //saveGeofence();
            //drawGeofence();
        } else {
            // inform about fail
        }
    }

    private final String KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE";
    private final String KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE";

    // Clear Geofence
    private void clearGeofence() {
        Log.d(TAG, "clearGeofence()");
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                createGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if ( status.isSuccess() ) {
                    // remove drawing
                    // removeGeofenceDraw();
                }
            }
        });
    }


    private class AsyncStoreCoordinates extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        String playerID;
        Double latitude;
        Double longitude;

        public AsyncStoreCoordinates(String playerID, Double latitude, Double longitude){
            this.playerID = playerID;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://rasmuslundrosenqvist.000webhostapp.com/P9/storeCoordinatesGetStatus.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                //conn.setReadTimeout(READ_TIMEOUT);
                //conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("playerRole", playerRole)
                        .appendQueryParameter("latitude", String.valueOf(latitude))
                        .appendQueryParameter("longitude", String.valueOf(longitude));
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if (result.equalsIgnoreCase("failure")) {

                Toast toast = Toast.makeText(getApplicationContext(), "Could not insert. Try again", Toast.LENGTH_LONG);
                toast.show();


            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Context context = getApplicationContext();
                CharSequence text = "Connection failed. Try again";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }

            else{
                //Try to parse the coordinates from JSON
                //Toast toast = Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT);
                //toast.show();
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String status = obj.getString("status");

                        if(booStatus == false) {
                            if (status.equals("active")) {
                                //this makes sure that mini game only starts, if it's not started already.
                                booStatus = true;
                                //start mini game!
                                Toast toast = Toast.makeText(getApplicationContext(), "mini game started", Toast.LENGTH_SHORT);
                                toast.show();

                                Intent intent = new Intent(MainActivity.this, MiniGameDrink.class);
                                startActivity(intent);
                            }
                        }

                    }

                }
                catch (JSONException e){

                }


            }


        }
    }

}
