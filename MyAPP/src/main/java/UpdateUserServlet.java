package main.java;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.util.ResponseUtils;
import main.java.util.ValidationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

public class UpdateUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String ageStr = request.getParameter("age");
        String birthdayStr = request.getParameter("birthday");
        String isActiveStr = request.getParameter("isActive");

        // ---------- VALIDATION ----------

        if (idStr == null || !idStr.matches("\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtils.showError(response.getWriter(), "Invalid user ID.");
            return;
        }

        if (!ValidationUtils.isValidName(name)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtils.showError(response.getWriter(), "Name must contain letters only.");
            return;
        }

        if (!ValidationUtils.isValidAge(ageStr)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtils.showError(response.getWriter(), "Age must be between 1 and 150.");
            return;
        }

        Date birthday = ValidationUtils.parseValidDate(birthdayStr);
        if (birthday == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtils.showError(response.getWriter(), "Birthday must be MM/DD/YYYY.");
            return;
        }

        // ---------- PARSING (SAFE NOW) ----------

        int id = Integer.parseInt(idStr);
        int age = Integer.parseInt(ageStr);
        boolean isActive = Boolean.parseBoolean(isActiveStr);

        // ---------- DATABASE ----------

        try {
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

            stmt.close();
            conn.close();

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            ResponseUtils.showDbError(response.getWriter(), e);
        }
    }
}
