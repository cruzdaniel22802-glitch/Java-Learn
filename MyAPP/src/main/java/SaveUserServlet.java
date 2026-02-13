package main.java;

import main.java.util.ValidationUtils;
import main.java.util.ResponseUtils;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String ageStr = request.getParameter("age");
        String birthdayStr = request.getParameter("birthday");
        boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));

        // ---------------- VALIDATION ----------------

        if (!ValidationUtils.isValidName(name)) {
            ResponseUtils.showError(out, "Name must contain letters only.");
            return;
        }

        if (!ValidationUtils.isValidAge(ageStr)) {
            ResponseUtils.showError(out, "Age must be a number between 1 and 150.");
            return;
        }

        Date birthday = ValidationUtils.parseValidDate(birthdayStr);
        if (birthday == null) {
            ResponseUtils.showError(out, "Birthday must be in MM/DD/YYYY format.");

            return;
        }


        // ---------------- DATABASE ----------------
        int age = Integer.parseInt(ageStr);

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

            out.println("<h2 style='color:green'>✅ User saved successfully.</h2>");

            // ---------------- DISPLAY USERS ----------------

            String fetchSql = "SELECT id, name, age, birthday, isActive FROM users";
            PreparedStatement fetchStmt = conn.prepareStatement(fetchSql);
            ResultSet rs = fetchStmt.executeQuery();

            out.println("<h3>📋 Current Users:</h3>");
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Name</th><th>Age</th><th>Birthday</th><th>Active</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td contenteditable='false'>" + rs.getString("name") + "</td>");
                out.println("<td contenteditable='false'>" + rs.getInt("age") + "</td>");
                Date dbBirthday = rs.getDate("birthday");

                String formattedBirthday =
                        (dbBirthday != null)
                                ? new SimpleDateFormat("MM/dd/yyyy").format(dbBirthday)
                                : "N/A";

                out.println("<td contenteditable='false'>" + formattedBirthday + "</td>");

                out.println("<td contenteditable='false'>" + rs.getBoolean("isActive") + "</td>");

                out.println("<td>");
                out.println("<button onclick='editRow(this)'>✏ Edit</button>");
                out.println("<button onclick='saveRow(this," + rs.getInt("id") + ")' style='display:none'>💾 Save</button>");
                out.println("<button onclick='addRow()'>➕ Add User</button>");
                out.println("<button onclick='deleteRow(this," + rs.getInt("id") + ")'>🗑 Delete</button>");
                out.println("</td>");

                out.println("</tr>");
            }

            out.println("</table>");

            out.println("""
<script>
function editRow(btn) {
    const row = btn.parentElement.parentElement;
    const cells = row.querySelectorAll("td[contenteditable]");

    cells.forEach(cell => cell.contentEditable = "true");

    btn.style.display = "none";
    btn.nextElementSibling.style.display = "inline";
}

function saveRow(btn, id) {
    const row = btn.parentElement.parentElement;
    const cells = row.querySelectorAll("td[contenteditable]");

    const data = {
        id: id,
        name: cells[0].innerText.trim(),
        age: cells[1].innerText.trim(),
        birthday: cells[2].innerText.trim(),
        isActive: cells[3].innerText.trim()
    };

    fetch("UpdateUserServlet", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: new URLSearchParams(data)
    }).then(res => {
        if (!res.ok) return res.text().then(t => alert(t));
        const cells = btn.parentElement.parentElement.querySelectorAll("td[contenteditable]");
        cells.forEach(c => c.contentEditable = "false");
        btn.style.display = "none";
        btn.previousElementSibling.style.display = "inline";
        });
                    
}

function deleteRow(btn, id) {

    if (!confirm("Delete this user?")) return;

    fetch("deleteUser", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: new URLSearchParams({ id })
    })
    .then(res => {
        if (!res.ok)
            return res.text().then(t => alert(t));

        btn.closest("tr").remove();
    });
}

function addRow() {

    const table = document.querySelector("table");
    const row = table.insertRow(-1);

    row.innerHTML = `
        <td>NEW</td>
        <td contenteditable="true"></td>
        <td contenteditable="true"></td>
        <td contenteditable="true"></td>
        <td contenteditable="true"></td>
        <td>
            <button onclick="saveNewRow(this)">💾 Save</button>
        </td>
    `;
}

</script>
""");


            out.println("<br><a href='index.html'>⬅ Back to form</a>");

            rs.close();
            fetchStmt.close();
            conn.close();

        } catch (Exception e) {
            ResponseUtils.showDbError(out, e);
        }
    }


}
