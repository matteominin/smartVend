package com.smartvend.app.dao.impl;

import com.smartvend.app.model.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
        User fakeUser = new User();

        // Mock TypedQuery and its method chain
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
    void createUser_persistsAndReturnsUser() {
        User user = new User();
        doNothing().when(entityManager).persist(user);
        User result = userDao.createUser(user);
        verify(entityManager).persist(user);
        assertSame(user, result);
    }

    @Test
    void getUserById_returnsUser() {
        User fakeUser = new User();
        when(entityManager.find(User.class, 5L)).thenReturn(fakeUser);

        User result = userDao.getUserById(5L);
        verify(entityManager).find(User.class, 5L);
        assertSame(fakeUser, result);
    }
}
