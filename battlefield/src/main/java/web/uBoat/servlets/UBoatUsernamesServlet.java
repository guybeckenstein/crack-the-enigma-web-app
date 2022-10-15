package web.uBoat.servlets;

import jakarta.servlet.annotation.WebServlet;
import web.abstractServlet.UsernamesServlet;

import java.io.PrintWriter;

@WebServlet(name = "UBoatNamesServlet", urlPatterns = "/uboat/username")
public class UBoatUsernamesServlet extends UsernamesServlet {
    @Override
    protected final void print(PrintWriter out) {
        out.println("UBoat usernames list so far:");
        for (int i = 0; i < getUsernamesList().size(); i++) {
            out.println( "#" + (i + 1) + ": " + getUsernamesList().get(i) );
        }
    }
}