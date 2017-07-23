alter table customer add constraint fk1 foreign key (c_nationkey) references nation;
alter table lineitem add constraint fk2 foreign key (l_partkey, l_suppkey) references partsupp;
alter table lineitem add constraint fk3 foreign key (l_orderkey) references orders;
alter table nation add constraint fk4 foreign key (n_regionkey) references region;
alter table orders add constraint fk5 foreign key (o_custkey) references customer;
alter table partsupp add constraint fk6 foreign key (ps_suppkey) references supplier;
alter table partsupp add constraint fk7 foreign key (ps_suppkey) references part;
alter table supplier add constraint fk8 foreign key (s_nationkey) references nation;
