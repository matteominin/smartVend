package com.smartvend.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.model.maintenance.Task;

import jakarta.persistence.EntityManager;

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
        Task task = new Task();
        when(entityManager.find(Task.class, 1L)).thenReturn(task);
        Task result = taskDao.getTaskById("1");
        assertEquals(task, result);
    }

    @Test
    void updateTask_mergesTask() {
        Task task = new Task();
        taskDao.updateTask(task);
        verify(entityManager).merge(task);
    }

    // Per testare getTasksForWorker servirebbe mockare anche la TypedQuery.
}

