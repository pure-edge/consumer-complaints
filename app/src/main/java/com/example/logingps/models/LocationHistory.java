package com.example.logingps.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class LocationHistory {
    private String name;
    private GeoPoint location;
    @ServerTimestamp
    private Date date_time_fetched;
    private float accuracy; // in meters
    private float speed;    // in meters per second

    public LocationHistory(GeoPoint location, Date dateMillis, float accuracy, float speed) {
        this(null, location, dateMillis, accuracy, speed);
    }

    public LocationHistory(String name, GeoPoint location, Date dateMillis, float accuracy, float speed) {
        this.name = name;
        this.location = location;
        this.date_time_fetched = dateMillis;
        this.accuracy = accuracy;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Date getDate_time_fetched() {
        return date_time_fetched;
    }

    public void setDate_time_fetched(Date date_time_fetched) {
        this.date_time_fetched = date_time_fetched;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
