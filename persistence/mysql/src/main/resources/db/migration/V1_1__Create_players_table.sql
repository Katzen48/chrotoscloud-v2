create table players (
    unique_id varchar(255) not null,
    name varchar(255),
    created_at datetime,
    updated_at datetime,
    deleted_at datetime,

    primary key (unique_id)
) engine=InnoDB