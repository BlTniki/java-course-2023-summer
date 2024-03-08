CREATE TABLE IF NOT EXISTS chat (
    id BIGINT NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS link (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    url TEXT NOT NULL,
    last_update TIMESTAMP WITH TIME ZONE,

    PRIMARY KEY (id),
    UNIQUE (id),
    UNIQUE (url)
);

CREATE TABLE IF NOT EXISTS link_subscription (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    chat_id BIGINT NOT NULL,
    link_id BIGINT NOT NULL,
    alias VARCHAR(10),

    PRIMARY KEY (id),
    UNIQUE (chat_id, link_id, alias),
    FOREIGN KEY (chat_id) REFERENCES chat(id),
    FOREIGN KEY (link_id) REFERENCES link(id)
);
