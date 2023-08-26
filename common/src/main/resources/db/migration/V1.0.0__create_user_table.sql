CREATE TABLE IF NOT EXISTS application_user(
    id          bigserial   not null primary key,
    email       varchar(80) not null unique,
    password    varchar(80) not null,
    role        varchar(80) not null,
    first_name  varchar(100),
    last_name   varchar(100),

    created_at  timestamp   not null,
    modified_at timestamp   not null
);