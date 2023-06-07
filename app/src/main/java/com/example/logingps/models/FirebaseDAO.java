package com.example.logingps.models;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDAO {
    FirebaseFirestore database;
    Context context;

    public FirebaseDAO(Context context) {
        database = Firebase.getDatabase();
        this.context = context;
    }
}
