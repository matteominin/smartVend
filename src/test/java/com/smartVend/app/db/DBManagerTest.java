package com.smartvend.app.db;

import java.sql.Connection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("DBManager Tests")
public class DBManagerTest {
    private Connection mockConnection;
    private MockedStatic<DriverManager> mockedDriverManager;

    @BeforeEach
    void setUp() throws SQLException {
        resetSingleton();
        mockConnection = mock(Connection.class);

        mockedDriverManager = mockStatic(DriverManager.class);
        mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                .thenReturn(mockConnection);

        when(mockConnection.isClosed()).thenReturn(false);
    }

    private void resetSingleton() {
        try {
            // Usa la reflection per accedere e resettare il campo 'instance' privato
            java.lang.reflect.Field instanceField = DBManager.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null); // Imposta l'istanza a null
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Impossibile resettare l'istanza Singleton per il test.");
        }
    }

    @AfterEach
    void tearDown() {
        mockedDriverManager.close();
        resetSingleton();
    }

    @Test
    @DisplayName("should return the same instance (Singleton pattern)")
    void testSingletonInstance() {
        DBManager instance1 = DBManager.getInstance();
        DBManager instance2 = DBManager.getInstance();

        assertNotNull(instance1, "instance1 shouldn't be null.");
        assertSame(instance1, instance2, "Instances should be the same (singleton pattern).");
    }

    @Test
    @DisplayName("should establish connection on first getConnection call")
    void testGetConnectionOnFirstCall() throws SQLException {
        DBManager dbManager = DBManager.getInstance();
        Connection connection = dbManager.getConnection();

        assertNotNull(connection, "Connection shouldn't be null.");
        assertSame(mockConnection, connection, "Should return mocked connection.");

        mockedDriverManager.verify(() -> DriverManager.getConnection(anyString(),
                anyString(), anyString()), times(1));
    }

    @Test
    @DisplayName("should return existing connection if not closed")
    void testGetConnectionReturnsExisting() throws SQLException {
        DBManager dbManager = DBManager.getInstance();
        dbManager.getConnection();

        Connection connection = dbManager.getConnection();

        assertNotNull(connection, "Connection shouldn't be null.");
        assertSame(mockConnection, connection,
                "Should return the existing connection.");

        // DriverManager should be called only once
        mockedDriverManager.verify(() -> DriverManager.getConnection(anyString(),
                anyString(), anyString()), times(1));
    }

    @Test
    @DisplayName("should re-establish connection if it was closed")
    void testGetConnectionReestablishesIfClosed() throws SQLException {
        DBManager dbManager = DBManager.getInstance();
        dbManager.getConnection();

        // mock the connection as closed
        when(mockConnection.isClosed()).thenReturn(true);

        // Disconnect the DBManager to simulate a closed connection
        dbManager.disconnect();
        Connection reestablishedConnection = dbManager.getConnection();

        assertNotNull(reestablishedConnection,
                "Restablished connection shoudln't be null.");
        assertSame(mockConnection, reestablishedConnection,
                "Should return a valid connection.");

        mockedDriverManager.verify(() -> DriverManager.getConnection(anyString(),
                anyString(), anyString()), times(2));
    }

    @Test
    @DisplayName("should close the connection when disconnect is called")
    void testDisconnectClosesConnection() throws SQLException {
        DBManager dbManager = DBManager.getInstance();

        dbManager.getConnection();
        dbManager.disconnect();

        verify(mockConnection, times(1)).close();

        when(mockConnection.isClosed()).thenReturn(true);
        assertTrue(dbManager.getConnection().isClosed(),
                "Connection should be closed after disconnect().");
    }
}
