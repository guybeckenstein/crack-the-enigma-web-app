package web.servlets.agent;

import jakarta.servlet.annotation.WebServlet;
import web.servlets.abstractServlet.NamesListServlet;

import java.io.PrintWriter;

@WebServlet(name = "AgentNamesServlet", urlPatterns = "/agent/names")
public class AgentNamesListServlet extends NamesListServlet {

    @Override
    protected final void printNamesList(PrintWriter out) {
        out.println("Agent names list so far:");
        for (int i = 0; i < getNamesList().size(); i++) {
            out.println( "#" + (i+1) + ": " + getNamesList().get(i) );
        }
    }
}