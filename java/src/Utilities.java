import java.sql.*;

public class Utilities {
    String generatePNR(String train_id, Date date_of_journey, int number_of_seats_left, String coach_type) {
        String date = date_of_journey.toString();
        String prefix = train_id + date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10);
        if (coach_type == "A") {
            prefix = prefix + "1";
        } else {
            prefix = prefix + "0";
        }

        String suffix = String.valueOf(number_of_seats_left);
        while (suffix.length() < 6) {
            suffix = "0" + suffix;
        }

        return prefix + suffix;
    }
}