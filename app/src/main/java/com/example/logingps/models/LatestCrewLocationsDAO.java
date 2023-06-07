package com.example.logingps.models;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LatestCrewLocationsDAO extends FirebaseDAO {
    public LatestCrewLocationsDAO(Context context) {
        super(context);
    }

    public Task<Void> insertLocationHistory(FirebaseUser user, GeoPoint location, Date date, float accuracy, float speed){
        DocumentReference document = database.collection(FirebaseConstants.COLLECTION_LATEST_CREW_LOCATIONS)
                .document(user.getUid());

        String crewName = Firebase.getCurrentUser().getDisplayName();
        return document.set(new LocationHistory(crewName, location, date, accuracy, speed), SetOptions.merge());
    }
}
