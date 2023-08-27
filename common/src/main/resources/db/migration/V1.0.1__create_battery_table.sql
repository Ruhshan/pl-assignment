CREATE TABLE IF NOT EXISTS battery(
    id          bigserial    not null primary key,
    name        varchar(100) not null,
    post_code   integer      not null,
    capacity    decimal      not null,

    created_at  timestamp    not null,
    modified_at timestamp    not null
);

CREATE INDEX idx_post_code ON battery(post_code);