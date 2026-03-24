import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.ConfigLoader;

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
            Class.forName(ConfigLoader.get("db.driver"));

            Connection conn = DriverManager.getConnection(
                    ConfigLoader.get("db.url"),
                    ConfigLoader.get("db.username"),
                    ConfigLoader.get("db.password")
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
