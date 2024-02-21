## HOW TO USE
    - Open up the terminal.
    - Navigate to the directory '/Railway Reservation System/java/src'.
    - Run the command 'javac DatabaseInit.java' followed by the 'java DatabaseInit' command to compile and run the database initialisation procedures.
    - Run the command 'javac Server.java' followed by the 'java Server' command to compile and run the server start-up procedures.
    - Copy all the required information (other than tickets) in their respective files presernt in the '/Railway Reservation System/data' directory according to the format specified by the dummy data present.
    - Import and use the required procedures in the 'App.java' file.
    - Run the command 'javac App.java' follwed by the 'java App' command to compile and run the required procedures.
    - Copy all the reservation input files in the 'Railway Reservation System/data/input-reservation' directory.
    - Run the command 'javac Client.java' followed by the 'java Client' command to compile and run the client procedures for ticket booking.


## ASSUMPTIONS
    - All the train, station, and passenger names are assumed to be of lenght less than or equal to thirty.
    - It is assumed that the user names does not contain intermediate blank spaces.
    - It is assumed that all trains ply on all days. Train routes are scheduled assuming 2022-12-01 is the first day, later days represent the offset of trains starting from the first day. All further days will follow the similar schedules.
    - All train and station IDs are assumed to be of length exactly equal to five and and contain only digits.
    - The maximum limit of first level and second level number of threads can be changed in the source code of the 'Client.java' file.
