package com.smartvend.app.dao;

import com.smartvend.app.model.connection.Connection;

public interface ConnectionDao {
    Connection createConnection(String userId, String machineId);
    void deleteConnection(Long connectionId);
}
