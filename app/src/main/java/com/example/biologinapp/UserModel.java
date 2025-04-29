package com.example.biologinapp;

public class UserModel {
    public String name;
    public String email;
    public String dob;
    public String address;

    // Default constructor (required for Firebase)
    public UserModel() {
    }

    // Constructor with fields
    public UserModel(String name, String email, String dob, String address) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.address = address;
    }
}
