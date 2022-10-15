package web.allies.servlets;

import jakarta.servlet.annotation.WebServlet;
import web.abstractServlet.UsernamesServlet;

import java.io.PrintWriter;

@WebServlet(name = "AlliesNamesServlet", urlPatterns = "/allies/username")
public class AlliesUsernamesServlet extends UsernamesServlet {

    @Override
    protected final void print(PrintWriter out) {
        out.println("Allies usernames list so far:");
        for (int i = 0; i < getUsernamesList().size(); i++) {
            out.println( "#" + (i+1) + ": " + getUsernamesList().get(i) );
        }
    }
}