package main.java;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class DeleteUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idStr = request.getParameter("id");

        if (idStr == null || !idStr.matches("\\d+")) {
            response.setStatus(400);
            response.getWriter().write("Invalid ID");
            return;
        }

        int id = Integer.parseInt(idStr);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/user_app",
                    "root",
                    "C@ndy22802"
            );

            PreparedStatement stmt =
                    conn.prepareStatement("DELETE FROM users WHERE id=?");
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                response.setStatus(404);
                response.getWriter().write("User not found");
                return;
            }

            stmt.close();
            conn.close();

            response.setStatus(200);

        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write(e.getMessage());
        }
    }
}