package com.is416.smujio.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Gods on 3/4/2018.
 */

public class Event {

    private int id;
    private double latitude;
    private double longitude;
    private Date initTime;
    private int status;
    private String type;
    private ArrayList<Integer> participants;

    public Event(int id, double latitude, double longitude, Date initTime, int status, String type, ArrayList<Integer> participants) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.initTime = initTime;
        this.status = status;
        this.type = type;
        this.participants = participants;
    }

    public int getId() {
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

    public ArrayList<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Integer> participants) {
        this.participants = participants;
    }
}
