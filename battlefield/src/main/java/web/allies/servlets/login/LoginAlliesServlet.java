package web.allies.servlets.login;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "LoginAlliesServlet", value = "/allies/login")
public class LoginAlliesServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/username").include(request, response); // Add username to general list
        getServletContext().getRequestDispatcher("/allies/add-agent").include(request, response); // Add username to allies teams map
    }
}
