package web.agent.servlets;

import jakarta.servlet.annotation.WebServlet;
import web.abstractServlet.UsernamesServlet;

import java.io.PrintWriter;

@WebServlet(name = "AgentNamesServlet", urlPatterns = "/agent/username")
public class AgentUsernamesServlet extends UsernamesServlet {

    @Override
    protected final void print(PrintWriter out) {
        out.println("Agent usernames list so far:");
        for (int i = 0; i < getUsernamesList().size(); i++) {
            out.println( "#" + (i+1) + ": " + getUsernamesList().get(i) );
        }
    }
}