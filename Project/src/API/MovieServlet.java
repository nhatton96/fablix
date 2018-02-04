package API;

import Models.MovieOut;

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

@WebServlet("/api/movie")
public class MovieServlet extends HttpServlet {
	String loginUser = "mytestuser";
	String loginPasswd = "mypassword";
	String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
	Gson gson = new Gson();

	// Use http GET
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// response.setContentType("text/html"); // Response mime type
		response.setContentType("application/json"); // Response mime type
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		String action = request.getParameter("ACTION");

		try {
			// Class.forName("org.gjt.mm.mysql.Driver");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			if ("LIST".equals(action)) {
				int page = Integer.parseInt(request.getParameter("Page"));
				int pageSize = Integer.parseInt(request.getParameter("PageSize"));
				String movieList = GetMovieList(page - 1, pageSize);

				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}

			} else if ("SEARCHADV".equals(action)) {
				int page = Integer.parseInt(request.getParameter("Page"));
				int pageSize = Integer.parseInt(request.getParameter("PageSize"));
				String star = request.getParameter("star");
				if (star.equals("0"))
					star = "%";
				String year = request.getParameter("year");
				if (year.equals("0"))
					year = "%";
				String title = request.getParameter("title");
				if (title.equals("0"))
					title = "%";
				String director = request.getParameter("director");
				if (director.equals("0"))
					director = "%";
				String movieList = searchAdv(page - 1, pageSize, title, year, director, star);

				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else if ("SEARCHGENRE".equals(action)) {
				int page = Integer.parseInt(request.getParameter("Page"));
				int pageSize = Integer.parseInt(request.getParameter("PageSize"));
				String genre = request.getParameter("genre");
				String movieList = searchGenre(page - 1, pageSize, genre);

				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else if ("SEARCH".equals(action)) {
				int page = Integer.parseInt(request.getParameter("Page"));
				int pageSize = Integer.parseInt(request.getParameter("PageSize"));
				String title = request.getParameter("title");
				String movieList = search(page - 1, pageSize, title);

				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else if ("SINGLE".equals(action)) {
				String movieId = request.getParameter("MovieId");
				String movie = GetMovie(movieId);

				if (!movie.equals("")) {
					out.write(movie.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}

			}

		} catch (java.lang.Exception ex) {
			out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
					+ "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
			return;
		}
		out.close();
	}

	private String GetMovieList(int page, int pageSize) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String shiftAmount = Integer.toString(page * pageSize);
			String query = "SELECT r.*, m.*, gim.*, g.name AS genreName, sim.*, s.name AS starName, s.id as stid FROM \r\n"
					+ "(SELECT * FROM ratings ORDER BY rating DESC LIMIT " + pageSize + " OFFSET " + shiftAmount
					+ ") AS  r\r\n" + "INNER JOIN movies AS m ON r.movieId = m.id\r\n"
					+ "INNER JOIN genres_in_movies gim \r\n" + "ON gim.movieId = r.movieId\r\n"
					+ "INNER JOIN genres g \r\n" + "ON gim.genreId = g.id\r\n" + "INNER JOIN stars_in_movies sim\r\n"
					+ "ON sim.movieId = r.movieId\r\n" + "INNER JOIN stars s\r\n" + "ON sim.starsId = s.id;";

			// Perform the query
			ResultSet rs = statement.executeQuery(query);

			// Iterate through each row of rs
			List<MovieOut> movieOutList = new ArrayList<MovieOut>();

			while (rs.next()) {

				String check = rs.getString("movieId");
				boolean alreadyAdded = false;
				int index = 0;
				for (int i = 0; i < movieOutList.size(); i++) {
					String moId = movieOutList.get(i).getMovieId();
					if (moId.equals(check)) {
						alreadyAdded = true;
						index = i;
					}
				}

				if (alreadyAdded) {
					movieOutList.get(index).addGenre(rs.getString("genreName"));
					movieOutList.get(index).addStar(rs.getString("starName"));
					movieOutList.get(index).addStid(rs.getString("stid"));

				} else {
					MovieOut mo = new MovieOut();
					mo.setMovieId(rs.getString("movieId"));
					mo.setTitle(rs.getString("title"));
					mo.setYear(rs.getString("year"));
					mo.setDirector(rs.getString("director"));
					mo.setRating(rs.getString("rating"));
					mo.addGenre(rs.getString("genreName"));
					mo.addStar(rs.getString("starName"));
					mo.addStid(rs.getString("stid"));

					movieOutList.add(mo);
				}
			}

			rs.close();
			statement.close();
			dbcon.close();

			return gson.toJson(movieOutList);

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} // end while
			return new String();
		} // end catch SQLException
	}

	private String GetMovie(String movieId) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String query = "SELECT r.*, m.*, gim.*, g.name as genreName, sim.*, s.name as starName, s.id as stid FROM \n"
					+ "(SELECT * FROM ratings) AS  r\n" + "INNER JOIN movies AS m ON r.movieId = m.id\n"
					+ "INNER JOIN genres_in_movies gim \n" + "ON gim.movieId = r.movieId\n" + "INNER JOIN genres g \n"
					+ "ON gim.genreId = g.id\n" + "INNER JOIN stars_in_movies sim\n" + "ON sim.movieId = r.movieId\n"
					+ "INNER JOIN stars s\n" + "ON sim.starsId = s.id\n" + "WHERE m.id = \'" + movieId + "\';";

			// Perform the query
			ResultSet rs = statement.executeQuery(query);

			// Iterate through each row of rs
			List<MovieOut> movieOutList = new ArrayList<MovieOut>();

			while (rs.next()) {

				String check = rs.getString("movieId");
				boolean alreadyAdded = false;
				int index = 0;
				for (int i = 0; i < movieOutList.size(); i++) {
					String moId = movieOutList.get(i).getMovieId();
					if (moId.equals(check)) {
						alreadyAdded = true;
						index = i;
					}
				}

				if (alreadyAdded) {
					movieOutList.get(index).addGenre(rs.getString("genreName"));
					movieOutList.get(index).addStar(rs.getString("starName"));
					movieOutList.get(index).addStid(rs.getString("stid"));

				} else {
					MovieOut mo = new MovieOut();
					mo.setMovieId(rs.getString("movieId"));
					mo.setTitle(rs.getString("title"));
					mo.setYear(rs.getString("year"));
					mo.setDirector(rs.getString("director"));
					mo.setRating(rs.getString("rating"));
					mo.addGenre(rs.getString("genreName"));
					mo.addStar(rs.getString("starName"));
					mo.addStid(rs.getString("stid"));

					movieOutList.add(mo);
				}
			}

			rs.close();
			statement.close();
			dbcon.close();

			// JsonObject jsonObject = gson.toJson(movieOutList);
			return gson.toJson(movieOutList);

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} // end while
			return new String();
		} // end catch SQLException

	}

	private String searchAdv(int page, int pageSize, String title, String year, String director, String star) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String shiftAmount = Integer.toString(page * pageSize);

			String query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, s.name as starName, s.id as stid, g.name as genreName, r.rating as rating from "
					+ "(select distinct m2.id, m2.director, m2.year, m2.title from movies m2, stars_in_movies st2, stars s2 where s2.id = st2.starsId and st2.movieId = m2.id "
					+ "and s2.name like '" + star + "' " + "and m2.title like '" + title + "' " + "and m2.year like '"
					+ year + "' " + "and m2.director like '" + director + "' " + "order by m2.title limit " + pageSize
					+ " offset " + shiftAmount + ") as m " + "left join genres_in_movies ge on ge.movieId = m.id "
					+ "left join genres g on g.id = ge.genreId " + "left join ratings r on r.movieId = m.id "
					+ "left join stars_in_movies st on st.movieId = m.id " + "left join stars s on s.id = st.starsId";

			// Perform the query
			ResultSet rs = statement.executeQuery(query);

			// Iterate through each row of rs
			List<MovieOut> movieOutList = new ArrayList<MovieOut>();

			while (rs.next()) {

				String check = rs.getString("movieId");
				boolean alreadyAdded = false;
				int index = 0;
				for (int i = 0; i < movieOutList.size(); i++) {
					String moId = movieOutList.get(i).getMovieId();
					if (moId.equals(check)) {
						alreadyAdded = true;
						index = i;
					}
				}

				if (alreadyAdded) {
					movieOutList.get(index).addGenre(rs.getString("genreName"));
					movieOutList.get(index).addStar(rs.getString("starName"));
					movieOutList.get(index).addStid(rs.getString("stid"));

				} else {
					MovieOut mo = new MovieOut();
					mo.setMovieId(rs.getString("movieId"));
					mo.setTitle(rs.getString("title"));
					mo.setYear(rs.getString("year"));
					mo.setDirector(rs.getString("director"));
					mo.setRating(rs.getString("rating"));
					mo.addGenre(rs.getString("genreName"));
					mo.addStar(rs.getString("starName"));
					mo.addStid(rs.getString("stid"));

					movieOutList.add(mo);
				}
			}

			rs.close();
			statement.close();
			dbcon.close();

			return gson.toJson(movieOutList);

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} // end while
			return new String();
		} // end catch SQLException
	}

	private String searchGenre(int page, int pageSize, String genre) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String shiftAmount = Integer.toString(page * pageSize);
			String query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, s.name as starName, s.id as stid, g.name as genreName, r.rating as rating "
					+ "from (select distinct m2.id, m2.director, m2.year, m2.title from movies m2, genres_in_movies ge2, genres g2 "
					+ "where ge2.movieId = m2.id and ge2.genreId = g2.id and g2.name = '" + genre + "' "
					+ "order by m2.title limit " + pageSize + " offset " + shiftAmount + ") as m "
					+ "left join genres_in_movies ge on ge.movieId = m.id " + "left join genres g on g.id = ge.genreId "
					+ "left join ratings r on r.movieId = m.id " + "left join stars_in_movies st on st.movieId = m.id "
					+ "left join stars s on s.id = st.starsId";

			// Perform the query
			ResultSet rs = statement.executeQuery(query);

			// Iterate through each row of rs
			List<MovieOut> movieOutList = new ArrayList<MovieOut>();

			while (rs.next()) {

				String check = rs.getString("movieId");
				boolean alreadyAdded = false;
				int index = 0;
				for (int i = 0; i < movieOutList.size(); i++) {
					String moId = movieOutList.get(i).getMovieId();
					if (moId.equals(check)) {
						alreadyAdded = true;
						index = i;
					}
				}

				if (alreadyAdded) {
					movieOutList.get(index).addGenre(rs.getString("genreName"));
					movieOutList.get(index).addStar(rs.getString("starName"));
					movieOutList.get(index).addStid(rs.getString("stid"));

				} else {
					MovieOut mo = new MovieOut();
					mo.setMovieId(rs.getString("movieId"));
					mo.setTitle(rs.getString("title"));
					mo.setYear(rs.getString("year"));
					mo.setDirector(rs.getString("director"));
					mo.setRating(rs.getString("rating"));
					mo.addGenre(rs.getString("genreName"));
					mo.addStar(rs.getString("starName"));
					mo.addStid(rs.getString("stid"));

					movieOutList.add(mo);
				}
			}

			rs.close();
			statement.close();
			dbcon.close();

			return gson.toJson(movieOutList);

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} // end while
			return new String();
		} // end catch SQLException
	}

	private String search(int page, int pageSize, String title) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String shiftAmount = Integer.toString(page * pageSize);
			String query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, s.name as starName, s.id as stid, g.name as genreName, r.rating as rating "
					+ "from (select distinct m2.id, m2.director, m2.year, m2.title from movies m2 where m2.title like '%"
					+ title + "%' " + "order by m2.title limit " + pageSize + " offset " + shiftAmount + ") as m "
					+ "left join genres_in_movies ge on ge.movieId = m.id " + "left join genres g on g.id = ge.genreId "
					+ "left join ratings r on r.movieId = m.id " + "left join stars_in_movies st on st.movieId = m.id "
					+ "left join stars s on s.id = st.starsId";

			// Perform the query
			ResultSet rs = statement.executeQuery(query);

			// Iterate through each row of rs
			List<MovieOut> movieOutList = new ArrayList<MovieOut>();

			while (rs.next()) {

				String check = rs.getString("movieId");
				boolean alreadyAdded = false;
				int index = 0;
				for (int i = 0; i < movieOutList.size(); i++) {
					String moId = movieOutList.get(i).getMovieId();
					if (moId.equals(check)) {
						alreadyAdded = true;
						index = i;
					}
				}

				if (alreadyAdded) {
					movieOutList.get(index).addGenre(rs.getString("genreName"));
					movieOutList.get(index).addStar(rs.getString("starName"));
					movieOutList.get(index).addStid(rs.getString("stid"));

				} else {
					MovieOut mo = new MovieOut();
					mo.setMovieId(rs.getString("movieId"));
					mo.setTitle(rs.getString("title"));
					mo.setYear(rs.getString("year"));
					mo.setDirector(rs.getString("director"));
					mo.setRating(rs.getString("rating"));
					mo.addGenre(rs.getString("genreName"));
					mo.addStar(rs.getString("starName"));
					mo.addStid(rs.getString("stid"));

					movieOutList.add(mo);
				}
			}

			rs.close();
			statement.close();
			dbcon.close();

			return gson.toJson(movieOutList);

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} // end while
			return new String();
		} // end catch SQLException
	}
}