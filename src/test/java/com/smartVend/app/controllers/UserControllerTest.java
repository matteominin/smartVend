package com.smartvend.app.controllers;

import com.smartvend.app.model.user.User;
import com.smartvend.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void constructor_NullUserService_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new UserController(null);
        });
        assertEquals("UserService cannot be null", exception.getMessage());
    }

    @Test
    void login_ValidCredentials_CallsUserServiceAndReturnsUser() {
        String email = "test@example.com";
        String password = "password";
        User user = mock(User.class);
        when(userService.logIn(email, password)).thenReturn(user);

        User result = userController.login(email, password);

        assertSame(user, result);
        verify(userService).logIn(email, password);
    }

    @Test
    void login_NullEmail_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.login(null, "password");
        });
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void login_EmptyEmail_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.login("   ", "password");
        });
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void login_NullPassword_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.login("test@example.com", null);
        });
        assertEquals("Password cannot be null or empty", exception.getMessage());
    }

    @Test
    void login_EmptyPassword_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.login("test@example.com", "   ");
        });
        assertEquals("Password cannot be null or empty", exception.getMessage());
    }

    @Test
    void signUp_ValidUser_CallsUserServiceAndReturnsUser() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getName()).thenReturn("John");
        when(user.getSurname()).thenReturn("Doe");
        when(user.getPassword()).thenReturn("password");

        User createdUser = mock(User.class);
        when(userService.signUp("test@example.com", "John", "Doe", "password")).thenReturn(createdUser);

        User result = userController.signUp(user);

        assertSame(createdUser, result);
        verify(userService).signUp("test@example.com", "John", "Doe", "password");
    }

    @Test
    void signUp_NullUser_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.signUp(null);
        });
        assertEquals("User cannot be null", exception.getMessage());
    }
}
