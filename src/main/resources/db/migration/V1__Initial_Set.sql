create table room
(
    room_id   bigint auto_increment
        primary key,
    name      varchar(50) not null,
    master_id bigint      not null
);

create table place
(
    id         bigint auto_increment
        primary key,
    address    varchar(255) null,
    latitude   double       not null,
    longitude  double       not null,
    place_name varchar(255) not null,
    room_id    bigint       not null,
    constraint FKn5fooimfmfn3cr1jcl1ynqlo4
        foreign key (room_id) references room (room_id)
);

create table trip_plans
(
    id              bigint auto_increment
        primary key,
    created_at      datetime(6)                                                         null,
    deleted_at      datetime(6)                                                         null,
    updated_at      datetime(6)                                                         null,
    description     varchar(255)                                                        null,
    end_date        date                                                                not null,
    group_name      varchar(255)                                                        null,
    image_url       varchar(255)                                                        null,
    location        varchar(50)                                                         not null,
    max_days        int                                                                 null,
    min_days        int                                                                 null,
    name            varchar(50)                                                         not null,
    price           varchar(255)                                                        null,
    start_date      date                                                                not null,
    status          enum ('COMPLETED', 'ONGOING', 'PLANNED')                            not null,
    trip_plan_type  enum ('COURSE', 'SCHEDULE')                                         not null,
    trip_type       enum ('DOMESTIC', 'OVERSEAS')                                       not null,
    vote_limit_time enum ('FOUR_HOURS', 'SIXTY_MINUTES', 'SIX_HOURS', 'THIRTY_MINUTES') null,
    room_id         bigint                                                              not null,
    user_id         bigint                                                              not null,
    vote_room_id    bigint                                                              null,
    constraint UK42k9hoe33eigk91y0p30g7pa1
        unique (vote_room_id),
    constraint FKja2tkp5xgslubitx3tjg7hyvk
        foreign key (room_id) references room (room_id)
);

create table vote_room
(
    id           bigint auto_increment
        primary key,
    created_at   datetime(6) null,
    deleted_at   datetime(6) null,
    updated_at   datetime(6) null,
    trip_plan_id bigint      not null,
    constraint UKcbeshvvejgwid80yuk6w3be61
        unique (trip_plan_id),
    constraint FKgc9mle9f41mxuxlol4i6rkwma
        foreign key (trip_plan_id) references trip_plans (id)
);

alter table trip_plans
    add constraint FKdkhe8451e74bkdthruplc1muu
        foreign key (vote_room_id) references vote_room (id);

create table vote
(
    id           bigint auto_increment
        primary key,
    created_at   datetime(6)          null,
    deleted_at   datetime(6)          null,
    updated_at   datetime(6)          null,
    type         enum ('BAD', 'GOOD') not null,
    trip_plan_id bigint               not null,
    vote_room_id bigint               not null,
    constraint FK6c963w1o9pwo0quv7yn0pvcgy
        foreign key (trip_plan_id) references trip_plans (id),
    constraint FK7u3cnhrdhm9ky4tpudlripwt
        foreign key (vote_room_id) references vote_room (id)
);

create table users
(
    user_id       bigint auto_increment
        primary key,
    created_at    datetime(6)  null,
    deleted_at    datetime(6)  null,
    updated_at    datetime(6)  null,
    access_token  varchar(255) null,
    email         varchar(255) not null,
    profile_image varchar(255) null,
    refresh_token varchar(255) null,
    username      varchar(255) null,
    vote_id       bigint       null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email),
    constraint FKl1vjmgij3ildpydw36401sbbw
        foreign key (vote_id) references vote (id)
);

alter table room
    add constraint FKhm55owuj9qgvc3hm9ikcd8kw5
        foreign key (master_id) references users (user_id);

create table room_member
(
    room_id bigint not null,
    user_id bigint not null,
    primary key (room_id, user_id),
    constraint FK1d9bddturxgt7hws5r59wirw8
        foreign key (user_id) references users (user_id),
    constraint FKlmp67erahqx7u5shbkc12p0lw
        foreign key (room_id) references room (room_id)
);

alter table trip_plans
    add constraint FKbmly4beva2ojcevvinyll1ccw
        foreign key (user_id) references users (user_id);

create table user_images
(
    id        bigint auto_increment
        primary key,
    image_url varchar(255) not null,
    user_id   bigint       not null,
    constraint UK17d6602b98y1l3ee0cqk320mi
        unique (user_id),
    constraint FKl1lf9kxrd8ybmovqsxcuxhw42
        foreign key (user_id) references users (user_id)
);

