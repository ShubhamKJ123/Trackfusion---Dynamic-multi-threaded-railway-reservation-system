CREATE OR REPLACE FUNCTION find_trains (
    from_station VARCHAR(5),
    to_station VARCHAR(5)
)
RETURNS TABLE (
    station_1_id VARCHAR(5),
    station_1_arr_time TIME,
    station_1_dep_time TIME,
    station_2_id VARCHAR(5),
    station_2_arr_time TIME,
    station_2_dep_time TIME,
    station_3_id VARCHAR(5),
    station_3_arr_time TIME,
    station_3_dep_time TIME,
    train_1_id VARCHAR(5),
    train_2_id VARCHAR(5),
    station_1_dep_date DATE,
    station_2_arr_date DATE,
    station_2_dep_date DATE,
    station_3_arr_date DATE
) AS $$
    DECLARE
        trains_from RECORD;
        trains_to RECORD;
        intermediate_stations_from RECORD;
        intermediate_stations_to RECORD;
        all_trains RECORD;

    BEGIN
        FOR trains_from IN (SELECT * FROM routes WHERE station_id = from_station) LOOP
            FOR trains_to IN (SELECT * FROM routes WHERE station_id = to_station) LOOP
                IF trains_from.train_id = trains_to.train_id AND ((trains_from.dep_date < trains_to.arr_date) OR (trains_from.dep_date = trains_to.arr_date AND trains_from.dep_time < trains_to.arr_time)) THEN
                    station_1_id := from_station;
                    station_1_arr_time := trains_from.arr_time;
                    station_1_dep_time := trains_from.dep_time;
                    station_1_dep_date := trains_from.dep_date;
                    station_2_id := to_station;
                    station_2_arr_time := trains_to.arr_time;
                    station_2_arr_date := trains_to.arr_date;
                    station_2_dep_time := trains_to.dep_time;
                    station_3_id := null;
                    station_3_arr_time := null;
                    station_3_dep_time := null;
                    train_1_id := trains_from.train_id;
                    train_2_id := null;
                    RETURN NEXT;
                END IF;

                FOR intermediate_stations_from IN (SELECT * FROM routes WHERE train_id = trains_from.train_id) LOOP
                    FOR intermediate_stations_to IN (SELECT * FROM routes WHERE train_id = trains_to.train_id) LOOP
                        IF intermediate_stations_from.station_id = intermediate_stations_to.station_id AND intermediate_stations_from.train_id <> intermediate_stations_to.train_id AND ((trains_from.dep_date < intermediate_stations_from.arr_date) OR (trains_from.dep_date = intermediate_stations_from.arr_date AND trains_from.dep_time < intermediate_stations_from.arr_time)) AND ((intermediate_stations_from.arr_date < intermediate_stations_to.dep_date) OR (intermediate_stations_from.arr_date = intermediate_stations_to.dep_date AND intermediate_stations_from.arr_time < intermediate_stations_to.dep_time)) AND ((intermediate_stations_to.dep_date < trains_to.arr_date) OR (intermediate_stations_to.dep_date = trains_to.arr_date AND intermediate_stations_to.dep_time < trains_to.arr_time)) THEN
                            station_1_id := from_station;
                            station_1_arr_time := trains_from.arr_time;
                            station_1_dep_time := trains_from.dep_time;
                            station_1_dep_date := trains_from.dep_date;
                            station_2_id := intermediate_stations_from.station_id;
                            station_2_arr_time := intermediate_stations_from.arr_time;
                            station_2_dep_time := intermediate_stations_to.dep_time;
                            station_2_arr_date := intermediate_stations_from.arr_date;
                            station_2_dep_date := intermediate_stations_to.dep_date;
                            station_3_id := to_station;
                            station_3_arr_time := trains_to.arr_time;
                            station_3_dep_time := trains_to.dep_time;
                            station_3_arr_date := trains_to.arr_date;
                            train_1_id := trains_from.train_id;
                            train_2_id := trains_to.train_id;
                            RETURN NEXT;
                        END IF;
                    END LOOP;
                END LOOP;
            END LOOP;
        END LOOP; 
    END;
$$ LANGUAGE plpgsql;