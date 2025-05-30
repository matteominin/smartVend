package com.smartvend.app.dao;

import com.smartvend.app.model.connection.Connection;

public interface ConnectionDao {
    Connection createConnection(Long userId, String machineId);

    void deleteConnection(Long connectionId);

    Connection getConnectionById(Long connectionId);
}
