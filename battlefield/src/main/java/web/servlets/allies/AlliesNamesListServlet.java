package web.servlets.allies;

import jakarta.servlet.annotation.WebServlet;
import web.servlets.abstractServlet.NamesListServlet;

import java.io.PrintWriter;

@WebServlet(name = "AlliesNamesServlet", urlPatterns = "/allies/names")
public class AlliesNamesListServlet extends NamesListServlet {

    @Override
    protected final void printNamesList(PrintWriter out) {
        out.println("Allies names list so far:");
        for (int i = 0; i < getNamesList().size(); i++) {
            out.println( "#" + (i+1) + ": " + getNamesList().get(i) );
        }
    }
}