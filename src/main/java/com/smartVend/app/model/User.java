package com.smartVend.app.model;


public class User {
    private String id;
    private String email;
    private String name;
    private String surname;
    private String hashedPassword;

    public User() {}

    public User(String id, String email, String name, String surname, String hashedPassword) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.hashedPassword = hashedPassword;
    }

}