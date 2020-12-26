package com.example.chatbox.models;

public class userSetters {
    String name,phone,key,email;

    public userSetters() {
    }

    public userSetters(String name, String phone, String key, String email) {
        this.name = name;
        this.phone = phone;
        this.key = key;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
