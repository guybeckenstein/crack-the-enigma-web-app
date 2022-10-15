package web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebListener
@WebServlet(name = "HelloWorld",
        urlPatterns = "/HelloWorld", initParams = {
        @WebInitParam(name="name", value="Not provided"),
        @WebInitParam(name="email", value="Not provided")
})
public class HelloWorldServlet extends HttpServlet implements ServletContextListener {

    @Override
    public void init() { // Useful for @initParams
        // String defaultValue = getServletConfig().getInitParameter("");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("Hello World!");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ServletContextEvent started!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ServletContextEvent destroyed!");
    }
}
