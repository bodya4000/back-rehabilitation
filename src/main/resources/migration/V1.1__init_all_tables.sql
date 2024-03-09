create table users
(
    login               varchar(255) primary key,
    address             varchar(255),
    contact_information varchar(255),
    email               varchar(255) not null,
    img_url             varchar(255),
    password            varchar(255) not null,

    constraint UK_users_email UNIQUE (email)
);


create table admins
(
    login varchar(255) not null primary key,

    constraint FK_admins_users FOREIGN KEY (login) references users (login)
);

create table re_hubs
(
    login  varchar(255) primary key,
    city   varchar(255),
    name   varchar(255),
    rating int not null,

    constraint FK_re_hubs_users FOREIGN KEY (login) references users (login)
);

create table clients
(
    login      varchar(255) not null primary key,
    last_name  varchar(255) not null,
    first_name varchar(255) not null,

    constraint FK_clients_users FOREIGN KEY (login) references users (login)
);

create table specialists
(
    login        varchar(255) not null primary key,
    last_name    varchar(255) not null,
    first_name   varchar(255) not null,
    speciality   varchar(255),
    city         varchar(255),
    description  varchar(255),
    re_hub_login varchar(255),
    age          int,
    experience   int,
    rate         int,

    constraint FK_specialists_users foreign key (login) references users (login),
    constraint FK_specialists_re_hubs foreign key (re_hub_login) references re_hubs (login)
);

create table clients_specialists
(
    client_login     text not null,
    specialist_login text not null,

    constraint FK_clients_specialists_clients foreign key (client_login) references clients (login),
    constraint FK_clients_specialists_specialists foreign key (specialist_login) references specialists (login),

    primary key (client_login, specialist_login)
);

create table user_roles
(
    id         bigint       not null primary key,
    role       varchar(255) not null,
    user_login varchar(255),

    constraint user_roles_role_check check ((role)::text = ANY
                                            ((ARRAY [
                                                'ROLE_CLIENT'::character varying,
                                                'ROLE_SPECIALIST'::character varying,
                                                'ROLE_REHUB'::character varying,
                                                'ROLE_ADMIN'::character varying])::text[])),
    constraint FK_user_roles_users foreign key (user_login) references users (login)

);

create table refresh_token
(
    id          bigint not null primary key,
    expiry_date timestamp(6) with time zone,
    token       varchar(255),
    user_login  varchar(255),

    constraint FK_refresh_token_users foreign key (user_login) references users (login)
);

alter table users
    owner to postgres;
alter table admins
    owner to postgres;
alter table re_hubs
    owner to postgres;
alter table clients
    owner to postgres;
alter table specialists
    owner to postgres;
alter table clients_specialists
    owner to postgres;
alter table user_roles
    owner to postgres;
alter table refresh_token
    owner to postgres;

create sequence refresh_token_seq
    increment by 50;

alter sequence refresh_token_seq owner to postgres;

create sequence user_roles_seq
    increment by 50;

alter sequence user_roles_seq owner to postgres;