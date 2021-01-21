CREATE SCHEMA `critter` ;

CREATE USER 'sa'@'localhost' IDENTIFIED BY 'sa1234'; -- Create the user if you haven’t yet
GRANT ALL ON critter.* TO 'sa'@'localhost'; -- Gives all privileges to the new user on critter
