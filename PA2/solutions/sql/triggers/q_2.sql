-- 2.
create or replace function part_price() returns trigger as $$
begin
if(new.p_partkey = old.p_partkey and new.p_retailprice != old.p_retailprice) then
	update partsupp set ps_supplycost = ps_supplycost * (new.p_retailprice/old.p_retailprice)
	where partsupp.ps_partkey = new.p_partkey;
end if;
return new;
end;
$$ language plpgsql;

create trigger part_partsupp_price after update on part for each row
execute procedure part_price();

-- tests
UPDATE part SET p_retailprice = p_retailprice * 1.1
WHERE p_partkey = 1;

SELECT ps_supplycost FROM partsupp WHERE ps_partkey = 1;
