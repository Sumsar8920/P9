package com.example.rasmus.p9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MrMime extends AppCompatActivity implements SensorEventListener {

    public Sensor mySensor;
    public SensorManager SM;
    ImageButton button1;
    Boolean player1Ready = false;
    Boolean player2Ready = false;
    List<Integer> xList;
    List<Integer> yList;
    List<Integer> zList;
    TextView player1, player2, guide;
    int counter = 0;
    int average = 0;
    int sum = 0;
    String playerRole;
    String player1X = "", player1Y = "", player1Z = "";
    int rangeMinusX;
    int rangePlusX;
    int rangeMinusY;
    int rangePlusY;
    int rangeMinusZ;
    int rangePlusZ;
    int counterWin = 0;

    public Handler handler = new Handler();
    public int delay = 1000; //milliseconds
    int imageCounter = 0;
    ImageView mrMimeImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreen();
        //setContentView(R.layout.activity_mr_mime);
        guide = (TextView) findViewById(R.id.guide);

        //get number of player
        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        if(playerRole.equals("2")){
            guide.setText("Wait for other player");
            checkMotion();
        }

        mrMimeImage = (ImageView) findViewById(R.id.mrMimeImage);

        xList = new ArrayList<>();
        yList = new ArrayList<>();
        zList = new ArrayList<>();

       player1 = (TextView) findViewById(R.id.player1);
       player2 = (TextView) findViewById(R.id.player2);

        changeImage();


        button1 = (ImageButton)findViewById(R.id.button1);

        // Create sensor manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // MiniGameDrink sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        button1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (playerRole.equals("1")){
                            // Register sensor listener
                            SM.registerListener(MrMime.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                            guide.setText("Do motion!");
                        }

                        if(playerRole.equals("2") && player2Ready == true){
                            SM.registerListener(MrMime.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // End
                        SM.unregisterListener(MrMime.this);
                        if(playerRole.equals("1")) {
                            new AsyncStoreMotion(String.valueOf(xAverage()), String.valueOf(yAverage()), String.valueOf(zAverage())).execute();
                        }

                        if(playerRole.equals("2")){
                            int player2X = xAverage();
                            int player2Y = yAverage();
                            int player2Z = zAverage();
                            player2.setText(String.valueOf(player2X) + " " + String.valueOf(player2Y) + " " + String.valueOf(player2Z));

                           if(player2X > rangeMinusX && player2X < rangePlusX &&
                                   player2Y > rangeMinusY && player2Y < rangePlusY
                            && player2Z > rangeMinusZ && player2Z < rangePlusZ){

                               counterWin++;
                               if(counterWin == 3){
                                   Intent intent = new Intent(MrMime.this, Victory.class);
                                   startActivity(intent);
                               }

                               Toast toast = Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT);
                               toast.show();

                               //delete all from the table after success, so it only gets the new motion from player1
                               new AsyncDelete().execute();

                               player2Ready = false;
                               SM.unregisterListener(MrMime.this);

                               //reset Strings
                               player1X = "";
                               player1Y = "";
                               player1Z = "";

                               //clear arraylists
                               xList.clear();
                               yList.clear();
                               zList.clear();

                           }

                           else{
                               Toast toast = Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT);
                               toast.show();
                               xList.clear();
                               yList.clear();
                               zList.clear();
                           }
                        }
                        counter++;
                        xList.clear();
                        sum = 0;
                        yList.clear();
                        sum = 0;
                        zList.clear();
                        sum = 0;

                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        xList.add((int)event.values[0]);
        yList.add((int)event.values[1]);
        zList.add((int)event.values[2]);

        Log.i("X:", String.valueOf(xList));
        Log.i("Y:", String.valueOf(yList));
        Log.i("Z:", String.valueOf(zList));

    }

    public int xAverage(){

        for(int i = 0; i < xList.size(); i++){
            sum = sum + xList.get(i);
        }
        average = sum/xList.size();

        return average;
    }

    public int yAverage(){

        for(int i = 0; i < yList.size(); i++){
            sum = sum + yList.get(i);
        }
        average = sum/yList.size();

        return average;
    }

    public int zAverage(){

        for(int i = 0; i < zList.size(); i++){
            sum = sum + zList.get(i);
        }
        average = sum/zList.size();

        return average;
    }

    public void checkMotion(){
        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(player1X.equals("") && player1Y.equals("") && player1Z.equals("")){
                new AsyncCheckMotion().execute();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private class AsyncStoreMotion extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MrMime.this);
        HttpURLConnection conn;
        URL url = null;

        String X;
        String Y;
        String Z;

        public AsyncStoreMotion(String X, String Y, String Z){
            this.X = X;
            this.Y = Y;
            this.Z = Z;
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
                url = new URL("http://rasmuslundrosenqvist.000webhostapp.com/P9/storeMotion.php");

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
                        .appendQueryParameter("X", X)
                        .appendQueryParameter("Y", Y)
                        .appendQueryParameter("Z", Z);
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
                guide.setText("Wait for other player to mimic");
            }


        }
    }

    private class AsyncCheckMotion extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MrMime.this);
        HttpURLConnection conn;
        URL url = null;

        public AsyncCheckMotion(){
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
                url = new URL("http://rasmuslundrosenqvist.000webhostapp.com/P9/checkMotion.php");

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
                .appendQueryParameter("X", playerRole);
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

            if (result.equalsIgnoreCase("false")) {


            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Context context = getApplicationContext();
                CharSequence text = "Connection failed. Try again";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }

            else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        player1X = obj.getString("X");
                        player1Y = obj.getString("Y");
                        player1Z = obj.getString("Z");
                    }
                } catch (JSONException e) {

                }

                if(playerRole.equals("2")){
                    guide.setText("Do motion!");
                    player1.setText(player1X + " " + player1Y + " " + player1Z);
                    rangeMinusX = Integer.parseInt(player1X) - 3;
                    rangePlusX = Integer.parseInt(player1X) + 3;
                    rangeMinusY = Integer.parseInt(player1Y) - 3;
                    rangePlusY = Integer.parseInt(player1Y) + 3;
                    rangeMinusZ = Integer.parseInt(player1Z) - 3;
                    rangePlusZ = Integer.parseInt(player1Z) + 3;
                    player2Ready = true;
                }
            }


        }
    }

    private class AsyncDelete extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MrMime.this);
        HttpURLConnection conn;
        URL url = null;

        public AsyncDelete(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            //pdLoading.setMessage("\tLoading...");
            //pdLoading.setCancelable(false);
            //pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://rasmuslundrosenqvist.000webhostapp.com/P9/deleteMrMime.php");

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
                        .appendQueryParameter("X", playerRole);
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

            if (result.equalsIgnoreCase("false")) {


            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Context context = getApplicationContext();
                CharSequence text = "Connection failed. Try again";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }

            else {

            }


        }
    }

    public void changeImage(){
        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                imageCounter ++;
                if (imageCounter == 1) {
                    mrMimeImage.setImageResource(R.drawable.still);
                }
                if (imageCounter == 2) {
                    mrMimeImage.setImageResource(R.drawable.left1);
                }
                if (imageCounter == 3) {
                    mrMimeImage.setImageResource(R.drawable.left2);
                }
                if (imageCounter == 4) {
                    mrMimeImage.setImageResource(R.drawable.still);
                }

                if (imageCounter == 5) {
                    mrMimeImage.setImageResource(R.drawable.right1);
                }
                if (imageCounter == 6) {
                    mrMimeImage.setImageResource(R.drawable.right2);
                }
                if (imageCounter == 7) {
                    mrMimeImage.setImageResource(R.drawable.finish);
                    imageCounter = 0;
                }

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public void fullscreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_mr_mime);
    }


}
