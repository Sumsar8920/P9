package com.example.rasmus.p9.Other;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rasmu on 01-03-2018.
 */

public class Database {

    public static FirebaseDatabase database;
    public static DatabaseReference rootReference;


    public Database(){
        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
    }

    public static DatabaseReference getDatabaseRootReference(){
        return rootReference;
    }

}
