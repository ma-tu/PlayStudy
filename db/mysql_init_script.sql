CREATE DATABASE IF NOT EXISTS play_world DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TABLE IF NOT EXISTS play_world.person (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    email varchar(255),
    birthday date,
    PRIMARY KEY (id)
);

delete from play_world.person;
INSERT INTO play_world.person(id, name, email, birthday) VALUES (1, 'user1', 'user1@example.com', '2000-01-01');
INSERT INTO play_world.person(id, name, email, birthday) VALUES (2, 'user2', 'user2@example.com', '1980-01-01');
INSERT INTO play_world.person(id, name, email, birthday) VALUES (3, 'user3', 'user3@example.com', null);
commit;
