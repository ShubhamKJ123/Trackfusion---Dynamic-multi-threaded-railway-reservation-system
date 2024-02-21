import java.sql.*;

public class TrainSearchingProcedures {
    void findTrains(String from_station, String to_station, Connection connection) {
        try {
            String sql = "{CALL find_trains(?, ?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, from_station);
            cs.setString(2, to_station);
            ResultSet resultSet = cs.executeQuery();

            int cnt = 1;
            while (resultSet.next()) {
                System.out.println("Option " + cnt++ + ":");
                System.out.print("Train 1: " + resultSet.getString(10) + "\t\t");
                System.out.print("Station 1: " + resultSet.getString(1) + "\t\t");
                System.out.print("Station 1 Departure Time: " + resultSet.getTime(3) + "\t\t");
                System.out.print("Station 1 Departure Date: " + resultSet.getDate(12) + "\t\t");
                System.out.print("Station 2: " + resultSet.getString(4) + "\t\t");
                System.out.print("Station 2 Arrival Time: " + resultSet.getTime(5) + "\t\t");
                System.out.print("Station 2 Arrival Date: " + resultSet.getDate(13) + "\t\t\n");

                if (resultSet.getString(11) != null) {
                    System.out.print("Train 2: " + resultSet.getString(11) + "\t\t");
                    System.out.print("Station 2: " + resultSet.getString(4) + "\t\t");
                    System.out.print("Station 2 Departure Time: " + resultSet.getTime(6) + "\t\t");
                    System.out.print("Station 2 Departure Date: " + resultSet.getDate(14) + "\t\t");
                    System.out.print("Station 3: " + resultSet.getString(7) + "\t\t");
                    System.out.print("Station 3 Arrival Time: " + resultSet.getTime(8) + "\t\t");
                    System.out.print("Station 3 Arrival Date: " + resultSet.getDate(15) + "\t\t\n");
                }
                System.out.println("\n\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}