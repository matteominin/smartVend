package com.smartvend.app.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;

public class DatabaseInitializer {

    private static EntityManagerFactory emf;

    public static void initializeDatabase() {
        try {
            // Modificato il nome della persistence unit da "smartvendPU" a "test-pu"
            emf = Persistence.createEntityManagerFactory("test-pu");

            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();

        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            // Rilancia l'eccezione per rendere più visibili gli errori nei test
            throw new RuntimeException("Failed to initialize database persistence unit.", e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            initializeDatabase();
        }
        return emf;
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
