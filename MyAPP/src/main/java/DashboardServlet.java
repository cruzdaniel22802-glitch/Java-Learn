package main.java;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Check if session exists
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.html");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = (String) session.getAttribute("user");

        out.println("<html><body>");
        out.println("<h1>Welcome, " + username + "!</h1>");
        out.println("<a href='LogoutServlet'>Logout</a>");
        out.println("<hr>");

        out.println("<h2>Add New User</h2>");
        out.println("<form action='SaveUserServlet' method='post'>");
        out.println("Name: <input name='name'><br><br>");
        out.println("Age: <input name='age'><br><br>");
        out.println("Birthday (MM/DD/YYYY): <input name='birthday'><br><br>");
        out.println("Active (true/false): <input name='isActive'><br><br>");
        out.println("<button type='submit'>Add User</button>");
        out.println("</form>");

        out.println("</body></html>");
    }
}