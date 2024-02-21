-- CREATE OR REPLACE FUNCTION delete_train (

-- )
-- RETURN VOID AS $$ 
--     BEGIN

--     END;
-- $$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION relieve_train ( 
    IN trainid VARCHAR(5),
    IN dateofjourney DATE
)
RETURNS VOID AS $$
    DECLARE
        table_name VARCHAR(14);
        row RECORD;
    BEGIN
        table_name := CONCAT(CONCAT('t', TO_CHAR(dateofjourney, 'yyyymmdd')), trainid);
        FOR row IN EXECUTE FORMAT('SELECT * FROM %s', table_name) LOOP
            EXECUTE FORMAT('INSERT INTO train_log VALUES (''%s'','' %s'', %s, %s, %s, %s)', trainid, dateofjourney, row.number_of_ac_coaches, row.number_of_sleeper_coaches, row.ac_seats_left, row.sleeper_seats_left);
        END LOOP;
        
        DELETE FROM released_trains WHERE train_id = trainid AND date_of_journey = dateofjourney;
        EXECUTE FORMAT('DROP TABLE %s', table_name);
    END;
$$ LANGUAGE plpgsql;



-- CREATE OR REPLACE FUNCTION delete_reservation (

-- )
-- RETURN VOID AS $$ 
--     BEGIN

--     END;
-- $$ LANGUAGE plpgsql;



-- CREATE OR REPLACE FUNCTION delete_station (

-- )
-- RETURN VOID AS $$ 
--     BEGIN

--     END;
-- $$ LANGUAGE plpgsql;



-- CREATE OR REPLACE FUNCTION delete_route (

-- )
-- RETURN VOID AS $$ 
--     BEGIN

--     END;
-- $$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION format_database ()
RETURNS VOID AS $$
    DECLARE 
        table_name VARCHAR(14);
        row RECORD;
    BEGIN
        FOR row IN (SELECT * FROM released_trains) LOOP
            table_name := CONCAT(CONCAT('t', TO_CHAR(row.date_of_journey, 'yyyymmdd')), row.train_id);
            EXECUTE FORMAT('DROP TABLE %s', table_name);
        END LOOP;

        DELETE FROM routes;
        DELETE FROM reservations;
        DELETE FROM released_trains;
        DELETE FROM trains;
        DELETE FROM stations;
        DELETE FROM train_log;
    END;
$$ LANGUAGE plpgsql;