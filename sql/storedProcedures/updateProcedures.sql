CREATE OR REPLACE FUNCTION update_train (
    IN old_train_id VARCHAR(5),
    IN new_train_id VARCHAR(5),
    IN new_train_name VARCHAR(30)
)
RETURNS VOID AS $$
    BEGIN
        UPDATE trains
        SET train_id = new_train_id, train_name = new_train_name
        WHERE train_id = old_train_id;
    END;
$$ LANGUAGE plpgsql;



-- CREATE OR REPLACE FUNCTION update_released_train (

-- )
-- RETURN VOID AS $$
--     BEGIN 

--     END;
-- $$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION update_station (
    IN old_station_id VARCHAR(5),
    IN new_station_id VARCHAR(5),
    IN new_station_name VARCHAR(30)
)
RETURNS VOID AS $$
    BEGIN
        UPDATE stations
        SET stations_id = new_stations_id, station_name = new_station_name
        WHERE station_id = old_station_id;
    END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION update_route (
    IN old_train_id VARCHAR(5),
    IN old_station_id VARCHAR(5),
    IN new_train_id VARCHAR(5),
    IN new_station_id VARCHAR(30),
    IN new_arr_time TIME,
    IN new_dep_time TIME,
    IN new_arr_date DATE,
    IN new_dep_date DATE
)
RETURNS VOID AS $$
    BEGIN
        UPDATE routes
        SET train_id = new_train_id, station_id = new_station_id, arr_time = new_arr_time, dep_time = new_dep_time, arr_date = new_arr_date, dep_date = new_dep_date
        WHERE train_id = old_train_id AND station_id = old_station_id;
    END;
$$ LANGUAGE plpgsql; 