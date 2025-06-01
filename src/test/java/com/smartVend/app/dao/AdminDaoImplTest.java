package com.smartvend.app.dao;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

class AdminDaoImplTest {

    // ========== UNIT TESTS (MOCK) ==========
    private EntityManagerFactory emfMock;
    private EntityManager emMock;
    private EntityTransaction txMock;
    private AdminDaoImpl adminDao;

    @Nested
    class Unit {

        @BeforeEach
        void init() {
            emfMock = mock(EntityManagerFactory.class);
            emMock  = mock(EntityManager.class);
            txMock  = mock(EntityTransaction.class);

            when(emfMock.createEntityManager()).thenReturn(emMock);
            when(emMock.getTransaction()).thenReturn(txMock);

            adminDao = new AdminDaoImpl(emfMock);
        }

        @Test
        void getAdminById_returnsAdmin() {
            Admin admin = new Admin();
            when(emMock.find(Admin.class, 1L)).thenReturn(admin);

            Admin result = adminDao.getAdminById(1L);
            assertNotNull(result);
            assertEquals(admin, result);
            verify(emMock).close();
        }

        @Test
        void findAll_returnsEmptyList() {
            @SuppressWarnings("unchecked")
            TypedQuery<Admin> query = (TypedQuery<Admin>) mock(TypedQuery.class);
            when(emMock.createQuery(anyString(), eq(Admin.class))).thenReturn(query);
            when(query.getResultList()).thenReturn(Collections.emptyList());

            List<Admin> result = adminDao.findAll();
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(emMock).close();
        }

        @Test
        void getAdminByEmail_returnsAdmin_whenFound() {
            @SuppressWarnings("unchecked")
            TypedQuery<Admin> query = (TypedQuery<Admin>) mock(TypedQuery.class);
            Admin admin = new Admin();
            when(emMock.createQuery(anyString(), eq(Admin.class))).thenReturn(query);
            when(query.setParameter(eq("email"), eq("test@example.com"))).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(admin));

            Admin result = adminDao.getAdminByEmail("test@example.com");
            assertNotNull(result);
            assertEquals(admin, result);
            verify(emMock).close();
        }

        @Test
        void getAdminByEmail_returnsNull_whenNotFound() {
            @SuppressWarnings("unchecked")
            TypedQuery<Admin> query = mock(TypedQuery.class);
            when(emMock.createQuery(anyString(), eq(Admin.class))).thenReturn(query);
            when(query.setParameter(eq("email"), any())).thenReturn(query);
            when(query.getResultList()).thenReturn(Collections.emptyList());

            Admin result = adminDao.getAdminByEmail("notfound@example.com");
            assertNull(result);
            verify(emMock).close();
        }

        @Test
        void createAdmin_persistsAdmin() {
            Admin admin = new Admin();

            Admin result = adminDao.createAdmin(admin);

            verify(txMock).begin();
            verify(emMock).persist(admin);
            verify(txMock).commit();
            verify(emMock).close();
            assertEquals(admin, result);
        }

        @Test
        void updateAdmin_mergesAdmin() {
            Admin admin = new Admin();
            Admin mergedAdmin = new Admin();
            when(emMock.merge(admin)).thenReturn(mergedAdmin);

            Admin result = adminDao.updateAdmin(admin);

            verify(txMock).begin();
            verify(emMock).merge(admin);
            verify(txMock).commit();
            verify(emMock).close();
            assertEquals(mergedAdmin, result);
        }

        @Test
        void deleteAdmin_removesAdmin_whenExists() {
            Admin admin = new Admin();
            when(emMock.find(Admin.class, 1L)).thenReturn(admin);

            adminDao.deleteAdmin(1L);

            verify(txMock).begin();
            verify(emMock).remove(admin);
            verify(txMock).commit();
            verify(emMock).close();
        }

        @Test
        void deleteAdmin_doesNothing_whenAdminNotFound() {
            when(emMock.find(Admin.class, 2L)).thenReturn(null);

            adminDao.deleteAdmin(2L);

            verify(txMock).begin();
            verify(emMock, never()).remove(any());
            verify(txMock).commit();
            verify(emMock).close();
        }
    }

    // ========== INTEGRATION TESTS (H2) ==========
    private static EntityManagerFactory emf;
    private AdminDaoImpl intDao;

    @BeforeAll
    static void startPU() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @AfterAll
    static void closePU() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void initDao() {
        intDao = new AdminDaoImpl(emf);
    }
     
    @Test
    void integration_CRUD_flow() {
        // PRIMA: crea e salva l'utente!
        User user = new User("admin@email.com", "Mario", "Rossi", "securepwd");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.close();

        // Collega l'user ad un admin
        Admin admin = new Admin(user);

        // CREATE
        intDao.createAdmin(admin);
        assertNotNull(admin.getId());

        // READ
        Admin loaded = intDao.getAdminById(admin.getId());
        assertNotNull(loaded);
        assertEquals("admin@email.com", loaded.getUser().getEmail());

        // UPDATE (modifica email)
        loaded.getUser().setEmail("nuovo@email.com");
        intDao.updateAdmin(loaded);

        // PATCH: aggiorna anche User manualmente
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        em2.merge(loaded.getUser());
        em2.getTransaction().commit();
        em2.close();

        Admin afterUpdate = intDao.getAdminById(loaded.getId());
        assertEquals("nuovo@email.com", afterUpdate.getUser().getEmail());

        // FIND ALL
        List<Admin> all = intDao.findAll();
        assertFalse(all.isEmpty());

        // FIND BY EMAIL
        Admin foundByEmail = intDao.getAdminByEmail("nuovo@email.com");
        assertNotNull(foundByEmail);
        assertEquals("nuovo@email.com", foundByEmail.getUser().getEmail());

        // DELETE
        intDao.deleteAdmin(admin.getId());
        Admin afterDelete = intDao.getAdminById(admin.getId());
        assertNull(afterDelete);
    }

}
