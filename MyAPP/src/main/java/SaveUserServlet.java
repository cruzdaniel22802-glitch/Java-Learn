package main.java;

import main.java.util.ValidationUtils;
import main.java.util.ResponseUtils;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

public class SaveUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String name = request.getParameter("name");
        String ageStr = request.getParameter("age");
        String birthdayStr = request.getParameter("birthday");
        String isActiveStr = request.getParameter("isActive");

        // ---------- VALIDATION ----------

        if (!ValidationUtils.isValidName(name)) {
            ResponseUtils.showError(response.getWriter(), "Name must contain letters only.");
            return;
        }

        if (!ValidationUtils.isValidAge(ageStr)) {
            ResponseUtils.showError(response.getWriter(), "Age must be between 1 and 150.");
            return;
        }

        Date birthday = ValidationUtils.parseValidDate(birthdayStr);
        if (birthday == null) {
            ResponseUtils.showError(response.getWriter(), "Birthday must be in MM/DD/YYYY format.");
            return;
        }

        boolean isActive = Boolean.parseBoolean(isActiveStr);
        int age = Integer.parseInt(ageStr);

        // ---------- DATABASE INSERT ----------

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/user_app",
                    "root",
                    "C@ndy22802"
            );

            String sql = "INSERT INTO users (name, age, birthday, isActive) VALUES (?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setDate(3, new java.sql.Date(birthday.getTime()));
            stmt.setBoolean(4, isActive);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

            // 🔥 IMPORTANT PART
            response.sendRedirect("users.html");

        } catch (Exception e) {
            ResponseUtils.showDbError(response.getWriter(), e);
        }
    }
}
