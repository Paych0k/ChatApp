create table if not exists users
(
    id       BIGSERIAL PRIMARY KEY,
    username varchar(255) not null unique,
    password varchar(255) not null
);

CREATE TABLE IF NOT EXISTS chatroom
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    owner_id   BIGINT       NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chatroom_participant
(
    chatroom_id BIGINT    NOT NULL,
    user_id     BIGINT    NOT NULL,
    joined_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (chatroom_id, user_id),
    FOREIGN KEY (chatroom_id) REFERENCES chatroom (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS message
(
    id           BIGSERIAL PRIMARY KEY,
    username_id  BIGINT NOT NULL,
    text         TEXT,
    message_time TIMESTAMP,
    chatroom_id  BIGINT NOT NULL,
    FOREIGN KEY (username_id) REFERENCES users (id),
    FOREIGN KEY (chatroom_id) REFERENCES chatroom (id) ON DELETE CASCADE
);


