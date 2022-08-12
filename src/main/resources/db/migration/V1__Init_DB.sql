create sequence hibernate_sequence start 1 increment 1;

-- create table enter_balance
-- (
--     id                 bigserial not null,
--     active             double precision,
--     passive            double precision,
--     account_id         int8,
--     summary_balance_id int8,
--     primary key (id)
-- );
--
-- create table money_flow
-- (
--     id                 bigserial not null,
--     debit              double precision,
--     credit             double precision,
--     account_id         int8,
--     summary_balance_id int8,
--     primary key (id)
-- );
--
-- create table proceed_balance
-- (
--     id                 bigserial not null,
--     active             double precision,
--     passive            double precision,
--     account_id         int8,
--     summary_balance_id int8,
--     primary key (id)
-- );

create table document
(
    id            bigserial not null,
    document_name varchar(255),
    primary key (id)
);

create table class_balance
(
    id          bigserial not null,
    class_name  varchar(255),
    document_id int8,
    primary key (id)
);

create table summary_balance
(
    id          bigserial not null,
    account_id  int8,
    class_balance_id int8,
    primary key (id)
);

create table common_balance
(
    id                    bigserial not null,
    account_id            int8,
    inner_balance_active  double precision,
    inner_balance_passive double precision,
    outer_balance_active  double precision,
    outer_balance_passive double precision,
    debit                 double precision,
    credit                double precision,
    summary_balance_id    int8,
    primary key (id)
);

alter table if exists class_balance
    add constraint forgein_key_class_balance_to_document
    foreign key (document_id)
    references document;

alter table if exists summary_balance
    add constraint forgein_key_summary_balance_to_class_balance
    foreign key (class_balance_id)
    references class_balance;

alter table if exists common_balance
    add constraint forgein_key_common_balance_to_summary_balance
    foreign key (summary_balance_id)
    references summary_balance;