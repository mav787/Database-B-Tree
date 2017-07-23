CREATE FUNCTION q_7() RETURNS TRIGGER AS $q_7$
    DECLARE
           new_n_regionkey    integer;
           old_n_regionkey    integer;
           new_s_suppkey      integer;
    BEGIN
        new_s_suppkey = NEW.s_suppkey;

        old_n_regionkey = 
        (SELECT n_regionkey 
        FROM nation 
        WHERE n_nationkey = OLD.s_nationkey);

        new_n_regionkey = 
        (SELECT n_regionkey
        FROM nation
        WHERE n_nationkey = NEW.s_nationkey);
        
        IF old_n_regionkey = 1 AND new_n_regionkey = 2
        THEN
        UPDATE partsupp
        SET ps_supplycost = ps_supplycost * 0.8
        WHERE ps_suppkey = new_s_suppkey;
        END IF;

        IF old_n_regionkey = 1 AND new_n_regionkey = 3
        THEN
        UPDATE partsupp
        SET ps_supplycost = ps_supplycost * 1.05
        WHERE ps_suppkey = new_s_suppkey;
        END IF;

        IF old_n_regionkey = 2 AND new_n_regionkey = 1
        THEN
        UPDATE partsupp
        SET ps_supplycost = ps_supplycost * 1.2
        WHERE ps_suppkey = new_s_suppkey;
        END IF;

        IF old_n_regionkey = 2 AND new_n_regionkey = 3
        THEN
        UPDATE partsupp
        SET ps_supplycost = ps_supplycost * 1.1
        WHERE ps_suppkey = new_s_suppkey;
        END IF;

        IF old_n_regionkey = 3 AND new_n_regionkey = 1
        THEN
        UPDATE partsupp
        SET ps_supplycost = ps_supplycost * 0.95
        WHERE ps_suppkey = new_s_suppkey;
        END IF;

        IF old_n_regionkey = 3 AND new_n_regionkey = 2
        THEN
        UPDATE partsupp
        SET ps_supplycost = ps_supplycost * 0.9
        WHERE ps_suppkey = new_s_suppkey;
        END IF;
        
        RETURN NEW;
    END
$q_7$ LANGUAGE 'plpgsql';


CREATE TRIGGER q_7
AFTER UPDATE ON supplier
FOR EACH ROW EXECUTE PROCEDURE q_7();

