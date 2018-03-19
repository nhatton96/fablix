
package API;

import Models.MovieOut;
import Models.TableOut;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class AddMovieServlet
 */
@WebServlet("/api/add")
public class AddMovieServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		Gson gson = new Gson();

		response.setContentType("application/json"); // Response mime type
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Context initCtx = new InitialContext();

			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			// Look up our data source
			DataSource ds = (DataSource) envCtx.lookup("jdbc/master");
			DataSource dss = (DataSource) envCtx.lookup("jdbc/slave");
			// the following commented lines are direct connections without pooling
			// Class.forName("org.gjt.mm.mysql.Driver");
			// Class.forName("com.mysql.jdbc.Driver").newInstance();
			// Connection dbcon = DriverManager.getConnection(loginUrl, loginUser,
			// loginPasswd);

			Connection dbcon = ds.getConnection();
			String action = request.getParameter("action");
			if (action.equals("movie")) {
				String title = request.getParameter("title");
				String year = request.getParameter("year");
				String genre = request.getParameter("genre");
				String director = request.getParameter("director");
				String star = request.getParameter("star");
				String call = "{call add_movie(?,?,?,?,?)}";
				try (CallableStatement stmt = dbcon.prepareCall(call)) {
					stmt.setString(1, title);
					stmt.setInt(2, Integer.parseInt(year));
					stmt.setString(3, director);
					stmt.setString(4, star);
					stmt.setString(5, genre);
					stmt.execute();
				}
				dbcon.close();
				List<String> result = new ArrayList<String>();
				result.add("testing");
				String revalue = gson.toJson(result);
				if (revalue != null) {
					out.write(revalue.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in Add Servlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				out.close();
			} else if (action.equals("star")) {
				String year = request.getParameter("year");
				String star = request.getParameter("star");
				String call = "{call add_star(?,?)}";
				try (CallableStatement stmt = dbcon.prepareCall(call)) {
					stmt.setInt(1, Integer.parseInt(year));
					stmt.setString(2, star);
					stmt.execute();
				}
				dbcon.close();
				List<String> result = new ArrayList<String>();
				result.add("testing");
				String revalue = gson.toJson(result);
				if (revalue != null) {
					out.write(revalue.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in Add Servlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				out.close();
			} else if (action.equals("table")) {
				Statement statement = dbcon.createStatement();
				String query = "show tables";
				ResultSet rs = statement.executeQuery(query);
				List<TableOut> tableOutList = new ArrayList<TableOut>();
				while (rs.next()) {
					TableOut tabout = new TableOut();
					String table = rs.getString(1);
					tabout.setName(table);
					Statement statement2 = dbcon.createStatement();
//					String attQuery = "select * from " + table + ";";
					String attQuery = "describe " + table + ";";
					ResultSet attrs = statement2.executeQuery(attQuery);
					while (attrs.next()) {
						tabout.addAtt(attrs.getString(1));
						tabout.addType(attrs.getString(2));
					}
//					ResultSetMetaData rsmd = attrs.getMetaData();
//					int numcol = rsmd.getColumnCount();
//					for (int i = 1; i <= numcol; ++i) {
//						tabout.addAtt(rsmd.getColumnName(i));
//						tabout.addType(rsmd.getColumnTypeName(i));
//					}
					attrs.close();
					statement2.close();
					tableOutList.add(tabout);
				}
				String tableList = gson.toJson(tableOutList);
				if (tableList != null) {
					out.write(tableList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in Add Servlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				rs.close();
				statement.close();
				dbcon.close();
				out.close();
			}
		} catch (java.lang.Exception ex) {
			out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
					+ "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
