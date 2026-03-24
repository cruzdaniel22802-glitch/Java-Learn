import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.ConfigLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    ConfigLoader.get("db.url"),
                    ConfigLoader.get("db.username"),
                    ConfigLoader.get("db.password")
            );

            String sql = "SELECT * FROM accounts WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                // ✅ CREATE SESSION
                HttpSession session = request.getSession();
                session.setAttribute("username", username);

                // ✅ redirect to dashboard
                response.sendRedirect("DashboardServlet");

            } else {

                response.sendRedirect("index.html?error=1");

            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}