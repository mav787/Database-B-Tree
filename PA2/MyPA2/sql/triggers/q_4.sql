BEGIN;
ALTER TABLE orders
DROP CONSTRAINT o_fk1;

ALTER TABLE orders
ADD CONSTRAINT o_fk1 FOREIGN KEY (o_custkey) REFERENCES customer ON DELETE CASCADE;
COMMIT;

BEGIN;
ALTER TABLE lineitem
DROP CONSTRAINT l_fk2;

ALTER TABLE lineitem
ADD CONSTRAINT l_fk2 FOREIGN KEY (l_orderkey) REFERENCES orders ON DELETE CASCADE;
COMMIT;

