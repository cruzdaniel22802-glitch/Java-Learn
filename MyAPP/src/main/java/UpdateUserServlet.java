package main.java;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UpdateUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String ageStr = request.getParameter("age");
        String birthdayStr = request.getParameter("birthday");
        String isActiveStr = request.getParameter("isActive");

        try {
            int id = Integer.parseInt(idStr);
            int age = Integer.parseInt(ageStr);
            boolean isActive = Boolean.parseBoolean(isActiveStr);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);
            Date birthday = sdf.parse(birthdayStr);

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/user_app",
                    "root",
                    "C@ndy22802"
            );

            String sql = """
                UPDATE users
                SET name = ?, age = ?, birthday = ?, isActive = ?
                WHERE id = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setDate(3, new java.sql.Date(birthday.getTime()));
            stmt.setBoolean(4, isActive);
            stmt.setInt(5, id);

            stmt.executeUpdate();

            conn.commit();

            stmt.close();
            conn.close();

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        }
    }
}
