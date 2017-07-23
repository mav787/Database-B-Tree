ALTER TABLE customer
ADD CONSTRAINT c_fk1 FOREIGN KEY (c_nationkey) REFERENCES nation;

ALTER TABLE lineitem
ADD CONSTRAINT l_fk1 FOREIGN KEY (l_partkey, l_suppkey) REFERENCES partsupp;

ALTER TABLE lineitem
ADD CONSTRAINT l_fk2 FOREIGN KEY (l_orderkey) REFERENCES orders;

ALTER TABLE nation
ADD CONSTRAINT n_fk1 FOREIGN KEY (n_regionkey) REFERENCES region;

ALTER TABLE orders
ADD CONSTRAINT o_fk1 FOREIGN KEY (o_custkey) REFERENCES customer;

ALTER TABLE partsupp
ADD CONSTRAINT ps_fk1 FOREIGN KEY (ps_suppkey) REFERENCES supplier;

ALTER TABLE partsupp
ADD CONSTRAINT ps_fk2 FOREIGN KEY (ps_partkey) REFERENCES part;

ALTER TABLE supplier
ADD CONSTRAINT s_fk1 FOREIGN KEY (s_nationkey) REFERENCES nation;

