package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.AdminDao;
import com.smartvend.app.model.user.Admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class AdminDaoImpl implements AdminDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Admin getAdminById(String adminId) {
        return entityManager.find(Admin.class, adminId);
    }

    @Override
    public List<Admin> findAll() {
        return entityManager.createQuery(
                "SELECT a FROM Admin a", Admin.class
        ).getResultList();
    }
}
