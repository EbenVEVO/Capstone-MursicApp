package com.example.capstone_mursicapp;

public class PostModel {
    String pUsername, pProfilePic, pImage, pTime;

    public PostModel(String pUsername, String pImage, String pTime, String pProfilePic){
        this.pImage = pImage;
        this.pTime = pTime;
        this.pUsername = pUsername;
        this.pProfilePic = pProfilePic;

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

    public String getpTime() {
        return pTime;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public void setpProfilePic(String pProfilePic) {
        this.pProfilePic = pProfilePic;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public void setpUsername(String pUsername) {
        this.pUsername = pUsername;
    }
}
