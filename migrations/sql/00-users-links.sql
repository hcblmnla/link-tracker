create table users
(
    id bigserial primary key
);

create table links
(
    id            serial primary key,
    url           text        not null unique,
    type          varchar(31) not null,
    uri_variables text[]      not null,
    updated_at    timestamp   not null default '-infinity'::timestamp
);

create index idx_url on links (url);

create table user_links
(
    user_id bigint references users (id) on delete cascade,
    link_id bigint references links (id) on delete cascade,
    primary key (user_id, link_id)
);

create table tags
(
    id   serial primary key,
    name varchar(255) not null unique
);

create table filters
(
    id   serial primary key,
    name varchar(255) not null unique
);

create table link_tags
(
    link_id integer references links (id) on delete cascade,
    tag_id  integer references tags (id) on delete cascade,
    primary key (link_id, tag_id)
);

create table link_filters
(
    link_id   integer references links (id) on delete cascade,
    filter_id integer references filters (id) on delete cascade,
    primary key (link_id, filter_id)
);

create index idx_tag_id on link_tags (tag_id);
create index idx_filter_id on link_filters (filter_id);

create index idx_tags_name on tags (name);
create index idx_filters_name on filters (name);
