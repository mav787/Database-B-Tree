-- 1.
alter table customer drop constraint fk1;
alter table customer add constraint fk1 foreign key 
(c_nationkey) references nation on update cascade;

alter table supplier drop constraint fk8;
alter table supplier add constraint fk8 foreign key
(s_nationkey) references nation on update cascade;

-- tests
update nation set n_nationkey = 99 where n_name = 'UNITED STATES';

select c_custkey, c_nationkey from customer where c_nationkey = 99;

select s_suppkey, s_nationkey from supplier where s_nationkey = 99;
