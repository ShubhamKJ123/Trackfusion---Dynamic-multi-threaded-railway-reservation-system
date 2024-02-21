import java.sql.*;

public class DeleteProcedures {
    // void deleteTrain() {
    // return;
    // }

    void relieveTrain(String train_id, Date date_of_journey, Connection connection) {
        try {
            String sql = "{CALL relieve_train(?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, train_id);
            cs.setDate(2, date_of_journey);
            cs.execute();
            System.out.println("Train removed successfully: " + train_id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    // void deleteReservation() {
    // return;
    // }

    // void deleteStation() {
    // return;
    // }

    // void deleteRoute() {
    // return;
    // }

    void formatDatabase(Connection connection) {
        try {
            String sql = "{CALL format_database()}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.execute();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }
}