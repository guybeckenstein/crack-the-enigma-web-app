package web.uBoat.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@WebServlet(name = "LoadXmlFileServlet", urlPatterns = "/uboat/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXmlFileServlet extends HttpServlet {
    private final String TITLE_PARAMETER = "title";
    private final String USERNAME_PARAMETER = "username";
    private final StringBuffer stringBuffer;

    public LoadXmlFileServlet() {
        stringBuffer = null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");

        Properties properties = new Properties();
        properties.load(request.getInputStream());

        String title = properties.getProperty(TITLE_PARAMETER);
        String username = null, xmlFile = null;
        try {
            String result = java.net.URLDecoder.decode(title, StandardCharsets.UTF_8.name());
            String[] bodyProperties = result.split("&");
            title = bodyProperties[0]; // title='?'
            username = bodyProperties[1].substring(9); // username='?'
            xmlFile = bodyProperties[2].substring(4); // xml='?'

            request.getSession(true).setAttribute("xml", xmlFile);
        } catch (UnsupportedEncodingException e) {
            // not going to happen - value came from JDK's own StandardCharsets
        }
        request.setAttribute(TITLE_PARAMETER, title);
        request.setAttribute(USERNAME_PARAMETER, username);
        try {
            getServletContext().getRequestDispatcher("/uboat/title").include(request, response);
        } catch (Exception e) {
            throw new ServletException(e.getLocalizedMessage());
        }

        response.getWriter().println(xmlFile); // To return the content of the file to the client (postman)
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        if (stringBuffer == null) {
            throw new ServletException("This UBoat did not load file yet.");
        } else {
            response.getWriter().println(stringBuffer); // To return the content of the file to the client
        }
    }
}