package main.java.util;

import java.io.PrintWriter;

public class ResponseUtils {
    public static void showError(PrintWriter out, String message) {
        out.println("<h2 style='color:red'>❌ " + message + "</h2>");
        out.println("<br><a href='index.html'>⬅ Back to form</a>");
    }

    public static void showDbError(PrintWriter out, Exception e) {
        showError(out, "Database error: " + e.getMessage());
    }
}
