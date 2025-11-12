import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class SaveUser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Accept user input
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();

        // MySQL connection details
        String url = "jdbc:mysql://localhost:3306/user_app";
        String user = "root";
        String password = "C@ndy22802";

        // SQL insert statement
        String sql = "INSERT INTO users (name, age) VALUES (?, ?)";


        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Class.forName("com.mysql.cj.jdbc.Driver");
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.executeUpdate();

            System.out.println("User data saved to MySQL successfully.");

        }catch (SQLException e) {
            System.out.println("Database error:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        scanner.close();

    }

}