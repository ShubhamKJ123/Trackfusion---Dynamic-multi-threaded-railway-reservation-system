CREATE OR REPLACE FUNCTION trains_insert_trigger_function()
RETURNS TRIGGER AS $$
    BEGIN
        IF LENGTH(new.train_id) <> 5 THEN
            RAISE EXCEPTION 'Invalid "train_id" length: "%"', new.train_id
            USING HINT = '"train_id" length should be exactly equal to five';
        ELSIF LENGTH(new.train_name) = 0 THEN
            RAISE EXCEPTION 'Invalid "train_name" length: "%"', new.train_name
            USING HINT = '"train_name" length should be greater than zero';
        ELSIF (SELECT new.train_id ~ '^[0-9]+$') = false THEN
            RAISE EXCEPTION 'Invalid "train_id" type: "%"', new.train_id
            USING HINT = '"train_id" must only contain digits';
        END IF;

        RETURN new;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trains_insert_trigger
BEFORE INSERT OR UPDATE ON trains
FOR EACH ROW EXECUTE PROCEDURE trains_insert_trigger_function();



CREATE OR REPLACE FUNCTION released_trains_insert_trigger_function()
RETURNS TRIGGER AS $$
    BEGIN
        IF new.date_of_journey < (SELECT CURRENT_DATE) THEN
            RAISE EXCEPTION 'Invalid "date_of_journey": %', new.date_of_journey
            USING HINT = '"date_of_journey" cannot be prior to the current date';
        END IF;

        RETURN new; 
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER released_trains_insert_trigger
BEFORE INSERT OR UPDATE ON released_trains
FOR EACH ROW EXECUTE PROCEDURE released_trains_insert_trigger_function();



CREATE OR REPLACE FUNCTION reservations_insert_trigger_function()
RETURNS TRIGGER AS $$
    BEGIN
        IF LENGTH(new.pnr_number) <> 20 THEN
            RAISE EXCEPTION 'Invalid "pnr_number" length: "%"', new.pnr_number
            USING HINT = '"pnr_number" should have exactly eighteen digits';
        ELSIF (SELECT new.pnr_number ~ '^[0-9]+$') = false THEN
            RAISE EXCEPTION 'Invalid "pnr_number" type: "%"', new.pnr_number
            USING HINT = '"pnr_number" must only contain digits';
        ELSIF (SELECT new.coach_number ~ '^[AS][1-9][0-9]*$') = false THEN
            RAISE EXCEPTION 'Invalid "coach_number" type: "%"', new.coach_number
            USING HINT = '"coach_number" should be of the form "AXX" or "SXX"';
        ELSIF (SELECT new.coach_number ~ '^A[1-9][0-9]*$') = true AND (new.seat_number > 18 OR new.seat_number <= 0) THEN
            RAISE EXCEPTION 'Invalid "seat_number": "%"', new.seat_number
            USING HINT = '"seat_number" must be less than or equal to eighteen for AC coaches';
        ELSIF (SELECT new.coach_number ~ '^S[1-9][0-9]*$') = true AND (new.seat_number > 24 OR new.seat_number <=0) THEN
            RAISE EXCEPTION 'Invalid "seat_number": "%"', new.seat_number
            USING HINT = '"seat_number" must be less than or equal to twenty-four for Sleeper coaches';
        ELSIF (SELECT new.coach_number ~ '^A[1-9][0-9]*$') = true AND NOT (new.seat_type = 'LB' OR new.seat_type = 'UB' OR new.seat_type = 'SL' OR new.seat_type = 'SU') THEN
            RAISE EXCEPTION 'Invalid "seat_type": "%"', new.seat_type
            USING HINT = '"seat_type" must be either "UB", "LB", "SU" or "SL" for AC coaches';
        ELSIF (SELECT new.coach_number ~ '^S[1-9][0-9]*$') = true AND NOT (new.seat_type = 'LB' OR new.seat_type = 'UB' OR new.seat_type = 'SL' OR new.seat_type = 'SU' OR new.seat_type = 'MB') THEN
            RAISE EXCEPTION 'Invalid "seat_type": "%"', new.seat_type
            USING HINT = '"seat_type" must be either "UB", "MB", "LB", "SU", or "SL" for sleeper coaches';
        END IF;

        RETURN new;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER reservations_insert_trigger
BEFORE INSERT OR UPDATE ON reservations
FOR EACH ROW EXECUTE PROCEDURE reservations_insert_trigger_function();



CREATE OR REPLACE FUNCTION stations_insert_trigger_function()
RETURNS TRIGGER AS $$
    BEGIN
        IF LENGTH(new.station_id) <> 5 THEN
            RAISE EXCEPTION 'Invalid "station_id" length: "%"', new.station_id
            USING HINT = '"station_id" length should be exactly equal to five';
        ELSIF LENGTH(new.station_name) = 0 THEN
            RAISE EXCEPTION 'Invalid "station_name" length: "%"', new.station_name
            USING HINT = '"station_name" length should be greater than zero';
        ELSIF (SELECT new.station_id ~ '^[0-9]+$') = false THEN
            RAISE EXCEPTION 'Invalid "station_id" type: "%"', new.station_id
            USING HINT = '"station_id" must only contain digits';
        END IF;

        RETURN new;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER stations_insert_trigger
BEFORE INSERT OR UPDATE ON stations
FOR EACH ROW EXECUTE PROCEDURE stations_insert_trigger_function();



CREATE OR REPLACE FUNCTION routes_insert_trigger_function()
RETURNS TRIGGER AS $$
    BEGIN
        IF new.arr_time <> NULL AND new.dep_time <> NULL AND new.arr_date <> NULL AND new.dep_date <> NULL AND ((new.arr_date = new.dep_date AND new.arr_time > new.dep_time) OR new.arr_date > new.dep_date) THEN
            RAISE EXCEPTION 'Invalid arrival and/or departure time'
            USING HINT = 'Arrival should prior be to the departure';
        END IF;
        RETURN new;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER routes_insert_trigger
BEFORE INSERT OR UPDATE ON routes
FOR EACH ROW EXECUTE PROCEDURE routes_insert_trigger_function();