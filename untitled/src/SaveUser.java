import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SaveUser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Collect user input
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter your birthday (MM/dd/yyyy): ");
        String birthdayStr = scanner.nextLine();

        System.out.print("Is the user active? (true/false): ");
        boolean isActive = scanner.nextBoolean();

        // Parse birthday
        Date birthday = null;
        try {
            birthday = new SimpleDateFormat("MM/dd/yyyy").parse(birthdayStr);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use MM/dd/yyyy.");
            scanner.close();
            return;
        }

        // MySQL connection details
        String url = "jdbc:mysql://localhost:3306/user_app";
        String user = "root";
        String password = "C@ndy22802";

        String sql = "INSERT INTO users (name, age, birthday, isActive) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load driver

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, name);
                stmt.setInt(2, age);
                stmt.setDate(3, new java.sql.Date(birthday.getTime()));
                stmt.setBoolean(4, isActive);

                stmt.executeUpdate();
                System.out.println("User data saved to MySQL successfully.");
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error:");
            e.printStackTrace();
        }

        scanner.close();
    }
}