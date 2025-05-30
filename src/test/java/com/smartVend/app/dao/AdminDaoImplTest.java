package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.AdminDaoImpl;
import com.smartvend.app.model.user.Admin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AdminDaoImplTest {

    private EntityManager entityManager;
    private AdminDaoImpl adminDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        adminDao = new AdminDaoImpl();
        // Iniettiamo l'EntityManager tramite reflection
        try {
            var field = AdminDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(adminDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAdminById_returnsAdmin() {
        Admin admin = new Admin();
        when(entityManager.find(Admin.class, "123")).thenReturn(admin);

        Admin result = adminDao.getAdminById("123");
        assertNotNull(result);
        assertEquals(admin, result);
    }

    @Test
    void findAll_returnsEmptyList() {
        @SuppressWarnings("unchecked")
        TypedQuery<Admin> query = (TypedQuery<Admin>) mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Admin.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Admin> result = adminDao.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
