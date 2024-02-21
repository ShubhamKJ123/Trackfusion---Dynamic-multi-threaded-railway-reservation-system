import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class sendQuery implements Runnable {
    File file;
    int socketPort = 7006;

    public sendQuery(File fileLocal) {
        file = fileLocal;
    }

    public void run() {
        boolean flag = true;
        while (flag) {
            try {
                // creating a client socket to send query requests
                Socket socketConnection = new Socket("localhost", socketPort);

                // initialising the input/output file-streams and buffers for server contact
                try {
                    OutputStreamWriter outputStreamWriterObj = new OutputStreamWriter(
                            socketConnection.getOutputStream());
                    BufferedWriter bufferedWriterObj = new BufferedWriter(outputStreamWriterObj);
                    PrintWriter printWriterObj = new PrintWriter(bufferedWriterObj, true);
                    InputStreamReader InputStreamReaderObj = new InputStreamReader(socketConnection.getInputStream());
                    BufferedReader bufferedReaderObj = new BufferedReader(InputStreamReaderObj);

                    File input = file, output;
                    String filePath = new File("").getAbsolutePath() + "/../../data/reservations-output/"
                            + input.getName();
                    output = new File(filePath);
                    FileWriter fileWriterObj = new FileWriter(output);
                    Scanner sc = new Scanner(input);
                    String query;

                    // read input queries
                    while (sc.hasNextLine()) {
                        query = sc.nextLine();
                        printWriterObj.println(query);
                    }

                    // get query responses from the input end of the socket of client
                    char cur;
                    while ((cur = (char) bufferedReaderObj.read()) != (char) -1) {
                        fileWriterObj.write(cur);
                    }

                    // close the buffers and socket
                    sc.close();
                    fileWriterObj.close();
                    socketConnection.close();
                    System.out.println("File Completed: " + file.getName());
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("Retrying Connection: " + file.getName());
                try {
                    Thread.sleep(1);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

    }
}

class invokeWorkers implements Runnable {
    Client clientObj;
    int secondLevelThreads;

    public invokeWorkers(int secondLevelThreadsLocal, Client clientObjLocal) {
        secondLevelThreads = secondLevelThreadsLocal;
        clientObj = clientObjLocal;
    }

    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(secondLevelThreads);
        File file = null;
        while ((file = clientObj.assignFile()) != null) {
            if (file.getName().charAt(0) == '.') {
                continue;
            }

            Runnable runnableTask = new sendQuery(file);
            executorService.submit(runnableTask);
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        // stop further requests to executor service
        executorService.shutdown();
        try {
            // wait for 8 sec and then exit the executor service
            if (!executorService.awaitTermination(100, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

public class Client {
    public File[] fileList;
    public int fileIndex;

    public synchronized File assignFile() {
        if (fileIndex == fileList.length) {
            return null;
        }

        return fileList[fileIndex++];
    }

    public static void shuffleArray(File[] array) {
        int index;
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            index = random.nextInt(array.length);
            File curFile = array[index];
            array[index] = array[i];
            array[i] = curFile;
        }
    }

    public static void main(String[] args) throws IOException {
        Client clientObj = new Client();
        String filePath = new File("").getAbsolutePath() + "/../../data/reservations-input";
        File directoryPath = new File(filePath);
        clientObj.fileIndex = 0;
        clientObj.fileList = directoryPath.listFiles();
        shuffleArray(clientObj.fileList);

        // Creating a thread pool
        int firstLevelThreads = Math.min(15, (int) Math.ceil(Math.sqrt(clientObj.fileList.length))),
                secondLevelThreads = Math.min(15, (int) Math.ceil(Math.sqrt(clientObj.fileList.length)));
        ExecutorService executorServiceObj = Executors.newFixedThreadPool(firstLevelThreads);
        for (int i = 0; i < firstLevelThreads; i++) {
            Runnable runnableTask = new invokeWorkers(secondLevelThreads, clientObj);
            executorServiceObj.submit(runnableTask);
        }
        executorServiceObj.shutdown();

        try {
            if (!executorServiceObj.awaitTermination(100, TimeUnit.SECONDS)) {
                executorServiceObj.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorServiceObj.shutdownNow();
        }
    }
}
