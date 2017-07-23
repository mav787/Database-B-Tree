-- 4.
alter table orders drop constraint fk5;
alter table orders add constraint fk5 foreign key 
(o_custkey) references customer on delete cascade;

alter table lineitem drop constraint fk3;
alter table lineitem add constraint fk3 foreign key 
(l_orderkey) references orders on delete cascade;

-- tests
DELETE FROM customer WHERE c_custkey = 203;

SELECT l_orderkey, l_linenumber
FROM lineitem
WHERE l_orderkey IN
(SELECT o_orderkey FROM orders WHERE o_custkey = 203);
