package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class WorkerDaoImplTest {

    private EntityManager entityManager;
    private WorkerDaoImpl workerDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        workerDao = new WorkerDaoImpl();
        // inject entityManager
        try {
            var field = WorkerDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(workerDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getWorkerById_returnsWorker() {
        Worker worker = new Worker(new User(null, null, null, null));
        when(entityManager.find(Worker.class, "123")).thenReturn(worker);

        Worker result = workerDao.getWorkerById("123");
        assertNotNull(result);
        assertEquals(worker, result);
    }
}
