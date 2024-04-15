DROP TABLE IF EXISTS users, requests, items, bookings;
--
--CREATE TABLE IF NOT EXISTS users (
--    user_id integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
--    email varchar,
--    name varchar
--);
--
--CREATE TABLE IF NOT EXISTS requests (
--    request_id integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
--    description varchar,
--    requester_id integer,
--    FOREIGN KEY (requester_id) REFERENCES users (user_id) ON DELETE CASCADE
--);
--
--CREATE TABLE IF NOT EXISTS items (
--    item_id integer,
--    name varchar,
--    description varchar,
--    is_available BOOLEAN,
--    owner_id integer,
--    request_id integer,
--    FOREIGN KEY (owner_id) REFERENCES users (user_id) ON DELETE CASCADE,
--    FOREIGN KEY (request_id) REFERENCES requests (request_id) ON DELETE CASCADE
--);
--
--CREATE TABLE IF NOT EXISTS bookings (
--    booking_id integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
--    start_time timestamp,
--    end_time timestamp,
--    item_id integer,
--    booker_id integer,
--    status ENUM('WAITING', 'APPROVED', 'REJECTED', 'CANCELED'),
--    FOREIGN KEY (booker_id) REFERENCES users (user_id) ON DELETE CASCADE,
--    FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE
--);