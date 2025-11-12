import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SaveUserInfo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Accept user input
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();

        // Define the file path (change this if needed)
        String filePath = "user_info.txt";

        // Write to file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Name: " + name + "\n");
            writer.write("Age: " + age + "\n");
            System.out.println("Information saved to " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }

        scanner.close();
    }
}