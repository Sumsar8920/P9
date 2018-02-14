package com.example.rasmus.p9;

/**
 * Created by rasmu on 09-02-2018.
 */

public class Event {

    double latitude, longitude;

    public Event(){

    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

}
