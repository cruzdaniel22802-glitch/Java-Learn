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

                    printTable(conn, fetchSql);
                }

                // Prompt for ID and fetch that user
                System.out.print("\n🔍 Enter an ID to view that user's details: ");
                int searchId = scanner.nextInt();
                getUserById(conn, searchId);

                // Prompt to change ID
                System.out.print("\n✏️ Do you want to edit this user? (yes/no): ");
                scanner.nextLine();
                String editChoice = scanner.nextLine(); // consume newline

                if (editChoice.equalsIgnoreCase("yes")) {
                    updateUserById(conn, searchId, scanner);
                    printTable(conn, fetchSql);
                }
                // Prompt to delete ID
                System.out.print("\n🔍 Enter an ID to view that user's details: ");
                int deleteId = scanner.nextInt();
                scanner.nextLine();
                System.out.print("\n🗑️ Do you want to delete this user? (yes/no): ");
                scanner.nextLine(); // consume newline
                String deleteChoice = scanner.nextLine();

                if (deleteChoice.equalsIgnoreCase("yes")) {
                    deleteUserById(conn, deleteId, scanner);
                }
                printTable(conn, fetchSql);

            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("❌ Database error:");
            e.printStackTrace();
        }

        scanner.close();
    }

    // Method to fetch and display a user by ID
    public static void getUserById(Connection conn, int id) {
        String sql = "SELECT id, name, age, birthday, isActive FROM users WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int age = rs.getInt("age");
                    Date birthday = rs.getDate("birthday");
                    boolean isActive = rs.getBoolean("isActive");

                    String birthdayFormatted = (birthday != null)
                            ? new SimpleDateFormat("MM/dd/yyyy").format(birthday)
                            : "N/A";

                    System.out.println("\n🎯 User Details:");
                    System.out.println("ID       : " + id);
                    System.out.println("Name     : " + name);
                    System.out.println("Age      : " + age);
                    System.out.println("Birthday : " + birthdayFormatted);
                    System.out.println("Active   : " + isActive);
                } else {
                    System.out.println("⚠️ No user found with ID " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching user by ID:");
            e.printStackTrace();
        }
    }
    public static void updateUserById(Connection conn, int id, Scanner scanner) {
        String updateSql = "UPDATE users SET name = ?, age = ?, birthday = ?, isActive = ? WHERE id = ?";

        try {
            // Prompt for new values
            scanner.nextLine(); // clear buffer
            System.out.print("Enter new name: ");
            String newName = scanner.nextLine();

            System.out.print("Enter new age: ");
            int newAge = scanner.nextInt();
            scanner.nextLine(); // consume newline

            System.out.print("Enter new birthday (MM/dd/yyyy): ");
            String newBirthdayStr = scanner.nextLine();

            System.out.print("Is the user active? (true/false): ");
            boolean newIsActive = scanner.nextBoolean();

            // Parse birthday
            Date newBirthday;
            try {
                newBirthday = new SimpleDateFormat("MM/dd/yyyy").parse(newBirthdayStr);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Update aborted.");
                return;
            }

            // Execute update
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, newName);
                stmt.setInt(2, newAge);
                stmt.setDate(3, new java.sql.Date(newBirthday.getTime()));
                stmt.setBoolean(4, newIsActive);
                stmt.setInt(5, id);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ User record updated successfully.");
                } else {
                    System.out.println("⚠️ No user found with ID " + id);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error updating user:");
            e.printStackTrace();
        }
    }
    public static void printTable(Connection conn, String fetchSql) {
        System.out.println("📋 Current Users in Database:");
        System.out.printf("%-5s %-15s %-5s %-12s %-10s%n", "ID", "Name", "Age", "Birthday", "Active");
        System.out.println("------------------------------------------------------");

        try (PreparedStatement fetchStmt = conn.prepareStatement(fetchSql);
             ResultSet rs = fetchStmt.executeQuery()) {
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
        }catch (SQLException e) {
                System.out.println("❌ Error fetching user by ID:");
                e.printStackTrace();
            }
    }
    public static void deleteUserById(Connection conn, int id, Scanner scanner) {
        String deleteSql = "DELETE FROM users WHERE id = ?";

        System.out.print("⚠️ Are you sure you want to delete user with ID " + id + "? (yes or no): ");
        scanner.nextLine(); // consume leftover newline
        String confirm = scanner.nextLine();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("❎ Deletion cancelled.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("🗑️ User with ID " + id + " deleted successfully.");
            } else {
                System.out.println("⚠️ No user found with ID " + id + ". Nothing was deleted.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error deleting user:");
            e.printStackTrace();
        }
    }
}