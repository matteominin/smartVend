package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.user.Worker;

public interface WorkerDao {
    Worker getWorkerByUserId(Long userId);

    Worker getWorkerById(Long workerId);

    Worker getWorkerByEmail(String email);

    Worker createWorker(Worker worker);

    Worker updateWorker(Worker worker);

    void deleteWorker(Long id);

    List<Worker> findAllWorkers();
}
