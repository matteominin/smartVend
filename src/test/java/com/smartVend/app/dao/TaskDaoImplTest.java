package com.smartvend.app.dao;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Admin;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

class TaskDaoImplTest {

    private TaskDaoImpl taskDao;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws Exception {
        entityManager = mock(EntityManager.class);
        taskDao = new TaskDaoImpl();

        // Reflection per settare entityManager privato
        var field = TaskDaoImpl.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(taskDao, entityManager);
    }

    @Test
    void getTaskById_returnsTask() {
        Task task = new Task(
                0L,
                new Worker(new User(null, null, null, null)),
                new Admin(new User(null, null, null, null)),
                MaintenanceStatus.Assigned,
                Instant.now(),
                new MaintenanceReport("description", Instant.now(), null));
        when(entityManager.find(Task.class, 1L)).thenReturn(task);
        Task result = taskDao.getTaskById(1);
        assertEquals(task, result);
    }

    @Test
    void updateTask_mergesTask() {
        Task task = new Task(
                0L,
                new Worker(new User(null, null, null, null)),
                new Admin(new User(null, null, null, null)),
                MaintenanceStatus.Assigned,
                Instant.now(),
                new MaintenanceReport("description", Instant.now(), null));
        taskDao.updateTask(task);
        verify(entityManager).merge(task);
    }

    @Test
    void createTask_persistsTask() {
        Task task = new Task(
            0L,
            new Worker(new User(null, null, null, null)),
            new Admin(new User(null, null, null, null)),
            MaintenanceStatus.Assigned,
            Instant.now(),
            new MaintenanceReport("description", Instant.now(), null)
        );
        Task result = taskDao.createTask(task);
        verify(entityManager).persist(task);
        assertEquals(task, result);
    }

    @Test
    void getTasksForWorker_returnsList() {
        long workerId = 7L;
        @SuppressWarnings("unchecked")
        TypedQuery<Task> query = mock(TypedQuery.class);
        Task task = mock(Task.class);
        List<Task> expected = List.of(task);

        when(entityManager.createQuery(anyString(), eq(Task.class))).thenReturn(query);
        when(query.setParameter(eq("workerId"), eq(workerId))).thenReturn(query);
        when(query.getResultList()).thenReturn(expected);

        List<Task> actual = taskDao.getTasksForWorker(workerId);
        assertEquals(expected, actual);
        verify(entityManager).createQuery(contains("workerId"), eq(Task.class));
        verify(query).setParameter("workerId", workerId);
        verify(query).getResultList();
    }

    @Test
    void getTasksForWorker_returnsEmptyList() {
        long workerId = 8L;
        @SuppressWarnings("unchecked")
        TypedQuery<Task> query = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Task.class))).thenReturn(query);
        when(query.setParameter(eq("workerId"), eq(workerId))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Task> actual = taskDao.getTasksForWorker(workerId);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }
}
