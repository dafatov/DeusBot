CREATE TABLE qrtz_job_details
(
    sched_name        varchar(120) NOT NULL,
    job_name          varchar(200) NOT NULL,
    job_group         varchar(200) NOT NULL,
    description       varchar(250),
    job_class_name    varchar(250) NOT NULL,
    is_durable        boolean      NOT NULL,
    is_nonconcurrent  boolean      NOT NULL,
    is_update_data    boolean      NOT NULL,
    requests_recovery boolean      NOT NULL,
    job_data          bytea,
    PRIMARY KEY (sched_name, job_name, job_group)
);

CREATE INDEX idx_qrtz_j_req_recovery
    ON qrtz_job_details (sched_name, requests_recovery);

CREATE INDEX idx_qrtz_j_grp
    ON qrtz_job_details (sched_name, job_group);

CREATE TABLE qrtz_triggers
(
    sched_name     varchar(120) NOT NULL,
    trigger_name   varchar(200) NOT NULL,
    trigger_group  varchar(200) NOT NULL,
    job_name       varchar(200) NOT NULL,
    job_group      varchar(200) NOT NULL,
    description    varchar(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority       integer,
    trigger_state  varchar(16)  NOT NULL,
    trigger_type   varchar(8)   NOT NULL,
    start_time     bigint       NOT NULL,
    end_time       bigint,
    calendar_name  varchar(200),
    misfire_instr  smallint,
    job_data       bytea,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, job_name, job_group) REFERENCES qrtz_job_details
);

CREATE INDEX idx_qrtz_t_j
    ON qrtz_triggers (sched_name, job_name, job_group);

CREATE INDEX idx_qrtz_t_jg
    ON qrtz_triggers (sched_name, job_group);

CREATE INDEX idx_qrtz_t_c
    ON qrtz_triggers (sched_name, calendar_name);

CREATE INDEX idx_qrtz_t_g
    ON qrtz_triggers (sched_name, trigger_group);

CREATE INDEX idx_qrtz_t_state
    ON qrtz_triggers (sched_name, trigger_state);

CREATE INDEX idx_qrtz_t_n_state
    ON qrtz_triggers (sched_name, trigger_name, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_t_n_g_state
    ON qrtz_triggers (sched_name, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_t_next_fire_time
    ON qrtz_triggers (sched_name, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_st
    ON qrtz_triggers (sched_name, trigger_state, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_misfire
    ON qrtz_triggers (sched_name, misfire_instr, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_st_misfire
    ON qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_state);

CREATE INDEX idx_qrtz_t_nft_st_misfire_grp
    ON qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);

CREATE TABLE qrtz_simple_triggers
(
    sched_name      varchar(120) NOT NULL,
    trigger_name    varchar(200) NOT NULL,
    trigger_group   varchar(200) NOT NULL,
    repeat_count    bigint       NOT NULL,
    repeat_interval bigint       NOT NULL,
    times_triggered bigint       NOT NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers
);

CREATE TABLE qrtz_cron_triggers
(
    sched_name      varchar(120) NOT NULL,
    trigger_name    varchar(200) NOT NULL,
    trigger_group   varchar(200) NOT NULL,
    cron_expression varchar(120) NOT NULL,
    time_zone_id    varchar(80),
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers
);

CREATE TABLE qrtz_simprop_triggers
(
    sched_name    varchar(120) NOT NULL,
    trigger_name  varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    str_prop_1    varchar(512),
    str_prop_2    varchar(512),
    str_prop_3    varchar(512),
    int_prop_1    integer,
    int_prop_2    integer,
    long_prop_1   bigint,
    long_prop_2   bigint,
    dec_prop_1    numeric(13, 4),
    dec_prop_2    numeric(13, 4),
    bool_prop_1   boolean,
    bool_prop_2   boolean,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT qrtz_simprop_triggers_sched_name_trigger_name_trigger_grou_fkey
        FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers
);

CREATE TABLE qrtz_blob_triggers
(
    sched_name    varchar(120) NOT NULL,
    trigger_name  varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    blob_data     bytea,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers
);

CREATE TABLE qrtz_calendars
(
    sched_name    varchar(120) NOT NULL,
    calendar_name varchar(200) NOT NULL,
    calendar      bytea        NOT NULL,
    PRIMARY KEY (sched_name, calendar_name)
);

CREATE TABLE qrtz_paused_trigger_grps
(
    sched_name    varchar(120) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    PRIMARY KEY (sched_name, trigger_group)
);

CREATE TABLE qrtz_fired_triggers
(
    sched_name        varchar(120) NOT NULL,
    entry_id          varchar(95)  NOT NULL,
    trigger_name      varchar(200) NOT NULL,
    trigger_group     varchar(200) NOT NULL,
    instance_name     varchar(200) NOT NULL,
    fired_time        bigint       NOT NULL,
    sched_time        bigint       NOT NULL,
    priority          integer      NOT NULL,
    state             varchar(16)  NOT NULL,
    job_name          varchar(200),
    job_group         varchar(200),
    is_nonconcurrent  boolean,
    requests_recovery boolean,
    PRIMARY KEY (sched_name, entry_id)
);

CREATE INDEX idx_qrtz_ft_trig_inst_name
    ON qrtz_fired_triggers (sched_name, instance_name);

CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry
    ON qrtz_fired_triggers (sched_name, instance_name, requests_recovery);

CREATE INDEX idx_qrtz_ft_j_g
    ON qrtz_fired_triggers (sched_name, job_name, job_group);

CREATE INDEX idx_qrtz_ft_jg
    ON qrtz_fired_triggers (sched_name, job_group);

CREATE INDEX idx_qrtz_ft_t_g
    ON qrtz_fired_triggers (sched_name, trigger_name, trigger_group);

CREATE INDEX idx_qrtz_ft_tg
    ON qrtz_fired_triggers (sched_name, trigger_group);

CREATE TABLE qrtz_scheduler_state
(
    sched_name        varchar(120) NOT NULL,
    instance_name     varchar(200) NOT NULL,
    last_checkin_time bigint       NOT NULL,
    checkin_interval  bigint       NOT NULL,
    PRIMARY KEY (sched_name, instance_name)
);

CREATE TABLE qrtz_locks
(
    sched_name varchar(120) NOT NULL,
    lock_name  varchar(40)  NOT NULL,
    PRIMARY KEY (sched_name, lock_name)
);
