-- Admins
INSERT INTO admin (id, email, password, name) VALUES (1, 'admin1@smartvend.com', 'adminpass', 'Alice Admin');
INSERT INTO admin (id, email, password, name) VALUES (2, 'admin2@smartvend.com', 'adminpass', 'Bob Admin');

-- Workers
INSERT INTO worker (id, email, password, name, isactive) VALUES (10, 'worker1@smartvend.com', 'workerpass', 'Charlie Worker', true);
INSERT INTO worker (id, email, password, name, isactive) VALUES (11, 'worker2@smartvend.com', 'workerpass', 'Dana Worker', false);

-- Customers
INSERT INTO customer (id, email, password, name, wallet) VALUES (100, 'customer1@smartvend.com', 'custpass', 'Eve Customer', 50.00);
INSERT INTO customer (id, email, password, name, wallet) VALUES (101, 'customer2@smartvend.com', 'custpass', 'Frank Customer', 20.00);

-- Vending Machines
INSERT INTO vendingmachine (id, location, status, type) VALUES ('VM001', 'Building A', 'ACTIVE', 'SNACKS');
INSERT INTO vendingmachine (id, location, status, type) VALUES ('VM002', 'Building B', 'MAINTENANCE', 'DRINKS');

-- Items
INSERT INTO item (id, name, price, quantity, vendingmachine_id, type) VALUES (1000, 'Coke', 1.50, 20, 'VM001', 'DRINK');
INSERT INTO item (id, name, price, quantity, vendingmachine_id, type) VALUES (1001, 'Chips', 2.00, 15, 'VM001', 'SNACK');
INSERT INTO item (id, name, price, quantity, vendingmachine_id, type) VALUES (1002, 'Water', 1.00, 30, 'VM002', 'DRINK');

-- Maintenance Reports
INSERT INTO maintenancereport (id, issuedescription, issuedate, machine_id) VALUES (200, 'Coin jammed', '2024-06-01', 'VM002');
INSERT INTO maintenancereport (id, issuedescription, issuedate, machine_id) VALUES (201, 'Screen not working', '2024-06-02', 'VM001');

-- Tasks
INSERT INTO task (id, assignedat, status, worker_id, supervisor_id, report_id) VALUES (300, '2024-06-03', 'ASSIGNED', 10, 1, 200);
INSERT INTO task (id, assignedat, status, worker_id, supervisor_id, report_id) VALUES (301, '2024-06-04', 'REPORTED', 11, 2, 201);

-- Transactions
INSERT INTO transaction (id, customer_id, paymentmethod, total, change) VALUES (400, 100, 'WALLET', 3.50, 0.50);

-- Transaction Items
INSERT INTO transactionitem (id, transaction_id, item_id, quantity, price) VALUES (500, 400, 1000, 1, 1.50);
INSERT INTO transactionitem (id, transaction_id, item_id, quantity, price) VALUES (501, 400, 1001, 1, 2.00);