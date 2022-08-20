create table players (
    unique_id varchar(36) not null,
    name varchar(20),
    rank_unique_id varchar(36),
    created_at datetime not null,
    updated_at datetime not null,
    deleted_at datetime,

    primary key (unique_id),
    index (name),
    index (rank_unique_id),
    index (deleted_at)
) engine=InnoDB;

create table transactions (
                          entry_no bigint(20) not null auto_increment,
                          transaction_code varchar(36) not null,
                          account_type varchar(30) not null,
                          account_id varchar(36) not null,
                          from_unique_id varchar(36),
                          to_unique_id varchar(36),
                          type varchar(30) not null,
                          origin varchar(30) not null,
                          amount float not null,
                          absolute float not null,
                          positive bit not null,
                          created_at datetime not null,

                          primary key (entry_no),
                          index (transaction_code),
                          index (account_type, account_id),
                          index (from_unique_id),
                          index (to_unique_id),
                          index (created_at)
) engine=InnoDB;

create table accounts (
                          unique_id varchar(36) not null,
                          account_type varchar(30) not null,
                          owner_unique_id varchar(36) not null,
                          balance float not null,
                          credit_limit float not null,
                          balance_limit float not null,
                          shared_account bit not null,
                          created_at datetime not null,
                          updated_at datetime not null,
                          deleted_at datetime,

                          primary key (unique_id),
                          index (account_type),
                          index (owner_unique_id),
                          index (deleted_at)
) engine=InnoDB;

create table game_stats (
                            unique_id varchar(36) not null,
                            name varchar(100) not null,
                            game_mode varchar(100) not null,
                            player_unique_id varchar(36) not null,
                            value double not null,
                            created_at datetime not null,
                            updated_at datetime not null,
                            deleted_at datetime,

                            primary key (unique_id),
                            index (player_unique_id, game_mode, name),
                            index (deleted_at)
) engine=InnoDB;

create table game_states (
                            unique_id varchar(36) not null,
                            name varchar(100) not null,
                            game_mode varchar(100) not null,
                            player_unique_id varchar(36) not null,
                            state text not null,
                            created_at datetime not null,
                            updated_at datetime not null,
                            deleted_at datetime,

                            primary key (unique_id),
                            index (player_unique_id, game_mode, name),
                            index (deleted_at)
) engine=InnoDB;

create table player_inventories (
                            unique_id varchar(36) not null,
                            game_mode varchar(100) not null,
                            player_unique_id varchar(36) not null,
                            content text not null,
                            created_at datetime not null,
                            updated_at datetime not null,
                            deleted_at datetime,

                            primary key (unique_id),
                            index (player_unique_id, game_mode),
                            index (deleted_at)
) engine=InnoDB;

create table permissions (
                             unique_id varchar(36) not null,
                             name varchar(100) not null,
                             value bit not null,
                             permissible_type varchar(30) not null,
                             permissible_unique_id varchar(36) not null,

                             primary key (unique_id),
                             index (permissible_unique_id, permissible_type),
                             index (name)
) engine=InnoDB;

create table ranks (
                       unique_id varchar(36) not null,
                       name varchar(100) not null,
                       team bit not null,
                       default_rank bit not null,
                       prefix varchar(20) not null,
                       parent_unique_id varchar(36),
                       created_at datetime not null,
                       updated_at datetime not null,
                       deleted_at datetime,

                       primary key (unique_id),
                       index (parent_unique_id),
                       index (team),
                       index (default_rank),
                       index (name),
                       index (deleted_at)
) engine=InnoDB;

create table bans (
                    unique_id varchar(36) not null,
                    player_unique_id varchar(36) not null,
                    reason varchar(100) not null,
                    created_at datetime not null,
                    expires_at datetime,

                    primary key (unique_id),
                    index (player_unique_id),
                    index (created_at),
                    index (expires_at)
) engine=InnoDB;