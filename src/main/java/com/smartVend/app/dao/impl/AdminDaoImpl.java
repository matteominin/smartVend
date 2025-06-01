package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.AdminDao;
import com.smartvend.app.model.user.Admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class AdminDaoImpl implements AdminDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Admin getAdminById(Long adminId) {
        return entityManager.find(Admin.class, adminId);
    }

    @Override
    public List<Admin> findAll() {
        return entityManager.createQuery(
                "SELECT a FROM Admin a", Admin.class).getResultList();
    }

    @Override
    public Admin getAdminByEmail(String email) {
        List<Admin> admins = entityManager.createQuery(
                "SELECT a FROM Admin a WHERE a.email = :email", Admin.class)
                .setParameter("email", email)
                .getResultList();
        return admins.isEmpty() ? null : admins.get(0);
    }

    @Override
    @Transactional
    public Admin createAdmin(Admin admin) {
        entityManager.persist(admin);
        return admin;
    }

    @Override
    @Transactional
    public Admin updateAdmin(Admin admin) {
        return entityManager.merge(admin);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long id) {
        Admin admin = entityManager.find(Admin.class, id);
        if (admin != null) {
            entityManager.remove(admin);
        }
    }
}
