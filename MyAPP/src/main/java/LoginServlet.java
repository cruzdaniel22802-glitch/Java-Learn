package main.java;

import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/user_app",
                    "root",
                    "C@ndy22802"
            );

            String sql = "SELECT * FROM accounts WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // LOGIN SUCCESS

                HttpSession session = request.getSession();
                session.setAttribute("user", username);

                response.sendRedirect("DashboardServlet");
            } else {
                response.getWriter().println("<h3>Invalid credentials</h3>");
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
