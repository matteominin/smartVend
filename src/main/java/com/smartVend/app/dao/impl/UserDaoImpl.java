package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.UserDao;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class UserDaoImpl implements UserDao {

    private final EntityManagerFactory emf;

    public UserDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public User getUserByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            // Removed the try-catch for NoResultException to allow it to propagate
            return em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public User createUser(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public User getUserById(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(User.class, userId);
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public User updateUser(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User merged = em.merge(user);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteUser(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                em.remove(user);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
