-- Initialize database schema
USE billing_app;

-- Table for storing bills
CREATE TABLE IF NOT EXISTS bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(15),
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    final_amount DECIMAL(10, 2) NOT NULL
);

-- Table for storing bill items
CREATE TABLE IF NOT EXISTS bill_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    item_total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE
);

-- Sample data
INSERT INTO bills (customer_name, customer_phone, total_amount, discount_percent, final_amount) VALUES 
('John Doe', '9876543210', 500.00, 10, 450.00),
('Jane Smith', '9876543211', 750.00, 5, 712.50);

INSERT INTO bill_items (bill_id, item_name, quantity, price, item_total) VALUES
(1, 'Notebook', 5, 50.00, 250.00),
(1, 'Pen Set', 2, 125.00, 250.00),
(2, 'Calculator', 1, 500.00, 500.00),
(2, 'Geometry Box', 1, 250.00, 250.00);