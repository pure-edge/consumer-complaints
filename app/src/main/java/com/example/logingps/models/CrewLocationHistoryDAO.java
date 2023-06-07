package com.example.logingps.models;

import android.content.Context;

import com.example.logingps.R;
import com.example.logingps.models.Firebase;
import com.example.logingps.models.LocationHistory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class CrewLocationHistoryDAO extends FirebaseDAO {
    public CrewLocationHistoryDAO(Context context) {
        super(context);
    }

    public Task<DocumentReference> insertLocationHistory(String uid, GeoPoint location, Date dateCreated, float accuracy, float speed) {
        CollectionReference locationHistory = database.collection(FirebaseConstants.COLLECTION_ACCOUNT)
                .document(uid)
                .collection(FirebaseConstants.SUBCOLLECTION_LOCATION_HISTORY);
        return locationHistory.add(new LocationHistory(location, dateCreated, accuracy, speed));
    }
}
