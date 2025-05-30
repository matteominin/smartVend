package com.smartvend.app.model.user;

public class Worker extends User {
    private boolean isActive;

    public Worker() {
        super();
    }

    public Worker(String id, String email, String name, String surname, String hashedPassword, boolean isActive) {
        super(id, email, name, surname, hashedPassword);
        this.isActive = isActive;
    }

    // Getter e setter qui...
}
