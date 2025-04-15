insert into users
values (1);

insert into links (url, type, uri_variables)
values ('github.com/user/repo', 'GITHUB', '{"test","repo"}');

insert into user_links (user_id, link_id)
values (1, 1);
