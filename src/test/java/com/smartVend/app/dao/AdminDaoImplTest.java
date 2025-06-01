package com.smartvend.app.dao;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

class AdminDaoImplTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private AdminDaoImpl adminDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
    }

    @Test
    void getAdminById_returnsAdmin() {
        Admin admin = new Admin(new User(null, null, null, null));
        when(entityManager.find(Admin.class, 123L)).thenReturn(admin);

        Admin result = adminDao.getAdminById(123L);
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
        @SuppressWarnings("unchecked")
        TypedQuery<Admin> query = (TypedQuery<Admin>) mock(TypedQuery.class);
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
        @SuppressWarnings("unchecked")
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
