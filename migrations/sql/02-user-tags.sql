create table user_tags
(
    user_id bigint references users (id) on delete cascade,
    tag_id  bigint references tags (id) on delete cascade,
    primary key (user_id, tag_id)
);
