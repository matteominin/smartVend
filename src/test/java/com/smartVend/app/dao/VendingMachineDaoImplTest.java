package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VendingMachineDaoImplTest {

    private EntityManager entityManager;
    private ConcreteVendingMachineDaoImpl vendingMachineDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        vendingMachineDao = new ConcreteVendingMachineDaoImpl();
        // Reflection to inject EntityManager
        try {
            var field = ConcreteVendingMachineDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(vendingMachineDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAll_returnsList() {
        @SuppressWarnings("unchecked")
        TypedQuery<ConcreteVendingMachine> query = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(ConcreteVendingMachine.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<ConcreteVendingMachine> result = vendingMachineDao.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
