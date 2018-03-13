package com.is416.smujio.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Gods on 3/4/2018.
 */

public class Event {

    private long id;
    private User owner;
    private double latitude;
    private double longitude;
    private String location;
    private Date initTime;
    private int status;
    private String type;
    private ArrayList<User> participants;

    public Event(long id, User owner, double latitude, double longitude, String location, Date initTime, int status, String type, ArrayList<User> participants) {
        this.id = id;
        this.owner = owner;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.initTime = initTime;
        this.status = status;
        this.type = type;
        this.participants = participants;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getInitTime() {
        return initTime;
    }

    public void setInitTime(Date initTime) {
        this.initTime = initTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<User> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
