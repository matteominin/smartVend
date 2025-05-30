package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.UserDao;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User getUserByEmail(String email) {
        return entityManager.createQuery(
            "SELECT u FROM User u WHERE u.email = :email", User.class)
            .setParameter("email", email)
            .getSingleResult();
    }

    @Override
    @Transactional
    public User createUser(User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return entityManager.find(User.class, userId);
    }
}
