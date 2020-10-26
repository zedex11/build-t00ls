import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // установить MIME-type и кодировку ответа
        response.setContentType("text/html; charset=UTF8");
        PrintWriter out = response.getWriter();

        // Отправка веб-страницы
        try {
            out.println("<html>");
            out.println("<head><title>Servlet sample</title></head>");
            out.println("<body>");
            out.println("<p>Запрошенный ресурс: " + request.getRequestURI() + "</p>");
            out.println("<p>Протокол: " + request.getProtocol() + "</p>");
            out.println("<p>Адрес сервера: " + request.getRemoteAddr() + "</p>");
            out.println("</body></html>");
        } finally {
            out.close(); // Всегда закрывать Writer
        }
    }
}