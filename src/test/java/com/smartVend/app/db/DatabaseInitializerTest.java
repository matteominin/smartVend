package com.smartvend.app.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class DatabaseInitializerTest {

    private static void resetEmf() {
        try {
            Field emfField = DatabaseInitializer.class.getDeclaredField("emf");
            emfField.setAccessible(true);
            emfField.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            fail("Unable to reset 'emf' field via reflection.");
        }
    }

    @BeforeEach
    void setUp() {
        DatabaseInitializer.shutdown();
        resetEmf();
    }

    @AfterAll
    static void tearDownAll() {
        DatabaseInitializer.shutdown();
        resetEmf();
    }

    @Test
    @Order(1)
    void testInitializeDatabase() {
        assertNull(getStaticEmf(), "EMF should be null before initialization.");

        DatabaseInitializer.initializeDatabase();

        EntityManagerFactory emf = getStaticEmf();
        assertNotNull(emf, "EntityManagerFactory should not be null after initialization.");
        assertTrue(emf.isOpen(), "EntityManagerFactory should be open after initialization.");

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            assertNotNull(em, "EntityManager should be creatable.");
            assertTrue(em.isOpen(), "EntityManager should be open.");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    @Order(2)
    void testGetEntityManagerFactoryInitializesIfNull() {
        assertNull(getStaticEmf(), "EMF should be null before calling getEntityManagerFactory.");

        EntityManagerFactory emf = DatabaseInitializer.getEntityManagerFactory();

        assertNotNull(emf, "EntityManagerFactory should not be null.");
        assertTrue(emf.isOpen(), "EntityManagerFactory should be open.");

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            assertNotNull(em, "EntityManager should be creatable.");
            assertTrue(em.isOpen(), "EntityManager should be open.");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    @Order(3)
    void testGetEntityManagerFactoryReturnsExistingInstance() {
        DatabaseInitializer.initializeDatabase();
        EntityManagerFactory initialEmf = DatabaseInitializer.getEntityManagerFactory();
        assertNotNull(initialEmf, "Initial EMF should not be null.");
        assertTrue(initialEmf.isOpen(), "Initial EMF should be open.");

        EntityManagerFactory secondEmf = DatabaseInitializer.getEntityManagerFactory();

        assertSame(initialEmf, secondEmf,
                "Subsequent calls should return the same EntityManagerFactory instance.");
        assertTrue(secondEmf.isOpen(), "The second EMF should be open.");
    }

    @Test
    @Order(4)
    void testShutdown() {
        DatabaseInitializer.initializeDatabase();
        EntityManagerFactory emfBeforeShutdown = getStaticEmf();
        assertNotNull(emfBeforeShutdown, "EMF should be initialized before shutdown.");
        assertTrue(emfBeforeShutdown.isOpen(), "EMF should be open before shutdown.");

        DatabaseInitializer.shutdown();

        assertFalse(emfBeforeShutdown.isOpen(), "EntityManagerFactory should be closed after shutdown.");
        assertNotNull(getStaticEmf(),
                "The static EMF reference should not be null after shutdown (only closed).");
    }

    private static EntityManagerFactory getStaticEmf() {
        try {
            Field emfField = DatabaseInitializer.class.getDeclaredField("emf");
            emfField.setAccessible(true);
            return (EntityManagerFactory) emfField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            fail("Unable to get 'emf' field via reflection.");
            return null;
        }
    }
}
