CREATE FUNCTION q_5() RETURNS TRIGGER AS $q_5$
    DECLARE
           new_l_orderkey    integer;
           new_o_status      integer;
    BEGIN
        new_l_orderkey = NEW.l_orderkey;
        new_o_status = 
        COUNT(*)
        FROM (
        SELECT DISTINCT (l_linestatus)
              FROM lineitem
              WHERE l_orderkey = new_l_orderkey) AS foo;
        IF new_o_status > 1
        THEN
        UPDATE orders
        SET o_orderstatus = 'P'
        WHERE o_orderkey = new_l_orderkey;
        END IF;
        RETURN NEW;
    END
$q_5$ LANGUAGE 'plpgsql';


CREATE TRIGGER q_5
AFTER INSERT ON lineitem
FOR EACH ROW EXECUTE PROCEDURE q_5();

