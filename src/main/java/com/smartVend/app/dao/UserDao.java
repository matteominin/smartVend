package com.smartvend.app.dao;

import com.smartvend.app.model.user.User;

public class UserDao {
    public User getUserByEmail(String email) {
        return new User(1, "", "", "", "");
    }

    public User createUser(User user) {
        return new User(1, "", "", "", "");
    }

    public User getUserById(long userId) {
        return new User(1, "", "", "", "");
    }
}
