import java.sql.*;

public class App {
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost/railway_reservation_system";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            statement = connection.createStatement();
            System.out.println("Successfully connected to the database: " + DB_URL);

            // perform the required task here

            // RegistrationWrapper registrationWrapperObj = new RegistrationWrapper();
            // registrationWrapperObj.registerStations(connection);
            // registrationWrapperObj.registerRoutes(connection);
            // registrationWrapperObj.registerTrains(connection);

            TrainScheduleWrapper trainScheduleWrapperObj = new TrainScheduleWrapper();
            trainScheduleWrapperObj.init(connection);
            // trainScheduleWrapperObj.scheduleTrains(connection);
            // trainScheduleWrapperObj.relieveTrains(connection);

            // TrainSearchingProcedures trainSearchingProceduresObj = new
            // TrainSearchingProcedures();
            // trainSearchingProceduresObj.findTrains("00001", "00001", connection);

            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        System.out.println("Program executed successfully!");
    }
}