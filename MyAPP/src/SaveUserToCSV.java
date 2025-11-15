import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SaveUserToCSV {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Accept user input
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();

        // Define the CSV file path
        String filePath = "user_data.csv";

        // Write to CSV file
        try (FileWriter writer = new FileWriter(filePath, true)) { // 'true' enables appending
            writer.append(name);
            writer.append(",");
            writer.append(String.valueOf(age));
            writer.append("\n");
            System.out.println("Data saved to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to CSV file.");
            e.printStackTrace();
        }

        scanner.close();
    }
}