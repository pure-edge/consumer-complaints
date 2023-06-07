package com.example.logingps.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase {
    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static FirebaseFirestore getDatabase(){
        return FirebaseFirestore.getInstance();
    }

    /*public static DatabaseReference getCollection(String name) {
        return FirebaseDatabase.getInstance().getReference().child(name);
    }

    public static DatabaseReference getDatabase(){
        return FirebaseDatabase.getInstance().getReference();
    }*/


}
