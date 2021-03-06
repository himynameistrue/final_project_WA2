TRUNCATE TABLE order_service_db.orders;

INSERT INTO order_service_db.orders(id, buyer_id, status, created_at, updated_at)
VALUES (1, 1, 1, NOW(), NOW()),
       (2, 2, 1, NOW(), NOW()),
       (3, 1, 2, NOW(), NOW());

TRUNCATE TABLE order_service_db.order_product;

INSERT INTO order_service_db.order_product(product_id, amount, unit_price, order_id)
VALUES (1, 2, 1.2, 1),
       (2, 3, 2.7, 1),
       (1, 2, 1.2, 2),
       (2, 3, 2.7, 3);

UPDATE order_service_db.hibernate_sequence SET next_val=4;