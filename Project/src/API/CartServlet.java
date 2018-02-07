package API;

import Models.MovieOut;
import Models.StarOut;
import Models.Cart;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

@WebServlet("/api/cart")
public class CartServlet extends HttpServlet {

	Gson gson = new Gson();

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		response.setContentType("application/json"); // Response mime type
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();
			HttpSession session = request.getSession();
			Cart shoppingCart;
			shoppingCart = (Cart) session.getAttribute("cart");
			if (shoppingCart == null) {
				shoppingCart = new Cart();
				session.setAttribute("cart", shoppingCart);
			}

			String action = request.getParameter("action");

			if (action.equals("add")) {
				String movieid = request.getParameter("id");
				String query = "Select m.title as title from movies m where m.id = '" + movieid + "'";
				ResultSet rs = statement.executeQuery(query);
				String mvtitle = "";
				while (rs.next()) {
					mvtitle = rs.getString("title");
				}
				int amount = Integer.parseInt(request.getParameter("amount"));
				shoppingCart.addToCart(movieid, amount, mvtitle);
				rs.close();
			}

			if (action.equals("set")) {
				String movieid = request.getParameter("id");
				int amount = Integer.parseInt(request.getParameter("amount"));
				shoppingCart.setCart(movieid, amount);
			}

			if (action.equals("show")) {
				PrintWriter out = response.getWriter();
				List<Cart> cartList = new ArrayList<Cart>();
				cartList.add(shoppingCart);
				String result = gson.toJson(cartList);
				out.write(result.toString());
				out.close();
			}

			statement.close();
			dbcon.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (java.lang.Exception ex) {
			System.out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
					+ "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
			return;
		}
	}
}
