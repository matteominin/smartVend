package com.smartvend.app.dao;

import com.smartvend.app.model.user.User;

public interface UserDao {
    User getUserByEmail(String email);
    User createUser(User user);
    User getUserById(Long userId); // Aggiunto
}
