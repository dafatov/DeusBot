CREATE TABLE session
(
    guild_id varchar(255)                NOT NULL,
    user_id  varchar(255)                NOT NULL,
    finish   timestamp(6) WITH TIME ZONE,
    start    timestamp(6) WITH TIME ZONE NOT NULL,
    PRIMARY KEY (guild_id, user_id)
);
