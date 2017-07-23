-- 3.
create or replace function limit_orders() returns trigger as $$
begin
if((select count(*) from orders where o_custkey = new.o_custkey and o_orderstatus='O') >= 14) then
  raise exception 'Customer #% can have no more than 14 orders', new.o_custkey;
end if; 
return new;
end;
$$ language plpgsql;

create trigger orders_limit before insert on orders for each row
execute procedure limit_orders();

-- tests
INSERT INTO orders VALUES
(99098, 112, 'O', 99.00, NOW(), '5-LOW', 'Clerk#99', 0, 'IWillPass');

INSERT INTO orders VALUES
(99098, 112, 'O', 99.00, NOW(), '5-LOW', 'Clerk#99', 0, 'IWillPass');