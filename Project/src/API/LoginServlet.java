package API;
import Models.Customer;
import Controllers.CustomerService;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet{
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	private CustomerService customerService;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String username = request.getParameter("email");
	    String password = request.getParameter("password");
	    Customer customer = customerService.find(username, password);

	    if (customer != null) {
	        request.getSession().setAttribute("customer", customer);
	        response.sendRedirect("/servlet/MovieList");
	    }
	    else {
	        request.setAttribute("error", "Unknown user, please try again");
	        request.getRequestDispatcher("/login.jsp").forward(request, response);
	    }
	}

}
