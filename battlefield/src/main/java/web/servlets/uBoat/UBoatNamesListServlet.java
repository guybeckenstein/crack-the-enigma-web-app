package web.servlets.uBoat;

import jakarta.servlet.annotation.WebServlet;
import web.servlets.abstractServlet.NamesListServlet;

import java.io.PrintWriter;

@WebServlet(name = "UBoatNamesServlet", urlPatterns = "/uboat/names")
public class UBoatNamesListServlet extends NamesListServlet {

    @Override
    protected final void printNamesList(PrintWriter out) {
        out.println("UBoat names list so far:");
        for (int i = 0; i < getNamesList().size(); i++) {
            out.println( "#" + (i+1) + ": " + getNamesList().get(i) );
        }
    }
}