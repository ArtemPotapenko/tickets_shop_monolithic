-- ==========================
--   TABLE: theatre
-- ==========================
CREATE TABLE IF NOT EXISTS theatre (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        city VARCHAR(100) NOT NULL,
        address VARCHAR(255) NOT NULL
);

-- ==========================
--   TABLE: hall
-- ==========================
CREATE TABLE IF NOT EXISTS hall (
        id BIGSERIAL PRIMARY KEY,
        number INT NOT NULL,
        theatre_id BIGINT NOT NULL REFERENCES theatre(id) ON DELETE CASCADE
);

-- ==========================
--   TABLE: performance
-- ==========================
CREATE TABLE IF NOT EXISTS performance (
        id BIGSERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        description TEXT,
        duration_minutes INT
);

-- ==========================
--   TABLE: theatre_performance
-- ==========================
CREATE TABLE IF NOT EXISTS theatre_performance (
        theatre_id BIGINT NOT NULL REFERENCES theatre(id) ON DELETE CASCADE,
        performance_id BIGINT NOT NULL REFERENCES performance(id) ON DELETE CASCADE,
        PRIMARY KEY (theatre_id, performance_id)
);

-- ==========================
--   TABLE: show
-- ==========================
CREATE TABLE IF NOT EXISTS show (
        id BIGSERIAL PRIMARY KEY,
        performance_id BIGINT NOT NULL REFERENCES performance(id) ON DELETE CASCADE,
        hall_id BIGINT NOT NULL REFERENCES hall(id) ON DELETE CASCADE,
        show_time TIMESTAMP NOT NULL
);

-- ==========================
--   TABLE: seat
-- ==========================
CREATE TABLE IF NOT EXISTS seat (
        id BIGSERIAL PRIMARY KEY,
        row_number INT NOT NULL,
        seat_number INT NOT NULL,
        hall_id BIGINT NOT NULL REFERENCES hall(id) ON DELETE CASCADE
);

-- ==========================
--   TABLE: seat_price
-- ==========================
CREATE TABLE IF NOT EXISTS seat_price (
        seat_id BIGINT NOT NULL REFERENCES seat(id) ON DELETE CASCADE,
        show_id BIGINT NOT NULL REFERENCES show(id) ON DELETE CASCADE,
        price INT NOT NULL,
        PRIMARY KEY (seat_id, show_id)
);

-- ==========================
--   TABLE: ticket
-- ==========================
CREATE TABLE IF NOT EXISTS ticket (
        id BIGSERIAL PRIMARY KEY,
        show_id BIGINT NOT NULL REFERENCES show(id) ON DELETE CASCADE,
        seat_id BIGINT REFERENCES seat(id) ON DELETE CASCADE,
        status VARCHAR(20) NOT NULL
);

-- ==========================
--   TABLE: orders
-- ==========================
CREATE TABLE IF NOT EXISTS orders (
        id BIGSERIAL PRIMARY KEY,
        created_at TIMESTAMP NOT NULL,
        sum_price INT,
        reserved_at TIMESTAMP,
        status VARCHAR(20) NOT NULL
);

-- ==========================
--   TABLE: order_ticket
-- ==========================
CREATE TABLE IF NOT EXISTS order_ticket (
order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
ticket_id BIGINT NOT NULL REFERENCES ticket(id) ON DELETE CASCADE,
PRIMARY KEY (order_id, ticket_id)
);
