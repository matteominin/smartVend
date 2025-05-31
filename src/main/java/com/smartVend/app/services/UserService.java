package com.smartvend.app.services;

import com.smartvend.app.dao.UserDao;
import com.smartvend.app.model.user.User;

public class UserService {
    private UserDao userDao;

    UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User logIn(String email, String password) throws IllegalArgumentException {
        User user = userDao.getUserByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    public User signUp(String email, String name, String surname, String password) throws IllegalArgumentException {
        if (email == null || name == null || surname == null
                || password == null) {
            throw new IllegalArgumentException("Missing required user fields!");
        }

        if (userDao.getUserByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        User savedUser = userDao.createUser(new User(email, name, surname, password));
        return savedUser;
    }

    public void logOut(long userId) throws IllegalArgumentException {
        User user = userDao.getUserById(userId);
        if (user != null) {
            System.out.println("User " + user.email + " logged out successfully.");
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}
