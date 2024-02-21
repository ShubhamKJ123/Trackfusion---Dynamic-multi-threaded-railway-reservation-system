CREATE OR REPLACE FUNCTION check_availability (
    trainid VARCHAR(5),
    dateofjourney DATE,
    coach_type VARCHAR(1),
    number_of_seats INT
)
RETURNS INT AS $$
    DECLARE
        table_name VARCHAR(14);
        ac_seats INT;
        sleeper_seats INT;
        row RECORD;
    BEGIN
        IF (SELECT NOT EXISTS (SELECT * FROM released_trains WHERE train_id = trainid AND date_of_journey = dateofjourney)) THEN
            RAISE EXCEPTION 'Invalid "train_id" - "date_of_journey" combination'
            USING HINT = 'Either the given train does not ply on the given date, or the date has already passed';
        ELSIF coach_type <> 'A' AND coach_type <> 'S' THEN
            RAISE EXCEPTION 'Invalid "coach_type"'
            USING HINT = '"coach_type" must be either "A" or "S"';
        END IF;

        table_name := CONCAT(CONCAT('t', TO_CHAR(dateofjourney, 'yyyymmdd')), trainid);
        FOR row IN EXECUTE FORMAT('SELECT * FROM %s', table_name) LOOP
            ac_seats := row.ac_seats_left;
            sleeper_seats := row.sleeper_seats_left;
        END LOOP;
        
        IF coach_type = 'A' AND ac_seats < number_of_seats THEN
            RAISE EXCEPTION '"%" seats are not available in "AC" coaches', number_of_seats;
        ELSIF coach_type = 'S' AND sleeper_seats < number_of_seats THEN
            RAISE EXCEPTION '"%" seats are not available in "Sleeper" coaches', number_of_seats;
        END IF;

        IF coach_type = 'A' THEN
            RETURN ac_seats;
        ELSE
            RETURN sleeper_seats;
        END IF;
    END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION remove_seats (
    train_id VARCHAR(5),
    date_of_journey DATE,
    coach_type VARCHAR(1),
    number_of_seats INT
)
RETURNS VOID AS $$
    DECLARE
        table_name VARCHAR(14);
        ac_seats INT;
        sleeper_seats INT;
        row RECORD;
    BEGIN
        table_name := CONCAT(CONCAT('t', TO_CHAR(date_of_journey, 'yyyymmdd')), train_id);
        FOR row IN EXECUTE FORMAT('SELECT * FROM %s', table_name) LOOP
            ac_seats := row.ac_seats_left;
            sleeper_seats := row.sleeper_seats_left;
        END LOOP;

        IF coach_type = 'A' THEN
            EXECUTE FORMAT('UPDATE %s SET ac_seats_left = %s - %s', table_name, ac_seats, number_of_seats);
        ELSE 
            EXECUTE FORMAT('UPDATE %s SET sleeper_seats_left = %s - %s', table_name, sleeper_seats, number_of_seats);
        END IF;
    END;     
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION make_reservation (
    user_name VARCHAR(30),
    train_id VARCHAR(5),
    date_of_journey DATE,
    pnr_number VARCHAR(20),
    coach_type VARCHAR(1),
    number_of_seats_left INT
)
RETURNS VOID AS $$
    DECLARE
        table_name VARCHAR(14);
        coach_number VARCHAR(5);
        seat_type VARCHAR(2);
        temp_seat_type INT;
        seat_number INT;
        number_of_coaches INT;
        row RECORD;
    BEGIN
        table_name := CONCAT(CONCAT('t', TO_CHAR(date_of_journey, 'yyyymmdd')), train_id);
        IF coach_type = 'A' THEN
            FOR row IN EXECUTE FORMAT('SELECT * FROM %s', table_name) LOOP
                number_of_coaches := row.number_of_ac_coaches;
            END LOOP;      
            coach_number := CONCAT('A', CAST(number_of_coaches + 1 - (number_of_seats_left + 17)/18 AS VARCHAR(4)));

            IF MOD(number_of_seats_left, 18) = 0 THEN
                seat_number := 1;
            ELSE
                seat_number := 18 - MOD(number_of_seats_left, 18) + 1;
            END IF;
            temp_seat_type := MOD(seat_number, 6);

            IF temp_seat_type = 1 OR temp_seat_type = 2 THEN
                seat_type := 'LB';
            ELSIF temp_seat_type = 3 OR temp_seat_type = 4 THEN
                seat_type := 'UB';
            ELSIF temp_seat_type = 5 THEN
                seat_type := 'SL';
            ELSE
                seat_type := 'SU';
            END IF;
        ELSE
            FOR row IN EXECUTE FORMAT('SELECT * FROM %s', table_name) LOOP
                number_of_coaches := row.number_of_sleeper_coaches;
            END LOOP;        
            coach_number := CONCAT('S', CAST(number_of_coaches + 1 - (number_of_seats_left + 23)/24 AS VARCHAR(4)));
            
            IF MOD(number_of_seats_left, 24) = 0 THEN
                seat_number := 1;
            ELSE
                seat_number := 24 - MOD(number_of_seats_left, 24) + 1;
            END IF;
            temp_seat_type := MOD(seat_number, 8);

            IF temp_seat_type = 1 OR temp_seat_type = 4 THEN
                seat_type := 'LB';
            ELSIF temp_seat_type = 2 OR temp_seat_type = 5 THEN
                seat_type := 'MB';
            ELSIF temp_seat_type = 3 OR temp_seat_type = 6 THEN
                seat_type := 'UB';
            ELSIF temp_seat_type = 7 THEN
                seat_type := 'SL';
            ELSE
                seat_type := 'SU';
            END IF;
        END IF;

        PERFORM insert_reservation(user_name, train_id, date_of_journey, pnr_number, coach_number, seat_number, seat_type);
    END;
$$ LANGUAGE plpgsql;

 

CREATE OR REPLACE FUNCTION generate_ticket (
    pnrnumber VARCHAR(20)
)
RETURNS TABLE (
    user_name VARCHAR(30),
    train_id VARCHAR(5),
    train_name VARCHAR(30),
    date_of_journey DATE,
    coach_number VARCHAR(5),
    seat_number INT,
    seat_type VARCHAR(2)
) AS $$
    BEGIN
        IF (SELECT NOT EXISTS (SELECT * FROM reservations WHERE pnr_number = pnrnumber)) THEN
            RAISE EXCEPTION 'Invalid "pnr_number": "%"', pnrnumber
            USING HINT = 'Given "pnr_number" does not exist';
        END IF;

        RETURN QUERY (SELECT R.user_name, R.train_id, T.train_name, R.date_of_journey, R.coach_number, R.seat_number, R.seat_type 
        FROM reservations R, trains T 
        WHERE R.pnr_number = pnrnumber and R.train_id = T.train_id);
    END;
$$ LANGUAGE plpgsql;