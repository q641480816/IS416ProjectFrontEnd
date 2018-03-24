package com.is416.smujio.model;

import com.is416.smujio.util.General;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class User {
    private long accountId;

    private String email;
    private String passwordHash;
    private String passwordSalt;

    private String nickName;
    private String avatar;
    private Date dateOfBirth;
    private int gender;
    private Date date;
    private boolean inEventStatus;

    public User() {super();}

    public User(String email, String passwordHash, String passwordSalt, String nickName, Date dateOfBirth, int gender, Date date, String avatar) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.nickName = nickName == null ? "Edit Nick Name" : nickName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.date = date;
        this.avatar = avatar;
    }

    public User(long accountId, String email, String nickName, Date dateOfBirth, int gender, Date date, String avatar, boolean inEventStatus){
        this.accountId = accountId;
        this.email = email;
        this.nickName = nickName == null ? "Edit Nick Name" : nickName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.date = date;
        this.avatar = avatar;
        this.inEventStatus = inEventStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public boolean isInEventStatus() {
        return inEventStatus;
    }

    public void setInEventStatus(boolean inEventStatus) {
        this.inEventStatus = inEventStatus;
    }

    public static User JsonToObject(JSONObject jsonObject) throws JSONException, ParseException {
        long id = jsonObject.getLong(General.ACCOUNTID);
        String email = (String) jsonObject.get(General.EMAIL);
        String nickName = (String) jsonObject.get(General.NICKNAME);
        Date birthday  = General.SDF.parse((String) jsonObject.get(General.DATEOFBIRTH));
        int gender = (Integer) jsonObject.get(General.GENDER);
        Date date = General.SDF.parse((String) jsonObject.get(General.DATE));
        String avatar = jsonObject.getString(General.AVATAR) == null ? "null" : jsonObject.getString(General.AVATAR);
        boolean status = jsonObject.getBoolean(General.USERSTATUS);


        return (new User(id,email,nickName,birthday,gender,date,avatar,status));
    }
}
