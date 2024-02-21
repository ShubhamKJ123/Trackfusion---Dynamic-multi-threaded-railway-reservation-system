import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sql.DataSource;

class DatabaseUtility {
    private static ComboPooledDataSource cpds = new ComboPooledDataSource();
    static {
        try {
            cpds.setDriverClass("org.postgresql.Driver");
            cpds.setJdbcUrl("jdbc:postgresql://localhost/railway_reservation_system");
            cpds.setUser("postgres");
            cpds.setPassword("postgres");
            cpds.setMinPoolSize(10);
            cpds.setAcquireIncrement(25);
            cpds.setMaxPoolSize(99);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static DataSource getDataSource() {
        return cpds;
    }

}

class QueryRunner implements Runnable {
    // declare a socket for client access
    protected Socket socketConnection;

    public QueryRunner(Socket clientSocket) {
        this.socketConnection = clientSocket;
    }

    public void run() {
        try {
            // reading data from client
            InputStreamReader inputStreamReaderObj = new InputStreamReader(socketConnection.getInputStream());
            BufferedReader bufferedReaderObj = new BufferedReader(inputStreamReaderObj);
            OutputStreamWriter outputStreamWriterObj = new OutputStreamWriter(socketConnection.getOutputStream());
            BufferedWriter bufferedWriterObj = new BufferedWriter(outputStreamWriterObj);
            PrintWriter printWriterObj = new PrintWriter(bufferedWriterObj, true);
            String clientCommand = "";
            String responseQuery = "";

            Connection connection = null;
            Statement statement = null;
            while (connection == null) {
                try {
                    connection = DatabaseUtility.getDataSource().getConnection();
                    statement = connection.createStatement();
                    String sql = null;
                    sql = "SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL REPEATABLE READ READ WRITE;";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    System.err.println("Retrying to get connection: " + e.getMessage());
                    try {
                        Thread.sleep(1);
                    } catch (Exception E) {
                        System.out.println(E.getMessage());
                    }
                    System.exit(0);
                }
            }
            while (true) {
                // read client query
                clientCommand = bufferedReaderObj.readLine();

                // tokenize here
                String tokens[] = clientCommand.split("\\s");
                if (tokens[0].equals("#")) {
                    String returnMsg = "Client connection terminated: "
                            + socketConnection.getRemoteSocketAddress().toString();
                    System.out.println(returnMsg);
                    inputStreamReaderObj.close();
                    bufferedReaderObj.close();
                    outputStreamWriterObj.close();
                    bufferedWriterObj.close();
                    printWriterObj.close();
                    socketConnection.close();
                    try {
                        connection.close();
                    } catch (Exception conCloseException) {
                        System.out.println(conCloseException.getMessage());
                    }
                    return;
                }

                int numberOfPassengers = Integer.parseInt(tokens[0]);
                String[] user_names = new String[numberOfPassengers];
                for (int i = 0; i < (numberOfPassengers - 1); i++) {
                    user_names[i] = tokens[i + 1].substring(0, tokens[i + 1].length() - 1);
                }
                user_names[numberOfPassengers - 1] = tokens[numberOfPassengers];

                String coach_type;
                if (tokens[numberOfPassengers + 3].equals("SL")) {
                    coach_type = "S";
                } else {
                    coach_type = "A";
                }

                // -------------------database code goes here----------------------

                ReservationProcedures reservationProceduresObj = new ReservationProcedures();
                String pnr_number = reservationProceduresObj.bookTicket(numberOfPassengers,
                        user_names,
                        tokens[numberOfPassengers + 1], Date.valueOf(tokens[numberOfPassengers + 2]),
                        coach_type, connection);

                if (pnr_number.equals("-1")) {
                    System.out.println(
                            "Ticket booking unsuccessful: " + socketConnection.getRemoteSocketAddress().toString());
                    responseQuery = "Ticket booking unsuccessful";
                } else {
                    System.out.println(
                            "Ticket booking successful: " + socketConnection.getRemoteSocketAddress().toString());
                    responseQuery = "Ticket booked successfully with PNR number: " + pnr_number;
                }

                // ----------------------------------------------------------------

                // sending data back to the client
                printWriterObj.println(responseQuery);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

// main class to control the program flow
public class Server {
    static int serverPort = 7006;
    static int numServerCores = 3 * Runtime.getRuntime().availableProcessors();
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost/railway_reservation_system";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

    public static void main(String[] args) throws IOException {
        // creating a thread pool
        ExecutorService executorServiceObj = Executors.newFixedThreadPool(numServerCores);

        // creating a server socket to listen for clients
        ServerSocket serverSocketObj = new ServerSocket(serverPort); // need to close the port
        Socket socketConnection = null;

        // always-on server
        while (true) {
            System.out.println("Listening on port: " + serverPort + "\nWaiting for clients...");
            socketConnection = serverSocketObj.accept();
            System.out.println("Accepted client: " + socketConnection.getRemoteSocketAddress().toString() + "\n");

            // create a runnable task
            Runnable runnableTask = new QueryRunner(socketConnection);

            // submit task for execution
            executorServiceObj.submit(runnableTask);
        }
    }
}