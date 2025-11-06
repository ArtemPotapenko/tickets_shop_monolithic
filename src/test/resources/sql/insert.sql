INSERT INTO theatre (name, city, address)
VALUES ('Мариинский театр', 'Санкт-Петербург', 'Театральная пл., 1'),
       ('Большой театр', 'Москва', 'Театральная пл., 2');

INSERT INTO hall (number, theatre_id) VALUES (1, 1), (2, 1), (1, 2);

INSERT INTO performance (title, description, duration_minutes)
VALUES ('Щелкунчик', 'Балет Чайковского', 120),
       ('Лебединое озеро', 'Классический балет', 130);

INSERT INTO theatre_performance (theatre_id, performance_id) VALUES (1, 1), (2, 2);

INSERT INTO show (performance_id, hall_id, show_time)
VALUES (1, 1, now() + interval '1 day'),
       (2, 3, now() + interval '2 day');

INSERT INTO seat (row_number, seat_number, hall_id)
VALUES (1, 1, 1), (1, 2, 1), (2, 1, 1);

INSERT INTO seat_price (seat_id, show_id, price)
VALUES (1, 1, 1000), (2, 1, 1200), (3, 1, 900);

INSERT INTO ticket (show_id, seat_id, status)
VALUES (1, 1, 'RESERVED'), (1, 2, 'PAID'), (2, 1, 'CANCELLED');

INSERT INTO orders (created_at, reserved_at, status)
VALUES (now(), now(), 'RESERVED'),
       (now(), null, 'PAID');

INSERT INTO order_ticket (order_id, ticket_id)
VALUES (1, 1), (2, 2);
