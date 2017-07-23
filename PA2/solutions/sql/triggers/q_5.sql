-- 5.
alter table lineitem drop constraint fk2;

create or replace function lineitem_order() returns trigger as $$
begin
if((select count(distinct l_linestatus) from lineitem where l_orderkey = new.l_orderkey) > 1) then
	update orders set o_orderstatus = 'P' where o_orderkey = new.l_orderkey;
else
	update orders set o_orderstatus = new.l_linestatus where o_orderkey = new.l_orderkey;
end if;
return new;
end;
$$ language plpgsql;

create trigger lineitem_order after insert on lineitem for each row
execute procedure lineitem_order();

-- test
SELECT o_orderstatus FROM orders WHERE o_orderkey = 7;

INSERT INTO LINEITEM VALUES
(7, 1, 2, 8, 1, 99.00, 0.00, 0.05, 'N', 'F', NOW(), NOW(), NOW(), 'NONE', 'MAIL', 'No Comment');
