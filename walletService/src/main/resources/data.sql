INSERT INTO wallet_service_db.wallet(id, amount, customer_id)
VALUES (1, 1000, 1),
       (2, 0, 2),
       (3, 5000, 3);

INSERT INTO wallet_service_db.transaction(id, amount, timestamp, customer_wallet_id)
VALUES (1, 10.5, NOW(), 1),
       (2, 2.4, NOW(), 2),
       (3, 8.1, NOW(), 1);