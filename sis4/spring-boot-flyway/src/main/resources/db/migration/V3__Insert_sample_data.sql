-- Insert sample users
INSERT INTO users (first_name, last_name, email, phone) VALUES
('John', 'Doe', 'john.doe@example.com', '+1-555-0101'),
('Jane', 'Smith', 'jane.smith@example.com', '+1-555-0102'),
('Mike', 'Johnson', 'mike.johnson@example.com', '+1-555-0103'),
('Sarah', 'Wilson', 'sarah.wilson@example.com', '+1-555-0104'),
('David', 'Brown', 'david.brown@example.com', '+1-555-0105');

-- Insert sample orders
INSERT INTO orders (user_id, order_number, total_amount, status, order_date) VALUES
(1, 'ORD-2024-001', 299.99, 'DELIVERED', '2024-01-15'),
(2, 'ORD-2024-002', 149.50, 'SHIPPED', '2024-01-16'),
(1, 'ORD-2024-003', 75.25, 'CONFIRMED', '2024-01-17'),
(3, 'ORD-2024-004', 425.00, 'PENDING', '2024-01-18'),
(4, 'ORD-2024-005', 89.99, 'DELIVERED', '2024-01-19'),
(2, 'ORD-2024-006', 199.99, 'CANCELLED', '2024-01-20'),
(5, 'ORD-2024-007', 359.75, 'SHIPPED', '2024-01-21');