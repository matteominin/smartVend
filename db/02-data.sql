-- Insert data into app_user
INSERT INTO app_user (email, name, surname, password, role) VALUES
('admin@example.com', 'Admin', 'User', 'password', 1),
('worker1@example.com', 'Alice', 'Johnson', 'password', 2),
('worker2@example.com', 'Bob', 'Williams', 'password', 2),
('customer1@example.com', 'Charlie', 'Brown', 'password', 3),
('customer2@example.com', 'Diana', 'Prince', 'password', 3),
('customer3@example.com', 'John', 'Doe', 'password', 3);

-- Insert data into Admin
INSERT INTO Admin (user_id) VALUES
((SELECT id FROM app_user WHERE email = 'admin@example.com'));

-- Insert data into Customer
INSERT INTO Customer (balance, user_id) VALUES
(100.50, (SELECT id FROM app_user WHERE email = 'customer1@example.com')),
(50.25, (SELECT id FROM app_user WHERE email = 'customer2@example.com')),
(25.00, (SELECT id FROM app_user WHERE email = 'customer3@example.com'));

-- Insert data into Worker
INSERT INTO Worker (isActive, user_id) VALUES
(TRUE, (SELECT id FROM app_user WHERE email = 'worker1@example.com')),
(TRUE, (SELECT id FROM app_user WHERE email = 'worker2@example.com'));

-- Insert data into VendingMachine
INSERT INTO VendingMachine (modelNumber, type) VALUES
('VM-COFFEE-001', 'Coffe'),
('VM-SNACK-001', 'Snack'),
('VM-PIZZONE-001', 'PizzoneFarcito');

-- Insert data into ConcreteVendingMachine
INSERT INTO ConcreteVendingMachine (serialNumber, capacity, createdAt, lastMaintenance, location, status, vending_machine_model) VALUES
('SNACK-NYC-001', 150, '2023-01-15 10:00:00', '2024-05-20 09:30:00', 'New York, Times Square', 'Operative', 'VM-SNACK-001'),
('COFFEE-LA-001', 80, '2023-02-01 11:30:00', '2024-05-18 14:00:00', 'Los Angeles, Downtown', 'Connected', 'VM-COFFEE-001'),
('PIZZONE-CHI-001', 50, '2023-03-10 09:00:00', '2024-05-15 10:00:00', 'Chicago, O''Hare Airport', 'OutOfService', 'VM-PIZZONE-001'),
('SNACK-LON-002', 120, '2023-04-05 13:00:00', '2024-05-25 11:00:00', 'London, Piccadilly Circus', 'Operative', 'VM-SNACK-001');

-- Insert data into Inventory
INSERT INTO Inventory (occupiedSpace, machine_id) VALUES
(100, 'SNACK-NYC-001'),
(50, 'COFFEE-LA-001'),
(20, 'PIZZONE-CHI-001'),
(80, 'SNACK-LON-002');

-- Insert data into Item
INSERT INTO Item (position, price, quantity, volume, createdAt, inventory_id, description, name, type) VALUES
(1, 1.75, 20, 100, '2024-05-01 10:00:00', (SELECT id FROM Inventory WHERE machine_id = 'SNACK-NYC-001'), 'Crispy potato chips', 'Potato Chips', 'Snack'),
(2, 2.50, 15, 250, '2024-05-01 10:00:00', (SELECT id FROM Inventory WHERE machine_id = 'SNACK-NYC-001'), 'Sweet chocolate bar', 'Chocolate Bar', 'Snack'),
(1, 3.00, 30, 330, '2024-05-05 11:00:00', (SELECT id FROM Inventory WHERE machine_id = 'COFFEE-LA-001'), 'Refreshing bottled water', 'Bottled Water', 'Bottle'),
(1, 4.50, 10, 500, '2024-05-10 12:00:00', (SELECT id FROM Inventory WHERE machine_id = 'PIZZONE-CHI-001'), 'Hot pepperoni pizza', 'Pepperoni Pizza', 'ExpressBeverage'), -- Assuming PizzoneFarcito items are handled as ExpressBeverage for Item type
(3, 2.00, 25, 200, '2024-05-12 09:00:00', (SELECT id FROM Inventory WHERE machine_id = 'SNACK-LON-002'), 'Fizzy orange soda', 'Orange Soda', 'Bottle');

-- Insert data into MaintenanceReport
INSERT INTO MaintenanceReport (issueDate, issueDescription, machine_id) VALUES
('2024-05-22 10:00:00', 'Coin mechanism jammed', 'SNACK-NYC-001'),
('2024-05-20 15:30:00', 'Coffee dispenser not working', 'COFFEE-LA-001'),
('2024-05-16 08:00:00', 'Heating element faulty', 'PIZZONE-CHI-001');

-- Insert data into Task
INSERT INTO Task (assignedAt, report_id, supervisor_id, worker_id, status) VALUES
('2024-05-22 11:00:00', (SELECT id FROM MaintenanceReport WHERE issueDescription = 'Coin mechanism jammed'), (SELECT id FROM Admin LIMIT 1), (SELECT id FROM Worker WHERE user_id = (SELECT id FROM app_user WHERE email = 'worker1@example.com')), 'Assigned'),
('2024-05-20 16:00:00', (SELECT id FROM MaintenanceReport WHERE issueDescription = 'Coffee dispenser not working'), (SELECT id FROM Admin LIMIT 1), (SELECT id FROM Worker WHERE user_id = (SELECT id FROM app_user WHERE email = 'worker2@example.com')), 'InProgress'),
('2024-05-16 09:00:00', (SELECT id FROM MaintenanceReport WHERE issueDescription = 'Heating element faulty'), (SELECT id FROM Admin LIMIT 1), (SELECT id FROM Worker WHERE user_id = (SELECT id FROM app_user WHERE email = 'worker1@example.com')), 'Reported');

-- Insert data into Transaction
INSERT INTO Transaction (initialBalance, updatedBalance, customer_id, date, paymentMethod) VALUES
(100.50, 98.75, (SELECT id FROM Customer WHERE user_id = (SELECT id FROM app_user WHERE email = 'customer1@example.com')), '2024-05-28 14:00:00', 'Card'),
(50.25, 47.75, (SELECT id FROM Customer WHERE user_id = (SELECT id FROM app_user WHERE email = 'customer2@example.com')), '2024-05-28 14:15:00', 'Cash'),
(25.00, 20.50, (SELECT id FROM Customer WHERE user_id = (SELECT id FROM app_user WHERE email = 'customer3@example.com')), '2024-05-29 09:00:00', 'Wallet');

-- Insert data into TransactionItem
INSERT INTO TransactionItem (amount, item_id, transaction_id, machine_id) VALUES
(1, (SELECT id FROM Item WHERE name = 'Potato Chips' LIMIT 1), (SELECT id FROM Transaction WHERE initialBalance = 100.50 LIMIT 1), 'SNACK-NYC-001'),
(1, (SELECT id FROM Item WHERE name = 'Chocolate Bar' LIMIT 1), (SELECT id FROM Transaction WHERE initialBalance = 100.50 LIMIT 1), 'SNACK-NYC-001'),
(1, (SELECT id FROM Item WHERE name = 'Bottled Water' LIMIT 1), (SELECT id FROM Transaction WHERE initialBalance = 50.25 LIMIT 1), 'COFFEE-LA-001'),
(1, (SELECT id FROM Item WHERE name = 'Pepperoni Pizza' LIMIT 1), (SELECT id FROM Transaction WHERE initialBalance = 25.00 LIMIT 1), 'PIZZONE-CHI-001');

-- Insert data into Connection
INSERT INTO Connection (customer_id, start, machine_id) VALUES
((SELECT id FROM Customer WHERE user_id = (SELECT id FROM app_user WHERE email = 'customer1@example.com')), '2024-05-28 13:58:00', 'SNACK-NYC-001'),
((SELECT id FROM Customer WHERE user_id = (SELECT id FROM app_user WHERE email = 'customer2@example.com')), '2024-05-28 14:13:00', 'COFFEE-LA-001');
