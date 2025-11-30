package main.java;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

//@WebServlet("/SaveUserServlet")
public class SaveUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = request.getParameter("name");
        int age = Integer.parseInt(request.getParameter("age"));
        String birthdayStr = request.getParameter("birthday");
        boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Date birthday = new SimpleDateFormat("MM/dd/yyyy").parse(birthdayStr);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/user_app", "root", "C@ndy22802");

            //Insert user
            String sql = "INSERT INTO users (name, age, birthday, isActive) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setDate(3, new java.sql.Date(birthday.getTime()));
            stmt.setBoolean(4, isActive);
            stmt.executeUpdate();
            stmt.close();

            out.println("<h2>✅ User && saved  successfully.</h2>");


            //Fetch all users
            String fetchSql = "SELECT id, name, age, birthday, isActive FROM users";
            PreparedStatement fetchStmt = conn.prepareStatement(fetchSql);
            ResultSet rs = fetchStmt.executeQuery();

            out.println("<h3>📋 Current Users in Database:</h3>");
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Name</th><th>Age</th><th>Birthday</th><th>Active</th></tr>");

            while (rs.next()) {
                int id = rs.getInt("id");
                String fetchedName = rs.getString("name");
                int fetchedAge = rs.getInt("age");
                Date fetchedBirthday = rs.getDate("birthday");
                boolean fetchedActive = rs.getBoolean("isActive");

                String birthdayFormatted = (fetchedBirthday != null)
                        ? new SimpleDateFormat("MM/dd/yyyy").format(fetchedBirthday)
                        : "N/A";

                out.printf("<tr><td>%d</td><td>%s</td><td>%d</td><td>%s</td><td>%s</td></tr>",
                        id, fetchedName, fetchedAge, birthdayFormatted, fetchedActive);
            }
            out.println("</table>");

            rs.close();
            fetchStmt.close();
            conn.close();

        } catch (Exception e) {
            response.getWriter().println("❌ Error: " + e.getMessage());
        }
    }
}
