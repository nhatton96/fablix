package API;
import java.io.BufferedWriter;
import java.io.FileWriter;
import Models.MovieOut;
import Models.StarOut;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
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
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

@WebServlet("/api/star")
public class StarServlet extends HttpServlet {

	Gson gson = new Gson();

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long ts = 0;
		long tj1 = 0;
		long tj2 = 0;
		
		long startTime = System.nanoTime();
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
		response.setContentType("application/json"); // Response mime type
		PrintWriter out = response.getWriter();
		try {
			long tjstart = System.nanoTime();
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
			PreparedStatement statement = null;
			
			String starId = request.getParameter("starId");
			List<StarOut> starOutList = new ArrayList<StarOut>();
			StarOut so = new StarOut();

			String query = "select s.name as name, s.birthYear as year from stars s where s.id = ?;";
			statement = dbcon.prepareStatement(query);
			statement.setString(1, starId);
			ResultSet rs = statement.executeQuery();
			long tjend = System.nanoTime();
			tj1 = tjend - tjstart;
			
			while(rs.next()) {
				so.setName(rs.getString("name"));
				so.setBirth(rs.getString("year"));
			}
			
			String query2 = "select m.id as id, m.title as title from movies m, stars_in_movies st "
					+ "where st.movieId = m.id and st.starsId = ? order by m.title;";
			PreparedStatement statement2 = null;
			tjstart = System.nanoTime();
			statement2 = dbcon.prepareStatement(query2);
			statement2.setString(1, starId);
			ResultSet rs2 = statement2.executeQuery();
			tjend = System.nanoTime();
			tj2 = tjend - tjstart;
			while (rs2.next()) {
				so.addMovieId(rs2.getString("id"));
				so.addMovieNames(rs2.getString("title"));
			}

			rs.close();
			rs2.close();
			statement.close();
			statement2.close();
			dbcon.close();

			starOutList.add(so);
			String result = gson.toJson(starOutList);
			out.write(result.toString());
			out.close();
			long endTime = System.nanoTime();
			ts = endTime - startTime;
			long tj = tj1 + tj2;
			FileWriter fw = new FileWriter("log.txt",true);
			BufferedWriter bw = new BufferedWriter(fw);
			String time = ts + " " + tj;
			bw.write(time);
			bw.newLine();
			bw.close();
			fw.close();
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
