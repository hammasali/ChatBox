package com.example.chatbox.models;

public class UserModel{
    String userName,lastMsg,time,userID,imgUri;

    public UserModel(String userName, String lastMsg, String time, String userID, String imgUri) {
        this.userName = userName;
        this.lastMsg = lastMsg;
        this.time = time;
        this.userID = userID;
        this.imgUri = imgUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }
}
