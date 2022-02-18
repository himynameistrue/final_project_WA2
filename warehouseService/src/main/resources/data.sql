INSERT INTO warehouse_service_db.warehouse(id, location, name)
VALUES (1, 'Grugliasco', 'Amazon W'),
       (2, 'Nichelino', 'Amazon E');


INSERT INTO warehouse_service_db.product(id, average_rating, category, creation_date, description, name, picture_url,
                                         price)
VALUES (1, 4.4, 'clothing', NOW(), 'Comfy jeans', 'Levi\'s jeans', null, 1.2),
       (2, 3.9, 'food', NOW(), 'Just a regular pumpkin', 'A pumpkin', null, 2.7);

INSERT INTO warehouse_service_db.warehouse_product(product_id, warehouse_id, alarm, quantity)
VALUES (1, 1, 3, 5),
       (1, 2, 3, 100),
       (2, 1, 3, 100),
       (2, 2, 3, 5);