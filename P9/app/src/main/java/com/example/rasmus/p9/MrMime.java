package com.example.rasmus.p9;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MrMime extends AppCompatActivity implements SensorEventListener {

    public Sensor mySensor;
    public SensorManager SM;
    ImageButton button1;
    Boolean player1Ready = false;
    List<Integer> xList;
    List<Integer> yList;
    List<Integer> zList;
    TextView player1, player2, guide;
    int counter = 0;
    int average = 0;
    int sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mr_mime);

        xList = new ArrayList<>();
        yList = new ArrayList<>();
        zList = new ArrayList<>();

        player1 = (TextView) findViewById(R.id.player1);
        player2 = (TextView) findViewById(R.id.player2);
        guide = (TextView) findViewById(R.id.guide);

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
                        player1Ready = true;
                        if (player1Ready == true){
                            // Register sensor listener
                            SM.registerListener(MrMime.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                            guide.setText("Do motion!");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // End
                        SM.unregisterListener(MrMime.this);
                        counter++;
                        guide.setText("Place thumb on marker");
                        if(counter == 1){
                            String averageScore = "X: " + String.valueOf(xAverage()) + " Y: " +String.valueOf(yAverage()) + " Z: " + String.valueOf(zAverage());
                            player1.setText(averageScore);
                            xList.clear();
                            sum = 0;
                            yList.clear();
                            sum = 0;
                            zList.clear();
                            sum = 0;
                        }

                        if(counter == 2){
                            String averageScore = "X: " + String.valueOf(xAverage()) + " Y: " +String.valueOf(yAverage()) + " Z: " + String.valueOf(zAverage());
                            player2.setText(averageScore);
                            xList.clear();
                            sum = 0;
                            yList.clear();
                            sum = 0;
                            zList.clear();
                            sum = 0;
                            counter = 0;
                        }
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

}
