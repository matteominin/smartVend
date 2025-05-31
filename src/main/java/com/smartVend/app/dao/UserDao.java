package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.user.User;

public interface UserDao {
    User getUserByEmail(String email);

    User createUser(User user);

    User getUserById(Long userId);

    List<User> findAll();

    User updateUser(User user);

    void deleteUser(Long userId);
}