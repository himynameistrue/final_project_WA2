TRUNCATE TABLE catalog_service_db.user;

INSERT INTO catalog_service_db.user (id, delivery_address, email, is_enabled, name ,password, roles, surname)
VALUES (1, 'Via Fantasia 4', 'topolino@email.com', true, 'Mickey', '$2a$10$J8ogw6nk1nvbsFWIvYPKWO9UAVQzj6UBliyg0/0NjZV2iyTY5Oqya', 'CUSTOMER,ADMIN', 'Mouse'),
       (2, 'Via Fantasia 1', 'paperino@email.com', true, 'Donald', '$2a$10$J8ogw6nk1nvbsFWIvYPKWO9UAVQzj6UBliyg0/0NjZV2iyTY5Oqya', 'CUSTOMER', 'Duck'),
       (3, 'Via Fantasia 15', 'pippo@email.com', true, 'Goofy', '$2a$10$J8ogw6nk1nvbsFWIvYPKWO9UAVQzj6UBliyg0/0NjZV2iyTY5Oqya', 'ADMIN', 'Goof');

UPDATE catalog_service_db.hibernate_sequence SET next_val=4;