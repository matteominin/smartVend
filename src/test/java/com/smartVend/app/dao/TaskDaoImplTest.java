package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Admin;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TaskDaoImplTest {

    /*──────────────────────────── UNIT TESTS (Mockito) ───────────────────────────*/
    @Nested
    class Unit {

        @Mock EntityManagerFactory emf;
        @Mock EntityManager em;
        @Mock EntityTransaction tx;
        @InjectMocks TaskDaoImpl dao;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(emf.createEntityManager()).thenReturn(em);
            when(em.getTransaction()).thenReturn(tx);
        }

        @Test
        void getTaskById_returnsTask() {
            Task task = buildTask();
            when(em.find(Task.class, 1L)).thenReturn(task);
            Task result = dao.getTaskById(1L);
            assertEquals(task, result);
        }

        @Test
        void createTask_persistsTask() {
            Task task = buildTask();
            doNothing().when(em).persist(task);
            when(tx.isActive()).thenReturn(false);

            Task result = dao.createTask(task);

            verify(em).persist(task);
            verify(tx).begin();
            verify(tx).commit();
            assertEquals(task, result);
        }

        @Test
        void updateTask_mergesTask() {
            Task task = buildTask();
            Task merged = buildTask();
            when(em.merge(task)).thenReturn(merged);
            when(tx.isActive()).thenReturn(false);

            Task result = dao.updateTask(task);

            verify(em).merge(task);
            verify(tx).begin();
            verify(tx).commit();
            assertEquals(merged, result);
        }

        @Test
        void getTasksForWorker_returnsList() {
            long workerId = 7L;
            @SuppressWarnings("unchecked")
            TypedQuery<Task> query = (TypedQuery<Task>) mock(TypedQuery.class);
            Task task = buildTask();
            List<Task> expected = List.of(task);

            when(em.createQuery(anyString(), eq(Task.class))).thenReturn(query);
            when(query.setParameter(eq("workerId"), eq(workerId))).thenReturn(query);
            when(query.getResultList()).thenReturn(expected);

            List<Task> actual = dao.getTasksForWorker(workerId);
            assertEquals(expected, actual);
            verify(em).createQuery(contains("workerId"), eq(Task.class));
            verify(query).setParameter("workerId", workerId);
            verify(query).getResultList();
        }

        @Test
        void getTasksForWorker_returnsEmptyList() {
            long workerId = 8L;
            @SuppressWarnings("unchecked")
            TypedQuery<Task> query = (TypedQuery<Task>) mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Task.class))).thenReturn(query);
            when(query.setParameter(eq("workerId"), eq(workerId))).thenReturn(query);
            when(query.getResultList()).thenReturn(Collections.emptyList());

            List<Task> actual = dao.getTasksForWorker(workerId);
            assertNotNull(actual);
            assertTrue(actual.isEmpty());
        }

        // Utility per costruire Task fittizio
        private Task buildTask() {
            return new Task(
                0L, // id (primitivo, non può essere null!)
                new Worker(new User("worker@email.com", "John", "Doe", "pwd")),
                new Admin(new User("admin@email.com", "Jane", "Smith", "pwd")),
                MaintenanceStatus.Assigned,
                Instant.now(),
                new MaintenanceReport("desc", Instant.now(), null)
            );
        }
    }

    /*───────────────────────── INTEGRATION TESTS (H2 DB) ─────────────────────────*/
    @Nested
    class Integration {

        private static EntityManagerFactory emf;
        private TaskDaoImpl dao;

        @BeforeAll
        static void startPU() { emf = Persistence.createEntityManagerFactory("test-pu"); }

        @AfterAll
        static void stopPU() { if (emf != null) emf.close(); }

        @BeforeEach
        void setup() { dao = new TaskDaoImpl(emf); }

        @Test
        void integration_CRUD_flow() {
            // Crea worker e admin e li persiste
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            User workerUser = new User("worker@email.com", "John", "Doe", "pwd");
            em.persist(workerUser);
            em.flush();
            Worker worker = new Worker(workerUser);
            em.persist(worker);

            User adminUser = new User("admin@email.com", "Jane", "Smith", "pwd");
            em.persist(adminUser);
            em.flush();
            Admin admin = new Admin(adminUser);
            em.persist(admin);

            MaintenanceReport report = new MaintenanceReport("desc", Instant.now(), null);
            em.persist(report);

            em.getTransaction().commit();
            em.close();

            // CREATE
            Task task = new Task(
                admin,
                worker,
                report
            );
            task.setStatus(MaintenanceStatus.Assigned);
            dao.createTask(task);
            assertNotNull(task.getId());


            // READ
            Task loaded = dao.getTaskById(task.getId());
            assertNotNull(loaded);
            assertEquals(worker.getId(), loaded.getWorker().getId());
            assertEquals(admin.getId(), loaded.getSupervisor().getId()); // Corretto: getSupervisor()

            // UPDATE
            loaded.setStatus(MaintenanceStatus.Completed);
            Task updated = dao.updateTask(loaded);
            assertEquals(MaintenanceStatus.Completed, updated.getStatus());

            // getTasksForWorker
            List<Task> list = dao.getTasksForWorker(worker.getId());
            assertFalse(list.isEmpty());
            assertEquals(worker.getId(), list.get(0).getWorker().getId());
        }
    }
}
