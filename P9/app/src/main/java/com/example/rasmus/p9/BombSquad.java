package com.example.rasmus.p9;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;




public class BombSquad extends AppCompatActivity {


    public int blow_value;
    public TextView mic;
    public Button micButton;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_bomb_squad);

        MicPermissions();
        mic = (TextView) findViewById(R.id.mic);
        micButton = (Button)findViewById(R.id.micButtonTest);


    }

    public void MicPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);

        }
    }


    public boolean isBlowing(View v) {
        boolean recorder=true;

        int minSize = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);


        short[] buffer = new short[minSize];

        ar.startRecording();
        while(recorder)
        {

            ar.read(buffer, 0, minSize);
            for (short s : buffer)
            {
                if (Math.abs(s) > 18000)   //DETECT VOLUME (IF I BLOW IN THE MIC)
                {
                    blow_value=Math.abs(s);
                    mic.setText("Blow Value=" +blow_value);
                    //System.out.println("Blow Value="+blow_value);
                    ar.stop();
                    recorder=false;

                    return true;

                }

            }
        }
        return false;

    }

}
