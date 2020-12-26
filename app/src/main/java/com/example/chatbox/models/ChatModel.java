package com.example.chatbox.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatModel {
    String sender,reciever,message,time;

    public ChatModel(String sender, String reciever, String message, String time) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
