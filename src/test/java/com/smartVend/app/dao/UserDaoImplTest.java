package com.smartvend.app.dao;

import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * • Unit tests → Mockito, nessun DB<br>
 * • Integration tests → H2 in-memory, persistence-unit «test-pu»
 */
class UserDaoImplTest {

    /*
     * ────────────────────────────── UNIT TESTS (Mockito)
     * ──────────────────────────────
     */
    @Nested
    class Unit {

        @Mock
        EntityManagerFactory emf;
        @Mock
        EntityManager em;
        @Mock
        EntityTransaction tx;

        @InjectMocks
        UserDaoImpl dao;

        @BeforeEach
        void init() {
            MockitoAnnotations.openMocks(this);
            when(emf.createEntityManager()).thenReturn(em);
            when(em.getTransaction()).thenReturn(tx);
        }

        @Test
        void getUserByEmail_returnsUser() {
            User expected = new User("a@b.com", "Mario", "Rossi", "pwd");

            @SuppressWarnings("unchecked")
            TypedQuery<User> q = mock(TypedQuery.class);
            when(em.createQuery(anyString(), eq(User.class))).thenReturn(q);
            when(q.setParameter("email", "a@b.com")).thenReturn(q);
            when(q.getSingleResult()).thenReturn(expected);

            User result = dao.getUserByEmail("a@b.com");
            assertSame(expected, result);
            verify(q).getSingleResult();
        }

        @Test
        void getUserByEmail_throwsWhenNotFound() {
            @SuppressWarnings("unchecked")
            TypedQuery<User> q = mock(TypedQuery.class);
            when(em.createQuery(anyString(), eq(User.class))).thenReturn(q);
            when(q.setParameter(anyString(), anyString())).thenReturn(q);
            when(q.getSingleResult()).thenThrow(NoResultException.class);

            assertNull(dao.getUserByEmail("missing@mail"));
        }

        @Test
        void createUser_persistsAndReturns() {
            User u = new User("new@mail", "Anna", "Bianchi", "pwd");
            doNothing().when(em).persist(u);

            User res = dao.createUser(u);
            verify(em).persist(u);
            assertSame(u, res);
        }

        @Test
        void getUserById_found() {
            User u = new User("x@y", "X", "Y", "pwd");
            when(em.find(User.class, 3L)).thenReturn(u);

            assertSame(u, dao.getUserById(3L));
        }

        @Test
        void getUserById_nullWhenMissing() {
            when(em.find(User.class, 9L)).thenReturn(null);
            assertNull(dao.getUserById(9L));
        }

        @Test
        void findAll_empty() {
            @SuppressWarnings("unchecked")
            TypedQuery<User> q = mock(TypedQuery.class);
            when(em.createQuery(anyString(), eq(User.class))).thenReturn(q);
            when(q.getResultList()).thenReturn(Collections.emptyList());

            assertTrue(dao.findAll().isEmpty());
        }

        @Test
        void updateUser_merges() {
            User u = new User("up@mail", "Up", "User", "pwd");
            User merged = new User("up@mail", "Up", "User", "pwd");
            when(em.merge(u)).thenReturn(merged);

            User res = dao.updateUser(u);
            verify(em).merge(u);
            assertSame(merged, res);
        }

        @Test
        void deleteUser_removesWhenExists() {
            User u = new User("del@mail", "Del", "User", "pwd");
            when(em.find(User.class, 7L)).thenReturn(u);

            dao.deleteUser(7L);
            verify(em).remove(u);
        }

        @Test
        void deleteUser_noRemoveWhenMissing() {
            when(em.find(User.class, 8L)).thenReturn(null);

            dao.deleteUser(8L);
            verify(em, never()).remove(any());
        }
    }

    /*
     * ─────────────────────────── INTEGRATION TESTS (H2)
     * ───────────────────────────────
     */
    @Nested
    class Integration {

        private static EntityManagerFactory emf;
        private UserDaoImpl intDao;

        @BeforeAll
        static void startPU() {
            emf = Persistence.createEntityManagerFactory("test-pu");
        }

        @AfterAll
        static void stopPU() {
            if (emf != null)
                emf.close();
        }

        @BeforeEach
        void initDao() {
            intDao = new UserDaoImpl(emf);
        }

        @Test
        void integration_CRUD_flow() {
            /* CREATE */
            User user = new User("admin@email.com", "Mario", "Rossi", "pwd");
            intDao.createUser(user);
            assertNotNull(user.getId());

            /* READ by id & email */
            User byId = intDao.getUserById(user.getId());
            User byEmail = intDao.getUserByEmail("admin@email.com");
            assertEquals("Mario", byId.getName());
            assertEquals(byId.getId(), byEmail.getId());

            /* UPDATE */
            byId.setEmail("new@email.com");
            intDao.updateUser(byId);

            User afterUpd = intDao.getUserByEmail("new@email.com");
            assertEquals("new@email.com", afterUpd.getEmail());

            /* FIND ALL (≥1) */
            assertFalse(intDao.findAll().isEmpty());

            /* DELETE */
            intDao.deleteUser(afterUpd.getId());
            assertNull(intDao.getUserById(afterUpd.getId()));
            assertNull(intDao.getUserByEmail("new@email.com"));
        }
    }
}
