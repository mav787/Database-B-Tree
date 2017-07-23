-- 7.
create or replace function region_update() returns trigger as $$
begin
if((select n_regionkey from nation where n_nationkey = old.s_nationkey) = 1) then
	if((select n_regionkey from nation where n_nationkey = new.s_nationkey) = 2) then
		update partsupp set ps_supplycost = ps_supplycost * 0.8 where ps_suppkey = old.s_suppkey;
	elsif((select n_regionkey from nation where n_nationkey = new.s_nationkey) = 3) then
		update partsupp set ps_supplycost = ps_supplycost * 1.05 where ps_suppkey = old.s_suppkey;
	end if;
end if;

if((select n_regionkey from nation where n_nationkey = old.s_nationkey) = 2) then
	if((select n_regionkey from nation where n_nationkey = new.s_nationkey) = 1) then
		update partsupp set ps_supplycost = ps_supplycost * 1.2 where ps_suppkey = old.s_suppkey;
	elsif((select n_regionkey from nation where n_nationkey = new.s_nationkey) = 3) then
		update partsupp set ps_supplycost = ps_supplycost * 1.1 where ps_suppkey = old.s_suppkey;
	end if;
end if;

if((select n_regionkey from nation where n_nationkey = old.s_nationkey) = 3) then
	if((select n_regionkey from nation where n_nationkey = new.s_nationkey) = 1) then
		update partsupp set ps_supplycost = ps_supplycost * 0.9 where ps_suppkey = old.s_suppkey;
	elsif((select n_regionkey from nation where n_nationkey = new.s_nationkey) = 2) then
		update partsupp set ps_supplycost = ps_supplycost * 0.95 where ps_suppkey = old.s_suppkey;
	end if;
end if;
return new;
end;
$$ language plpgsql;

create trigger region_update after update on supplier
for each row execute procedure region_update();


-- tests
SELECT SUM(ps_supplycost) FROM partsupp WHERE ps_suppkey = 1;

UPDATE supplier SET s_nationkey = 6 WHERE s_suppkey = 1;
