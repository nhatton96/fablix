package API;

import Models.Customer;
import Controllers.EmployeeService;
import Controllers.CustomerService;

import java.io.IOException;
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
	private CustomerService customerService = new CustomerService();
	private EmployeeService employeeService = new EmployeeService();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//response.setContentType("application/json");
		HttpSession session = request.getSession(true);
		String type = request.getParameter("type");
		String username = request.getParameter("email");
	    String password = request.getParameter("password");

	    if (type.equals("customer")) {
			Customer customer = customerService.find(username, password);
			if(customer.getEmail() != ""){
				request.getSession().setAttribute("customer", customer);
				response.setStatus(HttpServletResponse.SC_OK);
			}else {
				request.setAttribute("error", "Unknown user, please try again");
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}
	    }
	    else if(type.equals("employee")){
			Customer employee = employeeService.find(username, password);
			if(employee.getEmail() != ""){
				request.getSession().setAttribute("employee", employee);
				response.setStatus(HttpServletResponse.SC_OK);
			}else {
				request.setAttribute("error", "Unknown user, please try again");
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}
		}
	    else {
	        request.setAttribute("error", "Unknown user, please try again");
	        request.getRequestDispatcher("/login.jsp").forward(request, response);
	    }
	}

}
