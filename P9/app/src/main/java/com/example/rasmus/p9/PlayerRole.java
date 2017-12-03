package com.example.rasmus.p9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class PlayerRole extends AppCompatActivity {

    EditText editPlayerRole;
    Button addPlayer;
    String strPlayerRole;
    String latitude;
    String longitude;
    String radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreen();
        //setContentView(R.layout.activity_player_role);

        editPlayerRole = (EditText) findViewById(R.id.playerRole);
        addPlayer = (Button) findViewById(R.id.addPlayer);

        //Flashlight obj = new Flashlight(this, 1000);
        //obj.startLight();

    }

    public void addPlayer(View view){
        //get text from EditText
        strPlayerRole = editPlayerRole.getText().toString();

        //save to sharedPreferences
        SharedPreferences prefs = getSharedPreferences("your_file_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("PLAYERROLE", strPlayerRole);
        editor.commit();

        //add to database
        //new AsyncAddPlayer(strPlayerRole).execute();

        //get coordinates
        new AsyncGetCoordinates().execute();
        //Intent intent = new Intent(PlayerRole.this, MainActivity.class);
        //startActivity(intent);

    }

    private class AsyncAddPlayer extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(PlayerRole.this);
        HttpURLConnection conn;
        URL url = null;

        String strPlayerRole;

        public AsyncAddPlayer(String strPlayerRole){
            this.strPlayerRole = strPlayerRole;
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
                url = new URL("http://rasmuslundrosenqvist.000webhostapp.com/P9/addPlayer.php");

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
                        .appendQueryParameter("playerRole", strPlayerRole);
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
                //If inserted successfully
                Toast toast = Toast.makeText(getApplicationContext(), "Player added", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(PlayerRole.this, MainActivity.class);
                startActivity(intent);

            }


        }
    }

    private class AsyncGetCoordinates extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(PlayerRole.this);
        HttpURLConnection conn;
        URL url = null;

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
                url = new URL("http://rasmuslundrosenqvist.000webhostapp.com/P9/getCoordinates.php");

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
                        .appendQueryParameter("playerRole", strPlayerRole);
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
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        latitude = obj.getString("latitude");
                        longitude = obj.getString("longitude");
                        radius = obj.getString("radius");
                    }

                }
                catch (JSONException e){

                }

                Intent intent = new Intent(PlayerRole.this, GameScreen.class);
                intent.putExtra("LATITUDE", latitude );
                intent.putExtra("LONGITUDE", longitude);
                intent.putExtra("RADIUS", radius);
                startActivity(intent);

            }


        }
    }

    public void fullscreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_player_role);
    }



}
