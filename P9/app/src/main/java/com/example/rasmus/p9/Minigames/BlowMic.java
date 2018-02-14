package com.example.rasmus.p9.Minigames;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rasmus.p9.R;

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

public class BlowMic extends AppCompatActivity {

    String playerRole;
    Boolean check = true;
    String[] status = new String[4];
    int counter = 20;
    TextView timer;
    ImageButton button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blow_mic);

        timer = (TextView) findViewById(R.id.timer);
        button1 = (ImageButton) findViewById(R.id.button1);

        //get number of player
        SharedPreferences shared = getSharedPreferences("your_file_name", MODE_PRIVATE);
        playerRole = (shared.getString("PLAYERROLE", ""));

        checkMotion();

        button1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //new BlowMic.AsyncSetStatus().execute();
                        break;
                    case MotionEvent.ACTION_UP:
                        // End

                        break;
                }
                return false;
            }
        });


    }

    public void checkMotion(){
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(check == true) {
                    new BlowMic.AsyncCheckStatus().execute();
                }

                //set check == false when I want this to stop

                handler.postDelayed(this, delay);

            }
        }, delay);
    }

    public void startCountdownTimer(){
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                counter --;
                timer.setText(String.valueOf(counter));
                handler.postDelayed(this, delay);

            }
        }, delay);

    }

    private class AsyncCheckStatus extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(BlowMic.this);
        HttpURLConnection conn;
        URL url = null;

        public AsyncCheckStatus(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://rasmuslundrosenqvist.000webhostapp.com/P9/checkStatus.php");

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
                        .appendQueryParameter("PLAYER_ROLE", playerRole);
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
                int counter = 0;
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        status[counter] = obj.getString("status");
                        counter ++;
                        if(counter == 4){
                            counter = 0;
                        }

                    }
                } catch (JSONException e) {

                }

                if(status[0].equals("READY") && status[1].equals("READY") &&
                        status[2].equals("READY") && status[3].equals("READY")){

                    check = false;
                    startCountdownTimer();
                }
            }

        }
    }

}
