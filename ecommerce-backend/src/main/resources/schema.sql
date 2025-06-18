-- Users table to store customer information
CREATE TABLE IF NOT EXISTS users
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    email
    VARCHAR
(
    255
) NOT NULL UNIQUE,
    password_hash VARCHAR
(
    255
) NOT NULL,
    full_name VARCHAR
(
    255
),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );
-- Products table with stock and version for optimistic locking
CREATE TABLE IF NOT EXISTS products
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    NAME
    VARCHAR
(
    255
) NOT NULL,
    description TEXT,
    price NUMERIC
(
    10,
    2
) NOT NULL,
    stock_quantity INT NOT NULL,
    -- version is crucial for optimistic locking to prevent concurrency issues
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );
-- Orders table to store customer orders
CREATE TABLE IF NOT EXISTS orders
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL
    REFERENCES
    users
(
    id
),
    status VARCHAR
(
    50
) NOT NULL,
    -- e.g., PENDING, COMPLETED, FAILED
    total_amount NUMERIC
(
    10,
    2
) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );
-- Order items junction table
CREATE TABLE IF NOT EXISTS order_items
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    order_id
    BIGINT
    NOT
    NULL
    REFERENCES
    orders
(
    id
),
    product_id BIGINT NOT NULL REFERENCES products
(
    id
),
    quantity INT NOT NULL,
    price_per_item NUMERIC
(
    10,
    2
) NOT NULL
    );
