package com.example.capstone_mursicapp;

public class CommentsModel {
    String userID, username, comment, profilePic;
    long time;
    public CommentsModel(String userID, String username, String comment, String profilePic, long time){
        this.userID=userID;
        this.username = username;
        this.comment = comment;
        this.profilePic = profilePic;
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public String getComment() {
        return comment;
    }

    public long getTime() {
        return time;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getUsername() {

        return username;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
