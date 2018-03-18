package com.is416.smujio.model;

import com.is416.smujio.util.General;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.text.ParseException;
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

    public int getParticipantsCount(){
        return participants.size();
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

    public static Event JsonToObject(JSONObject jsonObject) throws JSONException, ParseException {
        long id = jsonObject.getLong(General.ID);
        double latitude = (Double) jsonObject.get(General.LATITUDE);
        double longitude = (Double) jsonObject.get(General.LONGITUDE);
        Date initTime = General.SDF.parse((String) jsonObject.get(General.INITTIME));
        int status = (Integer) jsonObject.get(General.EVENTSTATUS);
        String type = (String) jsonObject.get(General.TYPE);
        User owner = User.JsonToObject((JSONObject) jsonObject.get(General.OWNER));
        String location = (String) jsonObject.get(General.LOCATION);
        ArrayList<User> participants = new ArrayList<>();
        JSONArray tempUsers = (JSONArray) jsonObject.get(General.PARTICIPANTS);
        for(int i = 0; i < tempUsers.length(); i ++){
            JSONObject jb = tempUsers.getJSONObject(i);
            participants.add(User.JsonToObject(jb));
        }

        return (new Event(id,owner,latitude,longitude,location,initTime,status,type,participants));
    }
}
