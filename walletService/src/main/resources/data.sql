TRUNCATE TABLE wallet_service_db.wallet;

INSERT INTO wallet_service_db.wallet(id, amount, customer_id)
VALUES (1, 1000, 1),
       (2, 0, 2),
       (3, 5000, 3);

TRUNCATE TABLE wallet_service_db.transaction;

INSERT INTO wallet_service_db.transaction(id, amount, order_id, timestamp, customer_wallet_id)
VALUES (1, 10.5, 1, NOW(), 1),
       (2, 2.4, 2, NOW(), 2),
       (3, 8.1, 3, NOW(), 1);