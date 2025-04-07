create view link_dto_view as
select *,
       coalesce((select array_agg(t.name)
                 from tags t
                          join link_tags lt on t.id = lt.tag_id
                 where lt.link_id = links.id), '{}'::text[]) as tags,
       coalesce((select array_agg(f.name)
                 from filters f
                          join link_filters lf on f.id = lf.filter_id
                 where lf.link_id = links.id), '{}'::text[]) as filters
from links;
