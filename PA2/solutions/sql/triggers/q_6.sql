-- 6.
alter table nation drop constraint fk4;
alter table nation add constraint fk4 foreign key 
(n_regionkey) references region on delete cascade;

alter table supplier drop constraint fk8;
alter table supplier add constraint fk8 foreign key 
(s_nationkey) references nation on delete cascade;

alter table customer drop constraint fk1;
alter table customer add constraint fk1 foreign key 
(c_nationkey) references nation on delete set NULL;

alter table partsupp drop constraint fk6;
alter table partsupp add constraint fk6 foreign key 
(ps_suppkey) references supplier on delete cascade;

alter table lineitem drop constraint fk2;
alter table lineitem add constraint fk2 foreign key 
(l_partkey,l_suppkey) references partsupp(ps_partkey,ps_suppkey) on delete set NULL;

-- tests
DELETE FROM region WHERE r_regionkey = 3;

SELECT COUNT(*) FROM nation WHERE n_regionkey = 3;
SELECT COUNT(*) FROM supplier;
SELECT COUNT(*) FROM customer WHERE c_nationkey IS NULL;
SELECT COUNT(*) FROM partsupp;

SELECT COUNT(*) FROM lineitem
WHERE l_partkey IS NULL AND l_suppkey IS NULL;
