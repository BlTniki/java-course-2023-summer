CREATE IF NOT EXISTS TABLE chat {
    id BIGINT NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (id)
}

CREATE IF NOT EXISTS TABLE link {
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    url TEXT NOT NULL,
    last_update TIMESTAMP WITH TIMEZONE,

    PRIMARY KEY (id),
    UNIQUE (url)
}

CREATE IF NOT EXISTS TABLE link_subscription {
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    chat_id BIGINT NOT NULL,
    link_id BIGINT NOT NULL,
    alias VARCHAR(10),

    PRIMARY KEY (id),
    UNIQUE (chat_id, link_id, alias)
    FOREIGN KEY (chat_id) REFERENCES chat(id),
    FOREIGN KEY (link_id) REFERENCES link(id)
};
