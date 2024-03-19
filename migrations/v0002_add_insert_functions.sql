CREATE OR REPLACE FUNCTION insert_link(
  p_id BIGINT,
  p_url VARCHAR,
  p_service_type VARCHAR,
  p_tracked_data JSONB,
  p_last_check TIMESTAMP WITH TIME ZONE
)
RETURNS TABLE(
  id BIGINT,
  url TEXT,
  service_type VARCHAR,
  tracked_data JSONB,
  last_check TIMESTAMP WITH TIME ZONE
)
AS $$
BEGIN
  IF p_id IS NULL THEN
    RETURN QUERY
    INSERT INTO link (url, service_type, tracked_data, last_check)
    VALUES (p_url, p_service_type, p_tracked_data, p_last_check)
    RETURNING *;
  ELSE
    RETURN QUERY
    INSERT INTO link (id, url, service_type, tracked_data, last_check)
    VALUES (p_id, p_url, p_service_type, p_tracked_data, p_last_check)
    RETURNING *;
  END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_subscription(
  p_id BIGINT,
  p_chat_id BIGINT,
  p_link_id BIGINT,
  p_alias VARCHAR
)
RETURNS TABLE(
  id BIGINT,
  chat_id BIGINT,
  link_id BIGINT,
  alias VARCHAR
)
AS $$
BEGIN
  IF p_id IS NULL THEN
    RETURN QUERY
    INSERT INTO subscription (chat_id, link_id, alias)
    VALUES (p_chat_id, p_link_id, p_alias)
    RETURNING *;
  ELSE
    RETURN QUERY
    INSERT INTO subscription (id, chat_id, link_id, alias)
    VALUES (p_id, p_chat_id, p_link_id, p_alias)
    RETURNING *;
  END IF;
END;
$$ LANGUAGE plpgsql;
