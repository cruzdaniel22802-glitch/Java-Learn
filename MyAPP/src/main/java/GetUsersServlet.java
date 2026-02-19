package main.java;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;

public class GetUsersServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");

        StringBuilder json = new StringBuilder();
        json.append("[");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/user_app",
                    "root",
                    "C@ndy22802"
            );

            PreparedStatement stmt =
                    conn.prepareStatement("SELECT * FROM users");
            ResultSet rs = stmt.executeQuery();

            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                Date dbBirthday = rs.getDate("birthday");

                String birthday = (dbBirthday != null)
                        ? new SimpleDateFormat("MM/dd/yyyy").format(dbBirthday)
                        : "";


                json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"name\":\"").append(rs.getString("name")).append("\",")
                        .append("\"age\":").append(rs.getInt("age")).append(",")
                        .append("\"birthday\":\"").append(birthday).append("\",")
                        .append("\"isActive\":").append(rs.getBoolean("isActive"))
                        .append("}");
            }

            json.append("]");

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("[]");
            return;
        }

        response.getWriter().write(json.toString());
    }
}
