import java.io.*;
import java.sql.*;

public class DatabaseInit {
    static void executeSql(File file, Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String sql = "";
            try (FileReader fileReaderObj = new FileReader(file)) {
                char cur;
                while ((cur = (char) fileReaderObj.read()) != (char) -1) {
                    sql = sql + cur;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            statement.executeUpdate(sql);
            statement.close();
        } catch (Exception e) {
            System.out.println(file.getName() + ": " + e.getMessage());
        }
    }

    static void init(File[] files, Connection connection) {
        for (File curFile : files) {
            if (curFile.isDirectory()) {
                init(curFile.listFiles(), connection);
            } else {
                executeSql(curFile, connection);
                System.out.println("File completed: " + curFile.getName());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String filePath = new File("").getAbsolutePath() + "/../../sql";
        File[] files = new File(filePath).listFiles();

        final String DB_URL_1 = "jdbc:postgresql://localhost/postgres";
        final String DB_URL_2 = "jdbc:postgresql://localhost/railway_reservation_system";
        final String USER = "postgres";
        final String PASSWORD = "postgres";

        try {
            Connection connection_1 = DriverManager.getConnection(DB_URL_1, USER, PASSWORD);
            Statement statement = connection_1.createStatement();
            System.out.println("Successfully connected to the database: " + DB_URL_1);

            String sql = "DROP DATABASE IF EXISTS railway_reservation_system;";
            statement.executeUpdate(sql);
            sql = "CREATE DATABASE railway_reservation_system;";
            statement.executeUpdate(sql);
            connection_1.close();
            statement.close();

            Connection connection_2 = DriverManager.getConnection(DB_URL_2, USER, PASSWORD);
            System.out.println("Successfully connected to the database: " + DB_URL_2);
            filePath = new File("").getAbsolutePath() + "/../../sql/queries/createTables.sql";
            File file = new File(filePath);
            executeSql(file, connection_2);

            init(files, connection_2);
            System.out.println("SQL files executed successfully!");

            connection_2.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
