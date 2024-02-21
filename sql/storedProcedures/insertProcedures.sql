CREATE OR REPLACE FUNCTION insert_train (
    IN train_id VARCHAR(5),
    IN train_name VARCHAR(30)
)
RETURNS VOID AS $$
    BEGIN
        INSERT INTO trains
        VALUES (train_id, train_name);
    END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION release_train (
    trainid VARCHAR(5),
    date_of_journey DATE,
    number_of_ac_coaches INT,
    number_of_sleeper_coaches INT
)
RETURNS VOID AS $$
    BEGIN
        INSERT INTO released_trains VALUES(trainid, date_of_journey);
        
        EXECUTE 'CREATE TABLE IF NOT EXISTS ' ||  't' || TO_CHAR(date_of_journey, 'yyyymmdd') || trainid || ' ( number_of_ac_coaches INT NOT NULL, number_of_sleeper_coaches INT NOT NULL, ac_seats_left INT NOT NULL, sleeper_seats_left INT NOT NULL )';
        EXECUTE FORMAT('INSERT INTO %s VALUES (%s, %s, %s, %s)', CONCAT(CONCAT('t', to_char(date_of_journey,'yyyymmdd')), trainid), number_of_ac_coaches, number_of_sleeper_coaches, number_of_ac_coaches * 18, number_of_sleeper_coaches * 24); 
    END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION insert_reservation (
    IN user_name VARCHAR(30),
    IN train_id VARCHAR(5),
    IN date_of_journey DATE,
    IN pnr_number VARCHAR(20),
    IN coach_number VARCHAR(5),
    IN seat_number INT,
    IN seat_type VARCHAR(2)
)
RETURNS VOID AS $$
    BEGIN
        INSERT INTO reservations
        VALUES (user_name, train_id, date_of_journey, pnr_number, coach_number, seat_number, seat_type);
    END;
$$ LANGUAGE plpgsql;

 

CREATE OR REPLACE FUNCTION insert_station (
    IN station_id VARCHAR(5),
    IN station_name VARCHAR(30)
)
RETURNS VOID AS $$
    BEGIN
        INSERT INTO stations
        VALUES (station_id, station_name);
    END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION insert_route (
    IN train_id VARCHAR(5),
    IN station_id VARCHAR(5),
    IN arr_time TIME,
    IN dep_time TIME,
    IN arr_date DATE,
    IN dep_date DATE
)
RETURNS VOID AS $$
    BEGIN
        INSERT INTO routes
        VALUES (train_id, station_id, arr_time, dep_time, arr_date, dep_date);
    END;
$$ LANGUAGE plpgsql;