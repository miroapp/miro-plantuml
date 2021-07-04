
CREATE TABLE token (
    access_token VARCHAR(64) PRIMARY KEY,
    access_token_payload VARCHAR(256) NOT NULL,
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    state VARCHAR(64) NOT NULL,
    created_time TIMESTAMP NOT NULL,
    last_accessed_time TIMESTAMP NULL
);

-- todo index
