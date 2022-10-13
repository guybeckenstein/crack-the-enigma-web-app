package web.servlets.uBoat;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@WebServlet(name = "LoadXmlFileServlet", urlPatterns = "/uboat/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXmlFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        StringBuilder fileContent = new StringBuilder();

        for (Part part : request.getParts()) {
            fileContent.append(readFromInputStream(part.getInputStream())); // to write the content of the file to a string
        }

        response.getWriter().println(fileContent);
    }

    private String readFromInputStream(InputStream inputStream) { // Actual file content
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        StringBuilder fileContent = new StringBuilder();

        for (Part part : request.getParts()) {
            fileContent.append(readFromInputStream(part.getInputStream())); // to write the content of the file to a string
        }

        response.getWriter().println(fileContent);
    }
}