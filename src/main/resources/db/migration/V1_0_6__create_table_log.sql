CREATE TABLE log
(
    id        uuid NOT NULL
        PRIMARY KEY,
    exception text,
    level     varchar(255),
    logger    varchar(255),
    message   text,
    timestamp timestamp
);
