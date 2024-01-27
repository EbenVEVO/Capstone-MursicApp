package com.example.capstone_mursicapp;

import java.util.List;

public class UserListModel {
    String username;
    String userID;
    String profilePic;
    public UserListModel(String username, String userID, String profilePic){
        this.username=username;
        this.profilePic = profilePic;
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
