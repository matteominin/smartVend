package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.user.Worker;

public interface WorkerDao {
    Worker getWorkerById(String workerId);
    List<Worker> findAllActive();
}
