import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        String insertSql = "INSERT INTO users (name, age, birthday, isActive) VALUES (?, ?, ?, ?)";
        String fetchSql = "SELECT id, name, age, birthday, isActive FROM users";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load driver

            try (Connection conn = DriverManager.getConnection(url, user, password)) {

                // Insert user
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, name);
                    insertStmt.setInt(2, age);
                    insertStmt.setDate(3, new java.sql.Date(birthday.getTime()));
                    insertStmt.setBoolean(4, isActive);
                    insertStmt.executeUpdate();
                    System.out.println("✅ User data saved to MySQL successfully.\n");
                }

                // Fetch and print all users
                try (PreparedStatement fetchStmt = conn.prepareStatement(fetchSql);
                     ResultSet rs = fetchStmt.executeQuery()) {

                    System.out.println("📋 Current Users in Database:");
                    System.out.printf("%-5s %-15s %-5s %-12s %-10s%n", "ID", "Name", "Age", "Birthday", "Active");
                    System.out.println("------------------------------------------------------");

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String fetchedName = rs.getString("name");
                        int fetchedAge = rs.getInt("age");
                        Date fetchedBirthday = rs.getDate("birthday");
                        boolean fetchedActive = rs.getBoolean("isActive");

                        String birthdayFormatted = (fetchedBirthday != null)
                                ? new SimpleDateFormat("MM/dd/yyyy").format(fetchedBirthday)
                                : "N/A";

                        System.out.printf("%-5d %-15s %-5d %-12s %-10s%n",
                                id, fetchedName, fetchedAge, birthdayFormatted, fetchedActive);
                    }

                }

            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("❌ Database error:");
            e.printStackTrace();
        }

        scanner.close();
    }
}