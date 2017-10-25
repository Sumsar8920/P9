package com.example.rasmus.p9;

/**
 * Created by Rasmus on 10-10-2017.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class Location {

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 200;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();
    static {
        // AAU CREATE building
        LANDMARKS.put("CREATE", new LatLng(57.048424, 9.930262));

        // Googleplex.
        LANDMARKS.put("Cassiopeia", new LatLng(57.012301, 9.991394));

        // Test
        //LANDMARKS.put("SFO", new LatLng(37.621313,-122.378955));
    }
}


