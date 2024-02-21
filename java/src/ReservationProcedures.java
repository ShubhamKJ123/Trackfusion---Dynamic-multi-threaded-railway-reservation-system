import java.sql.*;

public class ReservationProcedures {
    String bookTicket(int numberOfPassengers, String[] user_names, String train_id, Date date_of_journey,
            String coach_type, Connection connection) {
        boolean flag = true;
        while (flag) {
            try {
                // validate all the passenger ids
                String sql = null;
                CallableStatement cs = null;
                connection.setAutoCommit(false);

                // check the availability of seats in the required coach type
                sql = "{CALL check_availability(?, ?, ?, ?)}";
                cs = connection.prepareCall(sql);
                cs.setString(1, train_id);
                cs.setDate(2, date_of_journey);
                cs.setString(3, coach_type);
                cs.setInt(4, numberOfPassengers);

                ResultSet resultSet = cs.executeQuery();
                resultSet.next();
                int number_of_seats_left = resultSet.getInt(1);

                // generate a unique PNR
                Utilities utilities = new Utilities();
                String pnr_number = utilities.generatePNR(train_id, date_of_journey, number_of_seats_left, coach_type);

                // remove the total number of seats registered from the train table
                sql = "{CALL remove_seats(?, ?, ?, ?)}";
                cs = connection.prepareCall(sql);
                cs.setString(1, train_id);
                cs.setDate(2, date_of_journey);
                cs.setString(3, coach_type);
                cs.setInt(4, numberOfPassengers);
                cs.execute();

                // add reservations for all the passengers
                for (int i = 0; i < numberOfPassengers; i++) {
                    sql = "{CALL make_reservation(?, ?, ?, ?, ?, ?)}";
                    cs = connection.prepareCall(sql);
                    cs.setString(1, user_names[i]);
                    cs.setString(2, train_id);
                    cs.setDate(3, date_of_journey);
                    cs.setString(4, pnr_number);
                    cs.setString(5, coach_type);
                    cs.setInt(6, number_of_seats_left - i);
                    cs.execute();
                }

                connection.commit();
                return pnr_number;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                if (e.getMessage().contains("seats are not available")) {
                    flag = false;
                    break;
                }

                try {
                    connection.rollback();
                } catch (Exception E) {
                    System.err.println(E.getMessage());
                }
            }
        }
        return "-1";
    }

    void generateTicket(String pnr_number, Connection connection) {
        try {
            String sql = "{CALL generate_ticket(?)}";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, pnr_number);
            cs.execute();

            ResultSet resultSet = cs.executeQuery();
            resultSet.next();

            System.out.println("Train ID: " + resultSet.getString(2));
            System.out.println("Train Name: " + resultSet.getString(3));
            System.out.println("Date-Of-Journey: " + resultSet.getDate(4));
            System.out.println("PNR Number: " + pnr_number + "\n");
            System.out.println("Passenger Name:\t\t\t\t\tCoach Number:\t\tSeat Number:\t\tSeat Type:");
            do {
                String passangerName = resultSet.getString(1);
                while (passangerName.length() < 30) {
                    passangerName = passangerName + " ";
                }

                System.out.print("  " + passangerName + "\t\t  " + resultSet.getString(5) + "\t\t\t  "
                        + resultSet.getInt(6) + "\t\t\t  " + resultSet.getString(7) + "\n");
            } while (resultSet.next());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return;
    }
}