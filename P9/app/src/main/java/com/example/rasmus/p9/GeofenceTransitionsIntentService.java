package com.example.rasmus.p9;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rasmus on 10-10-2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    protected static final String TAG = "GeofenceTransitionsIS";

    public GeofenceTransitionsIntentService() {
        super(TAG);  // use TAG to name the IntentService worker thread
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + event.getErrorCode());
            return;
        }
        String description = getGeofenceTransitionDetails(event);
        sendNotification(description);
    }

    private static String getGeofenceTransitionDetails(GeofencingEvent event) {
        String transitionString =
                GeofenceStatusCodes.getStatusCodeString(event.getGeofenceTransition());
        List triggeringIDs = new ArrayList();
        for (Geofence geofence : event.getTriggeringGeofences()) {
            triggeringIDs.add(geofence.getRequestId());
        }
        return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs));
    }

    private void sendNotification(String notificationDetails) {

        //create vibrator object and pass the context
        Vibration vibrator = new Vibration(this);
        //call method that vibrates the phone
        vibrator.vibrate();


        try {
            // Create an explicit content Intent that starts MainActivity.
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

            // Get a PendingIntent containing the entire back stack.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class).addNextIntent(notificationIntent);
            PendingIntent notificationPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get a notification builder that's compatible with platform versions >= 4
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            // Define the notification settings.
            builder.setColor(Color.RED)
                    .setContentTitle(notificationDetails)
                    .setContentText("Click notification to return to App")
                    .setContentIntent(notificationPendingIntent)
                    .setSmallIcon(R.drawable.player_search2)
                    .setAutoCancel(true);

            // Fire and notify the built Notification.
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        }


}