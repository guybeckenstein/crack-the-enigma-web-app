package web.uBoat.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@WebServlet(name = "MachineConfigurationServlet", urlPatterns = "/uboat/machine-configuration")
public class MachineConfigurationServlet extends HttpServlet {
    private String configuration;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        Properties properties = new Properties();
        properties.load(request.getInputStream());

        configuration = properties.getProperty("configuration");
        try {
            configuration = java.net.URLDecoder.decode(configuration, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }

        if (configuration != null && !configuration.isEmpty()) {
            response.getWriter().println(configuration);
        } else {
            throw new ServletException("An empty configuration has been given.");
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        response.getWriter().println(configuration);
    }
}
