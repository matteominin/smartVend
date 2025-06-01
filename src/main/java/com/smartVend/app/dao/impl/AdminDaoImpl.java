package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.AdminDao;
import com.smartvend.app.model.user.Admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class AdminDaoImpl implements AdminDao {
    private final EntityManagerFactory emf;

    public AdminDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public Admin getAdminById(Long adminId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Admin.class, adminId);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Admin> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Admin a", Admin.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Admin getAdminByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Admin> admins = em.createQuery(
                    "SELECT a FROM Admin a WHERE a.user.email = :email", Admin.class)
                    .setParameter("email", email)
                    .getResultList();
            return admins.isEmpty() ? null : admins.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public Admin createAdmin(Admin admin) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(admin);
            em.getTransaction().commit();
            return admin;
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
    public Admin updateAdmin(Admin admin) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Admin merged = em.merge(admin);
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
    public void deleteAdmin(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Admin admin = em.find(Admin.class, id);
            if (admin != null) {
                em.remove(admin);
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