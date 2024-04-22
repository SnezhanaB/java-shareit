DROP TABLE IF EXISTS users, requests, items, bookings;

CREATE TABLE IF NOT EXISTS users (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name varchar(255) NOT NULL, email varchar(512) NOT NULL, CONSTRAINT UQ_USER_EMAIL UNIQUE (email));

CREATE TABLE IF NOT EXISTS items (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name varchar(255) NOT NULL, description varchar(1000) NOT NULL, available BOOLEAN, owner_id BIGINT, CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users(id), CONSTRAINT UQ_ITEM_NAME UNIQUE (name));
