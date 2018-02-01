package API;

import Models.MovieOut;
import Models.StarOut;
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

@WebServlet("/api/star")
public class StarServlet extends HttpServlet {

	Gson gson = new Gson();

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
		response.setContentType("application/json"); // Response mime type
		PrintWriter out = response.getWriter();
		try {
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();
			
			String starId = request.getParameter("starId");
			List<StarOut> starOutList = new ArrayList<StarOut>();
			StarOut so = new StarOut();

			String query = "select s.name as name, s.birthYear as year from stars s where s.id = '" + starId + "';";
			ResultSet rs = statement.executeQuery(query);
			
			while(rs.next()) {
				so.setName(rs.getString("name"));
				so.setBirth(rs.getString("year"));
			}
			
			String query2 = "select m.id as id, m.title as title from movies m, stars_in_movies st "
					+ "where st.movieId = m.id and st.starsId = '" + starId + "' order by m.title;";

			ResultSet rs2 = statement.executeQuery(query2);
			while (rs2.next()) {
				so.addMovieId(rs2.getString("id"));
				so.addMovieNames(rs2.getString("title"));
			}

			rs.close();
			rs2.close();
			statement.close();
			dbcon.close();

			starOutList.add(so);
			String result = gson.toJson(starOutList);
			out.write(result.toString());
			out.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} 
		}
		catch (java.lang.Exception ex) {
			System.out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
					+ "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
			return;
		}
	}
}
