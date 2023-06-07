package com.example.logingps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.logingps.models.CrewLocationHistoryDAO;
import com.example.logingps.models.Firebase;
import com.example.logingps.models.LatestCrewLocationsDAO;
import com.example.logingps.utils.NotificationBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;


public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private FusedLocationProviderClient mFusedLocationClientCrewLocationHistory;   // responsible for retrieving the location
    private final static long UPDATE_INTERVAL_CREW_LOCATION_HISTORY = 5 * 60 * 1000 + 4000;  /* 5 min, 4 secs */     // time that gonna pass before an update is made
    private final static long FASTEST_INTERVAL_CREW_LOCATION_HISTORY = 5 * 60 * 1000 + 2000; /* 5 min, 2 secs */  // fastest allowed update time

    private FusedLocationProviderClient mFusedLocationClientLatestLocation;
    private final static long UPDATE_INTERVAL_LATEST_LOCATION = 4 * 1000;  /* 4 secs */     // time that gonna pass before an update is made
    private final static long FASTEST_INTERVAL_LATEST_LOCATION = 2000; /* 2 sec */  // fastest allowed update time

    private LocationCallback locationCallBackCrewLocationHistory;
    private LocationCallback locationCallBackLatestLocation;
    private int id = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationClientCrewLocationHistory = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClientLatestLocation = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {  // starting a service for api 26 and above
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Sending GPS location to server...")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  // called when the service starts
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        Firebase.getDatabase()
                .collection("account")
                .document(Firebase.getCurrentUser().getUid())
                .collection("work_orders")
                .whereEqualTo("date_completed", null)
                //.orderBy("date_assigned", Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("E-BILECO", "Listen failed.", e);
                            return;
                        }

                        for(DocumentChange dc: queryDocumentSnapshots.getDocumentChanges()) {
                            switch(dc.getType()) {
                                case ADDED:
                                    if (!queryDocumentSnapshots.getMetadata().isFromCache()) {  // notify only if newly added documents came from the server
                                        Notification notification = NotificationBuilder.createAndroidNotification(getApplicationContext(), dc.getDocument());
                                        NotificationManagerCompat notificationMgr =
                                                NotificationManagerCompat.from(getApplicationContext());
                                        notificationMgr.notify(id, notification);
                                        id++;   // must provide unique id for each notification
                                    }
                                    break;
                            }
                        }
                    }
                });

        return START_NOT_STICKY;    // use for service that should only be running while processing any command sent to them
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates

        LocationRequest mLocationRequestHighAccuracyMinute = new LocationRequest();   // is use for constantly retrieving location at an interval
        mLocationRequestHighAccuracyMinute.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracyMinute.setInterval(UPDATE_INTERVAL_CREW_LOCATION_HISTORY);  // how often to retrieve the location
        mLocationRequestHighAccuracyMinute.setFastestInterval(FASTEST_INTERVAL_CREW_LOCATION_HISTORY);

        LocationRequest mLocationRequestHighAccuracySeconds = new LocationRequest();   // is use for constantly retrieving location at an interval
        mLocationRequestHighAccuracySeconds.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracySeconds.setInterval(UPDATE_INTERVAL_LATEST_LOCATION);  // how often to retrieve the location
        mLocationRequestHighAccuracySeconds.setFastestInterval(FASTEST_INTERVAL_LATEST_LOCATION);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,    // check if the user has the required permissions
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }

        Log.d(TAG, "getLocation: getting location information.");

        locationCallBackCrewLocationHistory = new LocationCallback() {  // recurring request being made every certain interval
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult: got location result.");
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                    //Toast.makeText(getApplicationContext(), "Lat/long: " + geoPoint.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Lat/long:" + geoPoint.toString());

                    Date dateMillis = Calendar.getInstance().getTime();
                    saveCrewLocationHistory(location, dateMillis);
                }
            }
        };
        locationCallBackLatestLocation = new LocationCallback() {  // recurring request being made every certain interval
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult: got location result.");
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                    //Toast.makeText(getApplicationContext(), "Lat/long: " + geoPoint.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Lat/long:" + geoPoint.toString());

                    Date dateMillis = Calendar.getInstance().getTime();
                    saveLatestLocation(location, dateMillis);
                }
            }
        };
        mFusedLocationClientCrewLocationHistory.requestLocationUpdates(mLocationRequestHighAccuracyMinute,
                locationCallBackCrewLocationHistory, Looper.myLooper());
        mFusedLocationClientLatestLocation.requestLocationUpdates(mLocationRequestHighAccuracySeconds,
                locationCallBackLatestLocation, Looper.myLooper());
        // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    private void saveCrewLocationHistory(Location location, Date dateCreated) {
        if (Firebase.getCurrentUser() != null) {
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            CrewLocationHistoryDAO crewLocationHistoryDAO = new CrewLocationHistoryDAO(this);
            crewLocationHistoryDAO.insertLocationHistory(Firebase.getCurrentUser().getUid(), geoPoint, dateCreated, location.getAccuracy(), location.getSpeed())
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Log.d("LocationService", "onComplete: \n inserted user location history into database");
                            }
                        }
                    });
        } else {
            Log.e(TAG, "No current user, stopping location service.");
            mFusedLocationClientCrewLocationHistory.removeLocationUpdates(locationCallBackCrewLocationHistory);
            stopSelf();
        }
    }

    private void saveLatestLocation(Location location, Date dateCreated) {
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (Firebase.getCurrentUser() != null) {
            LatestCrewLocationsDAO latestCrewLocationsDAO = new LatestCrewLocationsDAO(this);
            latestCrewLocationsDAO.insertLocationHistory(Firebase.getCurrentUser(), geoPoint, dateCreated, location.getAccuracy(), location.getSpeed());
        } else {
            Log.e(TAG, "No current user, stopping location service.");
            mFusedLocationClientLatestLocation.removeLocationUpdates(locationCallBackLatestLocation);
            stopSelf();
        }
    }
}
