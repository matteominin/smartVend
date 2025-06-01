package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.user.Admin;

public interface AdminDao {
    Admin getAdminById(Long adminId);

    List<Admin> findAll();

    Admin getAdminByEmail(String email);

    Admin createAdmin(Admin admin);

    Admin updateAdmin(Admin admin);

    void deleteAdmin(Long id);
}
