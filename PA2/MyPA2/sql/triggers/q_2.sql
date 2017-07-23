
CREATE FUNCTION q_2() RETURNS TRIGGER AS $q_2$
    DECLARE 
        delta_p_partkey     integer;
        delta_p_retailprice numeric(15,2);
    BEGIN
        delta_p_partkey  = OLD.p_partkey;
        delta_p_retailprice = NEW.p_retailprice - OLD.p_retailprice;

        UPDATE partsupp
        SET ps_supplycost = ps_supplycost + delta_p_retailprice
        WHERE ps_partkey = delta_p_partkey;

        RETURN NEW;
    END
$q_2$ LANGUAGE 'plpgsql';


CREATE TRIGGER q_2
AFTER UPDATE ON part
FOR EACH ROW EXECUTE PROCEDURE q_2();


