CREATE TABLE audit
(
    guild_id      varchar(255) NOT NULL,
    name          varchar(255) NOT NULL,
    type          varchar(255) NOT NULL,
    user_id       varchar(255) NOT NULL,
    count         bigint,
    created       timestamp(6) WITH TIME ZONE,
    last_modified timestamp(6) WITH TIME ZONE,
    PRIMARY KEY (guild_id, name, type, user_id)
);
