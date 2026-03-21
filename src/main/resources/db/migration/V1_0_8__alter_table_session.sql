ALTER TABLE session
    ADD states VARCHAR(255)[] NOT NULL default '{}';
