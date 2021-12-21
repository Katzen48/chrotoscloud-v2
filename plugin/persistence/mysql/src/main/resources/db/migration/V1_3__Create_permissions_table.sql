create table permissions (
    unique_id varchar(255) not null,
    name varchar(100) not null,
    value bit not null,
    permissible_type varchar(100) not null,
    permissible varchar(255) not null,

    primary key (unique_id)
) engine=InnoDB