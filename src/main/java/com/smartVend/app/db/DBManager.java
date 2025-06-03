package com.smartvend.app.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DBManager {

    private static EntityManagerFactory emf;
    private static final String DEFAULT_UNIT = "smartvendPU";

    public static void init() {
        initializeDatabase(DEFAULT_UNIT);
    }

    public static void initializeDatabase(String persistenceUnitName) {
        try {
            emf = Persistence.createEntityManagerFactory(persistenceUnitName);

            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();

        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database persistence unit.", e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            init();
        }
        return emf;
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
