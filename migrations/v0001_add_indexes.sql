CREATE UNIQUE INDEX url_idx ON link (url);
CREATE INDEX last_check_idx ON link (last_check);

CREATE INDEX chat_id_idx ON subscription (chat_id);
CREATE INDEX link_id_idx ON subscription (link_id);
CREATE UNIQUE INDEX chat_id_alias_idx ON subscription (chat_id, alias);
