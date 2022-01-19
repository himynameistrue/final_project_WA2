# create databases
CREATE DATABASE IF NOT EXISTS `catalog_service_db`;
CREATE DATABASE IF NOT EXISTS `order_service_db`;
CREATE DATABASE IF NOT EXISTS `wallet_service_db`;
CREATE DATABASE IF NOT EXISTS `warehouse_service_db`;

# create root user and grant rights
#CREATE USER 'root'@'localhost' IDENTIFIED BY 'moreSecret';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';