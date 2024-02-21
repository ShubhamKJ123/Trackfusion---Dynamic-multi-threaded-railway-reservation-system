import java.sql.*;

public class InsertProcedures {
    void insertTrain(String train_id, String train_name, Connection connection) {
        try {
            String sql = "{CALL insert_train(?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, train_id);
            cs.setString(2, train_name);
            cs.execute();
            System.out.println("Train added successfully: " + train_id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    void releaseTrain(String train_id, Date date_of_journey, int number_of_ac_coaches, int number_of_sleeper_coaches,
            Connection connection) {
        try {
            String sql = "{CALL release_train(?, ?, ?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, train_id);
            cs.setDate(2, date_of_journey);
            cs.setInt(3, number_of_ac_coaches);
            cs.setInt(4, number_of_sleeper_coaches);
            cs.execute();
            System.out.println("Train released successfully:\nTrain:  " + train_id + "\tDate: " + date_of_journey);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    void insertReservation(String user_name, String train_id, Date date_of_journey, String pnr_number,
            String coach_number, int seat_number, String seat_type, Connection connection) {
        try {
            String sql = "{CALL insert_reservation(?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, user_name);
            cs.setString(2, train_id);
            cs.setDate(3, date_of_journey);
            cs.setString(4, pnr_number);
            cs.setString(5, coach_number);
            cs.setInt(6, seat_number);
            cs.setString(7, seat_type);
            cs.execute();
            System.out
                    .println("Reservation added successfully:\nUser: " + user_name + "\tTrain: " + train_id + "\tDate: "
                            + date_of_journey);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    void insertStation(String station_id, String station_name, Connection connection) {
        try {
            String sql = "{CALL insert_station(?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, station_id);
            cs.setString(2, station_name);
            cs.execute();
            System.out.println("Station added successfully: " + station_id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    void insertRoute(String train_id, String station_id, Time arr_time, Time dep_time, Date arr_date, Date dep_date,
            Connection connection) {
        try {
            String sql = "{CALL insert_route(?, ?, ?, ?, ?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, train_id);
            cs.setString(2, station_id);
            cs.setTime(3, arr_time);
            cs.setTime(4, dep_time);
            cs.setDate(5, arr_date);
            cs.setDate(6, dep_date);
            cs.execute();
            System.out.println("Route added successfully:\nTrain: " + train_id + "\tStation: " + station_id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }
}