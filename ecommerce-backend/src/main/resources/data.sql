INSERT INTO users (email, password_hash, full_name)
SELECT 'roderikmogot@example.com',
       'roderikmogot',
       'Roderik Mogot' WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'roderikmogot@example.com');

INSERT INTO products (name, description, price, stock_quantity)
SELECT 'Laptop Pro',
       'A powerful and sleek laptop.',
       1200.00,
       50 WHERE NOT EXISTS (SELECT 1 FROM products WHERE NAME = 'Laptop Pro');

INSERT INTO products (name, description, price, stock_quantity)
SELECT 'Wireless Mouse',
       'Ergonomic wireless mouse.',
       49.99,
       200 WHERE NOT EXISTS (SELECT 1 FROM products WHERE NAME = 'Wireless Mouse');

INSERT INTO products (name, description, price, stock_quantity)
SELECT 'Mechanical Keyboard',
       'Clicky and satisfying to type on.',
       150.50,
       75 WHERE NOT EXISTS (SELECT 1 FROM products WHERE NAME = 'Mechanical Keyboard');