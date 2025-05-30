package com.smartvend.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.model.user.User;

public class UserServiceTest {

    @Mock
    private UserDaoImpl userDao;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // LOGIN TESTS
    @Test
    @DisplayName("Test user login with valid credentials")
    public void loginWithValidCredentials() {
        String email = "test@example.com";
        String password = "password";
        User mockUser = new User(email, "John", "Doe", password);

        when(userDao.getUserByEmail(email)).thenReturn(mockUser);
        User loggedInUser = userService.logIn(email, password);
        assertNotNull(loggedInUser);
        assertEquals(email, loggedInUser.email);
        assertEquals("John", loggedInUser.name);
        assertEquals("Doe", loggedInUser.surname);
        assertEquals(password, loggedInUser.getPassword());

        verify(userDao, times(1)).getUserByEmail(email);
    }

    @Test
    @DisplayName("Test user login with invalid password")
    public void loginWithInvalidPassword() {
        String email = "email@example.com";
        String password = "wrongpassword";
        User mockUser = new User(email, "John", "Doe", "password123");

        when(userDao.getUserByEmail(email)).thenReturn(mockUser);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.logIn(email, password);
        });

        assertEquals("Invalid email or password", thrown.getMessage());
        verify(userDao, times(1)).getUserByEmail(email);
    }

    @Test
    @DisplayName("Test user login with non-existing email")
    public void loginWithNonExistingEmail() {
        String email = "wrong@example.com";
        String password = "password";

        when(userDao.getUserByEmail(email)).thenReturn(null);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.logIn(email, password);
        });
        assertEquals("Invalid email or password", thrown.getMessage());
        verify(userDao, times(1)).getUserByEmail(email);
    }

    // SIGNUP TESTS
    @Test
    @DisplayName("Test user signup with valid data")
    public void signUpWithValidData() {
        String email = "email@exaple.com";
        String name = "John";
        String surname = "Doe";
        String password = "password";

        User newUser = new User(email, name, surname, password);
        when(userDao.getUserByEmail(email)).thenReturn(null);
        when(userDao.createUser(any(User.class))).thenReturn(newUser);

        User signedUpUser = userService.signUp(email, name, surname, password);
        assertNotNull(signedUpUser);
        assertEquals(email, signedUpUser.email);
        assertEquals(name, signedUpUser.name);
        assertEquals(surname, signedUpUser.surname);
        assertEquals(password, signedUpUser.getPassword());
        verify(userDao, times(1)).getUserByEmail(email);
        verify(userDao, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Test user signup with existing email")
    public void signUpWithExistingEmail() {
        String email = "existing@email.com";
        String name = "Jhon";
        String surname = "Doe";
        String password = "password";

        User existringUser = new User(email, name, surname, password);
        when(userDao.getUserByEmail(email)).thenReturn(existringUser);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.signUp(email, name, surname, password);
        });
        assertEquals("Email already exists", thrown.getMessage());
        verify(userDao, times(1)).getUserByEmail(email);
    }

    @Test
    @DisplayName("Test user with missing email during signup")
    public void signUpWithMissingEmail() {
        String name = "John";
        String surname = "Doe";
        String password = "password";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.signUp(null, name, surname, password);
        });
        assertEquals("Missing required user fields!", thrown.getMessage());
        verifyNoInteractions(userDao);
    }

    // LOGOUT TESTS
    @Test
    @DisplayName("Test user logout with valid user ID")
    public void logOutWithValidUserId() {
        long userId = 1L;
        String email = "example@email.com";
        User mockUser = new User(userId, email, "John", "Doe", "password");

        when(userDao.getUserById(userId)).thenReturn(mockUser);
        userService.logOut(userId);
        verify(userDao, times(1)).getUserById(userId);
    }
}
