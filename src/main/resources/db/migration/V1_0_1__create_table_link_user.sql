CREATE TABLE link_user
(
    discord_principal_name varchar(255) NOT NULL,
    linked_registration_id varchar(255) NOT NULL,
    linked_principal_name  varchar(255),
    PRIMARY KEY (discord_principal_name, linked_registration_id)
);
