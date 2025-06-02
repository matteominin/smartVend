package com.smartvend.app.dao;

import java.time.Instant;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.ConnectionDaoImpl;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.MachineStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConnectionDaoImplTest {

    // ------- UNIT TESTS -------- //
    EntityManagerFactory emfMock;
    EntityManager emMock;
    EntityTransaction txMock;
    ConnectionDaoImpl daoMocked;

    @BeforeEach
    void setupMocks() {
        emfMock = mock(EntityManagerFactory.class);
        emMock = mock(EntityManager.class);
        txMock = mock(EntityTransaction.class);

        when(emfMock.createEntityManager()).thenReturn(emMock);
        when(emMock.getTransaction()).thenReturn(txMock);

        daoMocked = new ConnectionDaoImpl(emfMock);
    }

    @Test
    void unit_createConnection_persistsConnection() {
        // id allineati
        long id = 10L;

        User user = new User(id, "test@email.com", "Test", "User", "pwd");
        Customer customer = new Customer(id, user, 12.5);
        ConcreteVendingMachine machine = new ConcreteVendingMachine(
                "macchinaA", null, "loc", 1, MachineStatus.Operative);

        when(emMock.find(User.class, id)).thenReturn(user); // <-- AGGIUNTO
        when(emMock.find(Customer.class, id)).thenReturn(customer); // <-- NON cambia
        when(emMock.find(ConcreteVendingMachine.class, "macchinaA"))
                .thenReturn(machine);

        Connection conn = daoMocked.createConnection(id, "macchinaA");

        assertNotNull(conn);
        verify(emMock).persist(any(Connection.class));
        verify(txMock).begin();
        verify(txMock).commit();
    }

    @Test
    void unit_createConnection_throwsIfUserNotFound() {
        // Mock che restituisce null per Customer non trovato
        when(emMock.find(Customer.class, 123L)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            daoMocked.createConnection(123L, "macchinaX");
        });

        // Verifica che il messaggio contenga una stringa relativa all'utente non
        // trovato
        assertTrue(ex.getMessage().toLowerCase().contains("user") ||
                ex.getMessage().toLowerCase().contains("customer") ||
                ex.getMessage().contains("not found"));
    }

    @Test
    void unit_createConnection_throwsIfMachineNotFound() {
        // Prima mock un customer valido
        User user = new User(999L, "test@email.com", "Test", "User", "pwd");
        Customer customer = new Customer(50L, user, 3.2);
        when(emMock.find(Customer.class, 50L)).thenReturn(customer);

        // Poi mock che la macchina non esista
        when(emMock.find(ConcreteVendingMachine.class, "idX")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            daoMocked.createConnection(50L, "idX");
        });

        // Verifica che il messaggio contenga una stringa relativa alla macchina non
        // trovata
        assertTrue(ex.getMessage().toLowerCase().contains("machine") ||
                ex.getMessage().toLowerCase().contains("vending") ||
                ex.getMessage().contains("not found"));
    }

    @Test
    void unit_deleteConnection_removesIfExists() {
        User user = mock(User.class);
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        Connection c = new Connection(user, machine, Instant.now());
        when(emMock.find(Connection.class, 55L)).thenReturn(c);

        daoMocked.deleteConnection(55L);

        verify(emMock).remove(c);
        verify(txMock).begin();
        verify(txMock).commit();
    }

    @Test
    void unit_deleteConnection_doesNothingIfNotFound() {
        when(emMock.find(Connection.class, 99L)).thenReturn(null);

        daoMocked.deleteConnection(99L);

        verify(emMock, never()).remove(any());
        // Se la connessione non esiste, non dovrebbe iniziare nemmeno la transazione
    }

    @Test
    void unit_getConnectionById_returnsCorrectly() {
        User user = mock(User.class);
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        Connection c = new Connection(user, machine, Instant.now());
        when(emMock.find(Connection.class, 44L)).thenReturn(c);

        Connection result = daoMocked.getConnectionById(44L);

        assertSame(c, result);
    }

    @Test
    void unit_getConnectionById_returnsNullIfMissing() {
        when(emMock.find(Connection.class, 222L)).thenReturn(null);

        assertNull(daoMocked.getConnectionById(222L));
    }

    // ------- INTEGRATION TEST -------- //
    EntityManagerFactory emfReal;
    ConnectionDaoImpl daoReal;

    @BeforeAll
    void setupIntegration() {
        emfReal = Persistence.createEntityManagerFactory("test-pu");
        daoReal = new ConnectionDaoImpl(emfReal);
    }

    @AfterAll
    void closeIntegration() {
        if (emfReal != null)
            emfReal.close();
    }

    @Test
    void integration_CRUD_flow() {
        // 1. Prepara utente & macchina & inventory
        EntityManager em = emfReal.createEntityManager();
        em.getTransaction().begin();

        // Crea User e Customer
        User user = new User("test@email.com", "Test", "User", "pwd");
        em.persist(user);
        em.flush(); // Assicura che l'ID sia generato

        Customer customer = new Customer(user, 50);
        em.persist(customer);

        // Crea la macchina
        ConcreteVendingMachine machine = new ConcreteVendingMachine(
                "macchinaTest",
                null,
                "location",
                5,
                MachineStatus.Operative);
        machine.setLastMaintenance(Instant.now());
        em.persist(machine);

        em.getTransaction().commit();
        em.close();

        // 2. CREATE
        Connection connection = daoReal.createConnection(customer.getId(), machine.getId());
        assertNotNull(connection.getId());
        assertEquals(customer.getId(), connection.getUserId());
        assertEquals(machine.getId(), connection.getMachineId());

        // 3. READ
        Connection loaded = daoReal.getConnectionById(connection.getId());
        assertNotNull(loaded);
        assertEquals(connection.getUserId(), loaded.getUserId());

        // 4. DELETE
        daoReal.deleteConnection(connection.getId());
        Connection afterDelete = daoReal.getConnectionById(connection.getId());
        assertNull(afterDelete);
    }
}