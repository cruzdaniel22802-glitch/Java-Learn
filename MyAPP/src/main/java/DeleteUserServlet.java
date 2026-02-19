package main.java;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class DeleteUserServlet extends HttpServlet {

        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                throws IOException {

            String idsParam = request.getParameter("ids");

            // Validate input
            if (idsParam == null || idsParam.isEmpty()) {
                response.setStatus(400);
                response.getWriter().write("No IDs provided");
                return;
            }

            String[] ids = idsParam.split(",");

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/user_app",
                        "root",
                        "C@ndy22802"
                );

                PreparedStatement stmt =
                        conn.prepareStatement("DELETE FROM users WHERE id = ?");

                int totalDeleted = 0;

                for (String idStr : ids) {

                    // Validate each ID
                    if (!idStr.matches("\\d+")) {
                        continue; // skip invalid ID
                    }

                    stmt.setInt(1, Integer.parseInt(idStr));
                    totalDeleted += stmt.executeUpdate();
                }

                stmt.close();
                conn.close();

                response.setStatus(200);
                response.getWriter().write("Deleted " + totalDeleted + " users");

            } catch (Exception e) {
                e.printStackTrace(); // very important for debugging
                response.setStatus(500);
                response.getWriter().write("Server error");
            }
        }
    }