package com.smartvend.app.dao;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

class UserDaoImplTest {

    private EntityManager entityManager;
    private UserDaoImpl userDao;

    @BeforeEach
    void setUp() throws Exception {
        entityManager = mock(EntityManager.class);
        userDao = new UserDaoImpl();

        // Inject entityManager in the userDao (since field is private)
        var field = UserDaoImpl.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(userDao, entityManager);
    }

    @Test
    void getUserByEmail_returnsUser() {
        User fakeUser = new User(null, null, null, null);

        @SuppressWarnings("unchecked")
        TypedQuery<User> queryMock = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("email"), anyString())).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(fakeUser);

        User result = userDao.getUserByEmail("email@example.com");
        assertSame(fakeUser, result);
        verify(entityManager).createQuery(contains("email"), eq(User.class));
        verify(queryMock).setParameter(eq("email"), eq("email@example.com"));
        verify(queryMock).getSingleResult();
    }

    @Test
    void getUserByEmail_throwsIfNotFound() {
        @SuppressWarnings("unchecked")
        TypedQuery<User> queryMock = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("email"), anyString())).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenThrow(new NoResultException());

        assertThrows(NoResultException.class, () -> userDao.getUserByEmail("notfound@example.com"));
    }

    @Test
    void createUser_persistsAndReturnsUser() {
        User user = new User(null, null, null, null);
        doNothing().when(entityManager).persist(user);
        User result = userDao.createUser(user);
        verify(entityManager).persist(user);
        assertSame(user, result);
    }

    @Test
    void getUserById_returnsUser() {
        User fakeUser = new User(null, null, null, null);
        when(entityManager.find(User.class, 5L)).thenReturn(fakeUser);

        User result = userDao.getUserById(5L);
        verify(entityManager).find(User.class, 5L);
        assertSame(fakeUser, result);
    }

    @Test
    void getUserById_returnsNullIfNotFound() {
        when(entityManager.find(User.class, 10L)).thenReturn(null);

        User result = userDao.getUserById(10L);
        verify(entityManager).find(User.class, 10L);
        assertNull(result);
    }

    @Test
    void findAll_returnsEmptyList() {
        @SuppressWarnings("unchecked")
        TypedQuery<User> queryMock = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(Collections.emptyList());

        List<User> result = userDao.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsListWithUsers() {
        @SuppressWarnings("unchecked")
        TypedQuery<User> queryMock = mock(TypedQuery.class);
        User user = new User(null, null, null, null);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(List.of(user));

        List<User> result = userDao.findAll();
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
    }

    @Test
    void updateUser_mergesAndReturnsUser() {
        User user = new User(null, null, null, null);
        User merged = new User(null, null, null, null);
        when(entityManager.merge(user)).thenReturn(merged);

        User result = userDao.updateUser(user);
        verify(entityManager).merge(user);
        assertSame(merged, result);
    }

    @Test
    void deleteUser_removesIfExists() {
        User user = new User(null, null, null, null);
        when(entityManager.find(User.class, 123L)).thenReturn(user);

        userDao.deleteUser(123L);
        verify(entityManager).remove(user);
    }

    @Test
    void deleteUser_doesNothingIfUserNotFound() {
        when(entityManager.find(User.class, 456L)).thenReturn(null);

        userDao.deleteUser(456L);
        verify(entityManager, never()).remove(any());
    }
}
