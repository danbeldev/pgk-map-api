
-- DROP TABLE borders;
-- DROP TABLE points;
-- DROP TABLE fields;

CREATE TABLE fields
(
    id bigint generated always as identity,
    name varchar(64) not null,
    folder_id varchar(64) not null,
    date date not null,

    CONSTRAINT PK__fields__key PRIMARY KEY(id)
);

CREATE TABLE points
(
    id bigint generated always as identity,
    name varchar(64) not null,
    latitude double precision not null,
    longitude double precision not null,
    speed_GPS double precision not null,
    rotate_GPS double precision not null,
    altitude double precision not null,
    roll double precision not null,
    pitch double precision not null,
    rotate int not null,
    v_Ref double precision not null,
    time_fly int not null,
    altitude_PVD double precision not null,
    speed_PVD double precision not null,
    num_Foto1 int not null,
    date_time timestamp not null,
    field_id bigint not null,

    CONSTRAINT PK__points__key PRIMARY KEY(id),
    CONSTRAINT FK__points__field FOREIGN KEY(field_id) REFERENCES fields(id)
);

CREATE TABLE borders
(
    id bigint generated always as identity,
    latitude double precision not null,
    longitude double precision not null,
    field_id bigint not null,

    CONSTRAINT PK__borders__key PRIMARY KEY(id),
    CONSTRAINT FK__borders__field FOREIGN KEY(field_id) REFERENCES fields(id)
);