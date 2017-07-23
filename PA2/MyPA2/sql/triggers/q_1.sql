BEGIN;
ALTER TABLE customer
DROP CONSTRAINT c_fk1;

ALTER TABLE customer
ADD CONSTRAINT c_fk1 FOREIGN KEY (c_nationkey) REFERENCES nation ON UPDATE CASCADE;
COMMIT;

BEGIN;
ALTER TABLE supplier
DROP CONSTRAINT s_fk1;

ALTER TABLE supplier
ADD CONSTRAINT s_fk1 FOREIGN KEY (s_nationkey) REFERENCES nation ON UPDATE CASCADE;
COMMIT;
