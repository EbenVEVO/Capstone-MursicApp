package com.example.capstone_mursicapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

public class PostModel {
    String  pImage, userID;
    Timestamp timeStamp;

    public PostModel(String pImage, Timestamp timeStamp, String userID){
        this.pImage = pImage;
        this.timeStamp = timeStamp;
        this.userID = userID;

    }

    public String getpImage() {
        return pImage;
    }

    public Timestamp gettimeStamp() {
        return timeStamp;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }


    public void setpTime(FieldValue pTime) {
        this.timeStamp = timeStamp;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
