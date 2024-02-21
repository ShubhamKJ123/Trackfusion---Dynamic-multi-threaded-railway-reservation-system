CREATE TABLE IF NOT EXISTS trains (
    train_id VARCHAR(5) NOT NULL,
    train_name VARCHAR(30) NOT NULL,
    PRIMARY KEY (train_id)
);



CREATE TABLE IF NOT EXISTS released_trains (
    train_id VARCHAR(5),
    date_of_journey DATE,
    PRIMARY KEY (train_id, date_of_journey),
    FOREIGN KEY (train_id) REFERENCES trains (train_id)
);



-- Schema for the tables of indivisual trains
-- CREATE TABLE IF NOT EXISTS YYYYMMDDTTTTT ( 
--     number_of_ac_coaches INT NOT NULL, 
--     number_of_sleeper_coaches INT NOT NULL, 
--     ac_seats_left INT NOT NULL, 
--     sleeper_seats_left INT NOT NULL 
-- );



CREATE TABLE IF NOT EXISTS train_log (
    train_id VARCHAR(5) NOT NULL,
    date_of_journey DATE NOT NULL,
    number_of_ac_coaches INT NOT NULL, 
    number_of_sleeper_coaches INT NOT NULL, 
    ac_seats_left INT NOT NULL, 
    sleeper_seats_left INT NOT NULL,
  	PRIMARY KEY (train_id, date_of_journey)
); 



CREATE TABLE IF NOT EXISTS reservations (
    user_name VARCHAR(30) NOT NULL,
    train_id VARCHAR(5) NOT NULL,
    date_of_journey DATE NOT NULL,
    pnr_number VARCHAR(20) NOT NULL,
    coach_number VARCHAR(5) NOT NULL,
    seat_number INT NOT NULL,
    seat_type VARCHAR(2) NOT NULL,
	FOREIGN KEY (train_id) REFERENCES trains (train_id)
);



CREATE TABLE IF NOT EXISTS stations (
  	station_id VARCHAR(5) NOT NULL,
  	station_name varchar(30) NOT NULL,
  	PRIMARY KEY (station_id)
);



CREATE TABLE IF NOT EXISTS routes (
  	train_id VARCHAR(5) NOT NULL,
  	station_id VARCHAR(5) NOT NULL,
	arr_time TIME,
	dep_time TIME,
    arr_date DATE,
    dep_date DATE,
	FOREIGN KEY (train_id) REFERENCES trains (train_id),
	FOREIGN KEY (station_id) REFERENCES stations (station_id)
);