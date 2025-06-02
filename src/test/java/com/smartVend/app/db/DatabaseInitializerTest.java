package com.smartvend.app.db;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@TestMethodOrder(OrderAnnotation.class)
public class DatabaseInitializerTest {

    private static final String TEST_UNIT = "test-pu";

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

        // Usa la nuova versione parametrica!
        DatabaseInitializer.initializeDatabase(TEST_UNIT);

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

        // Inizializza la persistence unit test, se non già fatto
        DatabaseInitializer.initializeDatabase(TEST_UNIT);

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
        // Assicura che l'unit test sia già inizializzata
        DatabaseInitializer.initializeDatabase(TEST_UNIT);
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
        DatabaseInitializer.initializeDatabase(TEST_UNIT);
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
