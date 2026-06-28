CREATE TABLE pack
(
    id   bigserial NOT NULL
        PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE pack_word
(
    pack_id bigserial NOT NULL,
    word    VARCHAR   NOT NULL,
    CONSTRAINT pk_pack_word PRIMARY KEY (pack_id, word)
);

CREATE TABLE word
(
    text VARCHAR NOT NULL
        PRIMARY KEY
);