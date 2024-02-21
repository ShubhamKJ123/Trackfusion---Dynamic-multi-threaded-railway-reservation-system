import java.io.*;
import java.sql.*;

public class TrainScheduleWrapper {
    InsertProcedures insertProceduresObj = new InsertProcedures();
    DeleteProcedures deleteProceduresObj = new DeleteProcedures();
    RegistrationWrapper registrationWrapperObj = new RegistrationWrapper();

    void init(Connection connection) {
        registrationWrapperObj.init(connection);
        scheduleTrains(connection);
        return;
    }

    void scheduleTrains(Connection connection) {
        try {
            String filePath = new File("").getAbsolutePath() + "/../../data/trainSchedule.txt";
            File file = new File(filePath);
            BufferedReader bufferedReaderObj = new BufferedReader(new FileReader(file));

            String line;
            while (!(line = bufferedReaderObj.readLine()).equals("#")) {
                String[] splittedLine = line.split("\\s+");
                insertProceduresObj.releaseTrain(splittedLine[0], Date.valueOf(splittedLine[1]),
                        Integer.parseInt(splittedLine[2]), Integer.parseInt(splittedLine[3]), connection);
            }
            bufferedReaderObj.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }

    void relieveTrains(Connection connection) {
        try {
            String filePath = new File("").getAbsolutePath() + "/../../data/departedTrains.txt";
            File file = new File(filePath);
            BufferedReader bufferedReaderObj = new BufferedReader(new FileReader(file));

            String line;
            while (!(line = bufferedReaderObj.readLine()).equals("#")) {
                String[] splittedLine = line.split("\\s+");
                if (splittedLine[0].equals("ALL")) {
                    String sql = "SELECT * FROM released_trains";
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ResultSet resultSet = ps.executeQuery();
                    while (resultSet.next()) {
                        deleteProceduresObj.relieveTrain(resultSet.getString("train_id"),
                                resultSet.getDate("date_of_journey"), connection);
                    }
                    break;
                }

                deleteProceduresObj.relieveTrain(splittedLine[0], Date.valueOf(splittedLine[1]), connection);
            }
            bufferedReaderObj.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }
}
