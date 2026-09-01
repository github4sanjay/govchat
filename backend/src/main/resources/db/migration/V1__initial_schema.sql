CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE message (
    id UUID PRIMARY KEY,
    client_message_id UUID NOT NULL,
    sender_id UUID NOT NULL REFERENCES app_user(id),
    recipient_id UUID NOT NULL REFERENCES app_user(id),
    content VARCHAR(2000) NOT NULL,
    sent_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_message_sender_client UNIQUE (sender_id, client_message_id),
    CONSTRAINT chk_message_not_self CHECK (sender_id <> recipient_id),
    CONSTRAINT chk_message_content_not_blank CHECK (length(trim(content)) > 0)
);

CREATE INDEX idx_message_conversation
    ON message (sender_id, recipient_id, sent_at, id);
