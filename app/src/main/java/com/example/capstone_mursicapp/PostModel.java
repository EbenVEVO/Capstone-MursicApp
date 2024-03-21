package com.example.capstone_mursicapp;

public class PostModel {
    String pUsername, pProfilePic, pImage, userID;
    long pTime;

    public PostModel(String pUsername, String pImage, long pTime, String pProfilePic, String userID){
        this.pImage = pImage;
        this.pTime = pTime;
        this.pUsername = pUsername;
        this.pProfilePic = pProfilePic;
        this.userID = userID;

    }

    public String getpImage() {
        return pImage;
    }

    public String getpProfilePic() {
        return pProfilePic;
    }

    public String getpUsername() {
        return pUsername;
    }

    public long getpTime() {
        return pTime;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public void setpProfilePic(String pProfilePic) {
        this.pProfilePic = pProfilePic;
    }

    public void setpTime(long pTime) {
        this.pTime = pTime;
    }

    public void setpUsername(String pUsername) {
        this.pUsername = pUsername;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
