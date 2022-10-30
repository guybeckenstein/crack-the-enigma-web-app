package web.agent.servlets.login;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "LoginAgentServlet", urlPatterns = "/agent/login")
public class LoginAgentServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/username").include(request, response);
        getServletContext().getRequestDispatcher("/agent/join/allies").include(request, response);
    }
}
