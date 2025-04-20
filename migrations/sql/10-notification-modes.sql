create table notification_modes
(
    chat_id bigserial primary key references users (id) on delete cascade,
    mode    varchar(16) not null check (mode in ('DIGEST', 'INSTANT')) default 'INSTANT'
);
