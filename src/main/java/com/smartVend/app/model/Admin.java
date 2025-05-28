package com.smartVend.app.model;

public class Admin extends User {
    public Admin() {
        super();
    }

    public Admin(String id, String email, String name, String surname, String hashedPassword) {
        super(id, email, name, surname, hashedPassword);
    }
}
