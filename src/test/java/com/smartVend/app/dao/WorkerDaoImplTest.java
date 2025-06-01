package com.smartvend.app.dao;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

class WorkerDaoImplTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private WorkerDaoImpl workerDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
    }

    @Test
    void getWorkerById_returnsWorker() {
        Worker worker = new Worker(new User(null, null, null, null));
        when(entityManager.find(Worker.class, 123L)).thenReturn(worker);

        Worker result = workerDao.getWorkerById(123L);
        assertNotNull(result);
        assertEquals(worker, result);
    }

    @Test
    void getWorkerById_returnsNullIfNotFound() {
        when(entityManager.find(Worker.class, 999L)).thenReturn(null);

        Worker result = workerDao.getWorkerById(999L);
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
