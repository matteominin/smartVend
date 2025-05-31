package com.smartvend.app.dao;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.AdminDaoImpl;
import com.smartvend.app.model.user.Admin;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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
        Admin admin = new Admin(new User(null, null, null, null));
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

    @Test
    void getAdminByEmail_returnsAdmin_whenFound() {
        TypedQuery<Admin> query = mock(TypedQuery.class);
        Admin admin = new Admin(new User(null, null, null, null));
        when(entityManager.createQuery(anyString(), eq(Admin.class))).thenReturn(query);
        when(query.setParameter(eq("email"), eq("test@example.com"))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(admin));

        Admin result = adminDao.getAdminByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(admin, result);
    }

    @Test
    void getAdminByEmail_returnsNull_whenNotFound() {
        TypedQuery<Admin> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Admin.class))).thenReturn(query);
        when(query.setParameter(eq("email"), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        Admin result = adminDao.getAdminByEmail("notfound@example.com");

        assertNull(result);
    }

    @Test
    void createAdmin_persistsAdmin() {
        Admin admin = new Admin(new User(null, null, null, null));

        Admin result = adminDao.createAdmin(admin);

        verify(entityManager).persist(admin);
        assertEquals(admin, result);
    }

    @Test
    void updateAdmin_mergesAdmin() {
        Admin admin = new Admin(new User(null, null, null, null));
        Admin mergedAdmin = new Admin(new User(null, null, null, null));
        when(entityManager.merge(admin)).thenReturn(mergedAdmin);

        Admin result = adminDao.updateAdmin(admin);

        verify(entityManager).merge(admin);
        assertEquals(mergedAdmin, result);
    }

    @Test
    void deleteAdmin_removesAdmin_whenExists() {
        Admin admin = new Admin(new User(null, null, null, null));
        when(entityManager.find(Admin.class, 1L)).thenReturn(admin);

        adminDao.deleteAdmin(1L);

        verify(entityManager).remove(admin);
    }

    @Test
    void deleteAdmin_doesNothing_whenAdminNotFound() {
        when(entityManager.find(Admin.class, 2L)).thenReturn(null);

        adminDao.deleteAdmin(2L);

        verify(entityManager, never()).remove(any());
    }

}
