import java.sql.*;

public class UpdateProcedures {
    void updateTrain(String old_train_id, String train_id, String train_name, Connection connection) {
        try {
            String sql = "{CALL update_train(?, ?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, old_train_id);
            cs.setString(2, train_id);
            cs.setString(3, train_name);
            cs.execute();
            System.out.println("Train updated successfully: " + train_id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    // void updateReleasedTrain() {
    // return;
    // }

    void updateStation(String old_station_id, String station_id, String station_name, Connection connection) {
        try {
            String sql = "{CALL update_station(?, ?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, old_station_id);
            cs.setString(2, station_id);
            cs.setString(3, station_name);
            cs.execute();
            System.out.println("Station updated successfully: " + station_id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    void updateRoute(String old_train_id, String old_station_id, String train_id, String station_id, Time arr_time,
            Time dep_time, Connection connection) {
        try {
            String sql = "{CALL update_route(?, ?, ?, ?, ?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, old_train_id);
            cs.setString(2, old_station_id);
            cs.setString(3, train_id);
            cs.setString(4, station_id);
            cs.setTime(5, arr_time);
            cs.setTime(6, dep_time);
            cs.execute();
            System.out.println("Route updated successfully:\nTrain: " + train_id + "\tStation: " + station_id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }
}