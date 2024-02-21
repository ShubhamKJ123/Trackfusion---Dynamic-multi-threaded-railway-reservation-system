import java.io.*;
import java.sql.*;

public class RegistrationWrapper {
    InsertProcedures insertProcedures = new InsertProcedures();
    DeleteProcedures deleteProceduresObj = new DeleteProcedures();

    void init(Connection connection) {
        deleteProceduresObj.formatDatabase(connection);
        registerTrains(connection);
        registerStations(connection);
        registerRoutes(connection);
        return;
    }

    void registerTrains(Connection connection) {
        try {
            String filePath = new File("").getAbsolutePath() + "/../../data/trains.txt";
            File file = new File(filePath);
            BufferedReader bufferedReaderObj = new BufferedReader(new FileReader(file));

            String line;
            while (!(line = bufferedReaderObj.readLine()).equals("#")) {
                String[] splittedLine = line.split("\\s+");
                String train_name = splittedLine[1];
                for (int i = 2; i < splittedLine.length; i++) {
                    train_name = train_name + " " + splittedLine[i];
                }

                insertProcedures.insertTrain(splittedLine[0], train_name, connection);
            }
            bufferedReaderObj.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    void registerStations(Connection connection) {
        try {
            String filePath = new File("").getAbsolutePath() + "/../../data/stations.txt";
            File file = new File(filePath);
            BufferedReader bufferedReaderObj = new BufferedReader(new FileReader(file));

            String line;
            while (!(line = bufferedReaderObj.readLine()).equals("#")) {
                String[] splittedLine = line.split("\\s+");
                String station_name = splittedLine[1];
                for (int i = 2; i < splittedLine.length; i++) {
                    station_name = station_name + " " + splittedLine[i];
                }

                insertProcedures.insertStation(splittedLine[0], station_name, connection);
            }
            bufferedReaderObj.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    void registerRoutes(Connection connection) {
        try {
            String filePath = new File("").getAbsolutePath() + "/../../data/routes.txt";
            File file = new File(filePath);
            BufferedReader bufferedReaderObj = new BufferedReader(new FileReader(file));

            String line;
            while (!(line = bufferedReaderObj.readLine()).equals("#")) {
                String[] splittedLine = line.split("\\s+");
                Time arr_time = null, dep_time = null;
                Date arr_date = null, dep_date = null;
                if (!splittedLine[2].equals("null")) {
                    arr_time = Time.valueOf(splittedLine[2]);
                }
                if (!splittedLine[4].equals("null")) {
                    dep_time = Time.valueOf(splittedLine[4]);
                }
                if (!splittedLine[3].equals("null")) {
                    arr_date = Date.valueOf(splittedLine[3]);
                }
                if (!splittedLine[5].equals("null")) {
                    dep_date = Date.valueOf(splittedLine[5]);
                }
                insertProcedures.insertRoute(splittedLine[0], splittedLine[1], arr_time, dep_time, arr_date, dep_date,
                        connection);
            }
            bufferedReaderObj.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }
}
