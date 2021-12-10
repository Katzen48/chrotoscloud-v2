create table accounts (
    unique_id varchar(255) not null,
    account_type varchar(255),
    owner_id varchar(255),
    balance float not null,
    credit_limit float not null,
    balance_limit float not null,
    shared_account bit not null,
    created_at datetime,
    updated_at datetime,
    deleted_at datetime,

    primary key (unique_id)
) engine=InnoDB