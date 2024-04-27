DROP
DATABASE IF EXISTS main_database;

CREATE
DATABASE main_database;

use
main_database;

create table users
(
    id                     BIGINT      NOT NULL UNIQUE AUTO_INCREMENT,
    email                  varchar(64) NOT NULL UNIQUE,
    password               varchar(64) NOT NULL,
    first_name             varchar(32) NOT NULL,
    last_name              varchar(32) NOT NULL,
    phone_number           varchar(16),
    address                varchar(128),
    birth_day              DATE        NOT NULL,
    creating_date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    primary key (id),
    index                  user_index_email (email)
) ENGINE = InnoDB;
