package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.user.Admin;

public interface AdminDao {
    Admin getAdminById(String adminId);
    List<Admin> findAll();
}
