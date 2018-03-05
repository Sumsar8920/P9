package com.example.rasmus.p9.Minigames;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.rasmus.p9.R;

public class Gyroscope extends AppCompatActivity {

    SensorManager sensorManager;
    Sensor rotationSensor;
    SensorEventListener rotationSensorListener;
    TextView txt1, txt2, txt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        txt1 = (TextView)findViewById(R.id.txt1);
        txt2 = (TextView)findViewById(R.id.txt2);
        txt3 = (TextView)findViewById(R.id.txt3);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Create listener
        rotationSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // More code goes here
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, sensorEvent.values);

                // Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);

                // Convert to orientations
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }

                txt1.setText(String.valueOf(orientations[0]));
                txt2.setText(String.valueOf(orientations[1]));
                txt3.setText(String.valueOf(orientations[2]));


                if(orientations[2] > 45) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                } else if(orientations[2] < -45) {
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if(Math.abs(orientations[2]) < 10) {
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(rotationSensorListener, rotationSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rotationSensorListener);
    }
}
