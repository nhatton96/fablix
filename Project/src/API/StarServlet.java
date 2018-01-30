package API;

import Controllers.CustomerService;
import Models.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/star")
public class StarServlet extends HttpServlet{
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    private CustomerService customerService = new CustomerService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //response.setContentType("application/json");
        HttpSession session = request.getSession(true);
        String username = request.getParameter("email");
        String password = request.getParameter("password");
        Customer customer = customerService.find(username, password);

        if (customer.getEmail() != "") {
            request.getSession().setAttribute("customer", customer);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            request.setAttribute("error", "Unknown user, please try again");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
