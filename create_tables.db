-- 1. User base (superclasse)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    user_type VARCHAR(30) NOT NULL -- 'admin', 'worker', 'customer'
);

-- 2. Admin, Worker, Customer (ereditano da users)
CREATE TABLE admins (
    id UUID PRIMARY KEY REFERENCES users(id)
);

CREATE TABLE workers (
    id UUID PRIMARY KEY REFERENCES users(id),
    is_active BOOLEAN NOT NULL
);

CREATE TABLE customers (
    id UUID PRIMARY KEY REFERENCES users(id),
    balance DOUBLE PRECISION NOT NULL
);

-- 3. VendingMachine (Concrete + virtual model info)
CREATE TABLE vending_machines (
    serial_number UUID PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL,
    last_maintenance DATE,
    created_at DATE,
    status VARCHAR(50) NOT NULL,    -- enum: 'operative', 'outOfService', 'connected'
    model_number VARCHAR(100),
    type VARCHAR(50) NOT NULL       -- enum: 'coffee', 'snack', etc
);

-- 4. Inventory
CREATE TABLE inventories (
    id UUID PRIMARY KEY,
    machine_id UUID NOT NULL REFERENCES vending_machines(serial_number),
    occupied_space INTEGER NOT NULL
);

-- 5. Item
CREATE TABLE items (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    size INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    created_at DATE,
    type VARCHAR(50) NOT NULL,          -- enum: 'snack', 'bottle', etc
    inventory_id UUID REFERENCES inventories(id)
);

-- 6. MachineBalance
CREATE TABLE machine_balances (
    id UUID PRIMARY KEY,
    machine_id UUID NOT NULL REFERENCES vending_machines(serial_number),
    five_cents_count INTEGER DEFAULT 0,
    ten_cents_count INTEGER DEFAULT 0,
    twenty_cents_count INTEGER DEFAULT 0,
    fifty_cents_count INTEGER DEFAULT 0,
    one_euro_count INTEGER DEFAULT 0,
    two_euro_count INTEGER DEFAULT 0
);

-- 7. Transaction
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    initial_balance DOUBLE PRECISION NOT NULL,
    updated_balance DOUBLE PRECISION NOT NULL,
    customer_id UUID NOT NULL REFERENCES customers(id),
    payment_method VARCHAR(20) NOT NULL   -- enum: 'Card', 'Cash', 'Wallet'
);

-- 8. TransactionItem
CREATE TABLE transaction_items (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL REFERENCES transactions(id),
    item_id UUID NOT NULL REFERENCES items(id),
    inventory_id UUID REFERENCES inventories(id),
    total_price DOUBLE PRECISION NOT NULL
);

-- 9. MaintenanceReport
CREATE TABLE maintenance_reports (
    id UUID PRIMARY KEY,
    issue_description TEXT NOT NULL,
    issue_date DATE NOT NULL,
    machine_id UUID NOT NULL REFERENCES vending_machines(serial_number)
);

-- 10. Task
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    assigned_at TIMESTAMP NOT NULL,
    worker_id UUID NOT NULL REFERENCES workers(id),
    admin_id UUID NOT NULL REFERENCES admins(id),
    status VARCHAR(30) NOT NULL,           -- enum: 'reported', 'assigned', 'completed', etc
    report_id UUID REFERENCES maintenance_reports(id)
);
