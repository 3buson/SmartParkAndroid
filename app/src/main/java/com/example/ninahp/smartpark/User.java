package com.example.ninahp.smartpark;

/**
 * Created by NinaHP on 11.7.2015.
 */
public class User {
    private int requestId;
    private String uid;
    private String distance;
    private String phone;
    private float reputation;

    public User(int requestId, String uid, String distance, String phone, float reputation) {
        this.requestId = requestId;
        this.uid = uid;
        this.distance = distance;
        this.phone = phone;
        this.reputation = reputation;
    }

    @Override
    public String toString(){
        return this.uid + " is " + this.distance + "m away! \nREPUTATION: " + this.reputation ;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getReputation() {
        return reputation;
    }

    public void setReputation(float reputation) {
        this.reputation = reputation;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
