package com.smartvend.app.dao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * Unit tests (Mockito) + Integration tests (H2).
 */
class WorkerDaoImplTest {

    // ────────────────────────── UNIT TESTS (mock) ──────────────────────────────
    @Nested
    class Unit {

        @Mock
        EntityManagerFactory entityManagerFactory;
        @Mock
        EntityManager entityManager;
        @Mock
        EntityTransaction transaction;

        @InjectMocks
        WorkerDaoImpl workerDao;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
            when(entityManager.getTransaction()).thenReturn(transaction);
        }

        @Test
        void getWorkerByUserId_returnsWorker() {
            // Create test data
            User user = new User(123L, "test@example.com", "Test", "User", "password");
            Worker worker = new Worker(user);

            @SuppressWarnings("unchecked")
            TypedQuery<Worker> query = mock(TypedQuery.class);
            Stream<Worker> stream = Stream.of(worker);

            when(entityManager.createQuery("SELECT w FROM Worker w WHERE w.user.id = :userId", Worker.class))
                    .thenReturn(query);
            when(query.setParameter("userId", 123L)).thenReturn(query);
            when(query.setMaxResults(1)).thenReturn(query);
            when(query.getResultStream()).thenReturn(stream);

            // Execute the method
            Worker result = workerDao.getWorkerByUserId(123L);

            // Verify results
            assertNotNull(result);
            assertEquals(worker, result);

            // Verify the query was called correctly
            verify(entityManager).createQuery("SELECT w FROM Worker w WHERE w.user.id = :userId", Worker.class);
            verify(query).setParameter("userId", 123L);
            verify(query).setMaxResults(1);
            verify(query).getResultStream();
        }

        @Test
        void getWorkerByUserId_returnsNullIfNotFound() {
            when(entityManager.find(Worker.class, 999L)).thenReturn(null);

            Worker result = workerDao.getWorkerByUserId(999L);
            assertNull(result);
        }

        @Test
        void findAllWorkers_returnsEmptyList() {
            @SuppressWarnings("unchecked")
            TypedQuery<Worker> query = (TypedQuery<Worker>) mock(TypedQuery.class);
            when(entityManager.createQuery(anyString(), eq(Worker.class))).thenReturn(query);
            when(query.getResultList()).thenReturn(Collections.emptyList());

            List<Worker> result = workerDao.findAllWorkers();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void findAllWorkers_returnsListWithWorkers() {
            @SuppressWarnings("unchecked")
            TypedQuery<Worker> query = (TypedQuery<Worker>) mock(TypedQuery.class);
            Worker worker = new Worker(new User(null, null, null, null));
            when(entityManager.createQuery(anyString(), eq(Worker.class))).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(worker));

            List<Worker> result = workerDao.findAllWorkers();
            assertEquals(1, result.size());
            assertEquals(worker, result.get(0));
        }

        @Test
        void getWorkerByEmail_returnsWorker() {
            @SuppressWarnings("unchecked")
            TypedQuery<Worker> query = (TypedQuery<Worker>) mock(TypedQuery.class);
            Worker worker = new Worker(new User(null, null, null, null));
            when(entityManager.createQuery(anyString(), eq(Worker.class))).thenReturn(query);
            when(query.setParameter(eq("email"), eq("test@email.com"))).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(worker));

            Worker result = workerDao.getWorkerByEmail("test@email.com");
            assertNotNull(result);
            assertEquals(worker, result);
        }

        @Test
        void getWorkerByEmail_returnsNullIfNotFound() {
            @SuppressWarnings("unchecked")
            TypedQuery<Worker> query = (TypedQuery<Worker>) mock(TypedQuery.class);
            when(entityManager.createQuery(anyString(), eq(Worker.class))).thenReturn(query);
            when(query.setParameter(eq("email"), eq("notfound@email.com"))).thenReturn(query);
            when(query.getResultList()).thenReturn(Collections.emptyList());

            Worker result = workerDao.getWorkerByEmail("notfound@email.com");
            assertNull(result);
        }

        @Test
        void createWorker_persistsAndReturnsWorker() {
            Worker worker = new Worker(new User(null, null, null, null));
            doNothing().when(entityManager).persist(worker);

            Worker result = workerDao.createWorker(worker);
            verify(entityManager).persist(worker);
            assertEquals(worker, result);
        }

        @Test
        void updateWorker_mergesAndReturnsWorker() {
            Worker worker = new Worker(new User(null, null, null, null));
            Worker merged = new Worker(new User(null, null, null, null));
            when(entityManager.merge(worker)).thenReturn(merged);

            Worker result = workerDao.updateWorker(worker);
            verify(entityManager).merge(worker);
            assertEquals(merged, result);
        }

        @Test
        void deleteWorker_removesIfExists() {
            Worker worker = new Worker(new User(null, null, null, null));
            when(entityManager.find(Worker.class, 42L)).thenReturn(worker);

            workerDao.deleteWorker(42L);
            verify(entityManager).remove(worker);
        }

        @Test
        void deleteWorker_doesNothingIfWorkerNotFound() {
            when(entityManager.find(Worker.class, 99L)).thenReturn(null);

            workerDao.deleteWorker(99L);
            verify(entityManager, never()).remove(any());
        }
    }

    // ────────────────────────── INTEGRATION TESTS (H2)
    // ──────────────────────────────
    @Nested
    class Integration {

        private static EntityManagerFactory emf;
        private WorkerDaoImpl intDao;

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
        void setupDao() {
            intDao = new WorkerDaoImpl(emf);
        }

        @Test
        void integration_CRUD_flow() {
            // PRIMA crea e salva User (relazione not-null obbligatoria)
            User user = new User("worker@email.com", "Luigi", "Verdi", "pw");
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            em.close();

            // CREATE Worker
            Worker worker = new Worker(user);
            intDao.createWorker(worker);
            assertNotNull(worker.getId());

            // READ by id & email
            Worker byId = intDao.getWorkerByUserId(worker.getId());
            assertNotNull(byId);
            assertEquals("worker@email.com", byId.getUser().getEmail());

            Worker byEmail = intDao.getWorkerByEmail("worker@email.com");
            assertNotNull(byEmail);
            assertEquals(worker.getId(), byEmail.getId());

            // UPDATE (modifica mail user)
            byId.getUser().setEmail("newworker@email.com");
            intDao.updateWorker(byId);

            // Aggiorna anche l'entity User (se necessario, vedi cascade)
            EntityManager em2 = emf.createEntityManager();
            em2.getTransaction().begin();
            em2.merge(byId.getUser());
            em2.getTransaction().commit();
            em2.close();

            Worker afterUpdate = intDao.getWorkerByEmail("newworker@email.com");
            assertNotNull(afterUpdate);
            assertEquals("newworker@email.com", afterUpdate.getUser().getEmail());

            // FIND ALL
            List<Worker> all = intDao.findAllWorkers();
            assertTrue(all.size() >= 1);

            // DELETE
            intDao.deleteWorker(worker.getId());
            Worker afterDelete = intDao.getWorkerByUserId(worker.getId());
            assertNull(afterDelete);
        }
    }
}
