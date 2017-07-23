CREATE FUNCTION q_3() RETURNS TRIGGER AS $q_3$
    DECLARE
        new_o_custkey    integer;
    BEGIN
        new_o_custkey = NEW.o_custkey;
        IF EXISTS (SELECT o_custkey, COUNT(o_orderkey)
           FROM orders
           WHERE o_orderstatus = 'O' AND o_custkey = new_o_custkey
           GROUP BY o_custkey
           HAVING COUNT(o_orderkey) > 14)
        THEN
        ROLLBACK TRANSACTION;
        END IF;
        RETURN NEW;
    END
$q_3$ LANGUAGE 'plpgsql';


CREATE TRIGGER q_3
AFTER INSERT ON orders
FOR EACH ROW EXECUTE PROCEDURE q_3();

