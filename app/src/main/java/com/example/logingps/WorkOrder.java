package com.example.logingps;

public class WorkOrder {
    private String date;
    private String name;
    private String description;
    private String address;

    public WorkOrder(String date, String name, String description, String address) {
        this.date = date;
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
