package web.servlets.abstractServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class NamesListServlet extends HttpServlet {
    private static final String NAME_PARAMETER = "name";
    public static final String LIST_PROPERTY_NAME = "namesList";

    // Shows names list
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = resp.getWriter()) {
            printNamesList(out);
        }
    }

    protected abstract void printNamesList(PrintWriter out);

    protected List<String> getNamesList() {
        // option #2 - save the name list in the servlet context
        // The Servlet Context is shared between all servlets in the web application
        List<String> namesList = (List<String>) getServletContext().getAttribute(LIST_PROPERTY_NAME);
        if (namesList == null) {
            namesList = new ArrayList<>();

            getServletContext().setAttribute(LIST_PROPERTY_NAME, namesList);
        }

        // option #3 - save the name list in the session context
        // The Session Context is unique for each browser that is connected to the server
        // HttpSession session = request.getSession();
        // List<String> namesList = (List<String>) session.getAttribute(LIST_PROPERTY_NAME);
        // if (namesList == null) {
        //     namesList = new ArrayList<>();
        //     session.setAttribute(LIST_PROPERTY_NAME, namesList);
        // }
        return namesList;
    }

    // Add name
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = resp.getWriter()) {
            String name = req.getParameter(NAME_PARAMETER);

            if (name != null && !name.isEmpty()) {
                if (!getNamesList().contains(name)) {
                    getNamesList().add(name);
                } else {
                    out.println("ERROR: Name [" + name + "] already exists! Choose unique name");
                    return;
                }
            } else {
                out.println("ERROR: Invalid name given! Choose valid name");
                return;
            }

            out.println("Name [" + name + "] was added successfully to the names list");
        }
    }
}