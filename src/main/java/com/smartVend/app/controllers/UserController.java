package com.smartvend.app.controllers;

import com.smartvend.app.model.user.User;
import com.smartvend.app.services.UserService;

public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        if (userService == null) {
            throw new IllegalArgumentException("UserService cannot be null");
        }
        this.userService = userService;
    }

    public User login(String email, String password) throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return userService.logIn(email, password);
    }

    public User signUp(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userService.signUp(user.getEmail(), user.getName(), user.getSurname(), user.getPassword());
    }
}