package com.example.capstone_mursicapp;

public class PostModel {
    String  pImage, userID;
    long pTime;

    public PostModel(String pImage, long pTime, String userID){
        this.pImage = pImage;
        this.pTime = pTime;
        this.userID = userID;

    }

    public String getpImage() {
        return pImage;
    }

    public long getpTime() {
        return pTime;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }


    public void setpTime(long pTime) {
        this.pTime = pTime;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
