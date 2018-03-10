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
import java.util.StringTokenizer;

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

@WebServlet("/api/movie")
public class MovieServlet extends HttpServlet {
	String loginUser = "mytestuser";
	String loginPasswd = "mypassword";
	String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
	Gson gson = new Gson();

	// Use http GET
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// response.setContentType("text/html"); // Response mime type

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		String action = request.getParameter("ACTION");
		if (!action.equals("SUG"))
			response.setContentType("application/json"); // Response mime type
		try {
			// Class.forName("org.gjt.mm.mysql.Driver");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			int page = 0;
			int pageSize = 0;
			if (!"SINGLE".equals(action) && !"SUG".equals(action)) {
				page = Integer.parseInt(request.getParameter("Page"));
				pageSize = Integer.parseInt(request.getParameter("PageSize"));
			}
			String order = "";
			if ("cred".equals(action)) {
				String fn = request.getParameter("FirstName");
				String ln = request.getParameter("LastName");
				String card = request.getParameter("CardNumber");
				String add = request.getParameter("Address");
				String exp = request.getParameter("CardExpiration");
				String cart = "{cart :" + request.getParameter("Cart") + "}";
				JsonParser parser = new JsonParser();
				JsonObject jscartob = parser.parse(cart).getAsJsonObject();
				JsonArray jscartarray = jscartob.getAsJsonArray("cart");

				String movieList = checkout(fn, ln, add, card, exp, jscartarray);

				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			}
			if (!("SEARCHLIST".equals(action) || "SINGLE".equals(action)) && !("cred".equals(action))
					&& !("SUG".equals(action))) {
				order = request.getParameter("order");
				if (order.equals("ta"))
					order = "m2.title asc";
				else if (order.equals("td"))
					order = "m2.title desc";
				else if (order.equals("ya"))
					order = "m2.year asc";
				else if (order.equals("yd"))
					order = "m2.year desc";
			}
			if ("LIST".equals(action)) {
				String movieList = GetMovieList(page - 1, pageSize, order);

				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}

			} else if ("SEARCHADV".equals(action)) {
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
				String movieList = searchAdv(page - 1, pageSize, title, year, director, star, order);

				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else if ("SEARCHGENRE".equals(action)) {
				String genre = request.getParameter("genre");
				String movieList = searchGenre(page - 1, pageSize, genre, order);

				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else if ("SEARCH".equals(action) || "SEARCHTITLE".equals(action)) {
				String title = request.getParameter("title");
				String movieList;
				if ("SEARCH".equals(action))
					movieList = search(page - 1, pageSize, title, order, 1);
				else {
					title = title + "%";
					movieList = search(page - 1, pageSize, title, order, 0);
				}
				if (movieList != null) {
					out.write(movieList.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					request.setAttribute("error", "Problem in MovieServlet");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else if ("SEARCHLIST".equals(action)) {
				// put in json format
				String cart = "{cart :" + request.getParameter("Cart") + "}";
				JsonParser parser = new JsonParser();
				JsonObject jscartob = parser.parse(cart).getAsJsonObject();
				JsonArray jscartarray = jscartob.getAsJsonArray("cart");
				String movieList = searchList(page - 1, pageSize, jscartarray);

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
			} else {
				String kw = request.getParameter("keyWord");
				String suglist = findSug(kw);

				out.write(suglist);
				response.setStatus(HttpServletResponse.SC_OK);

			}

		} catch (java.lang.Exception ex) {
			out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
					+ "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
			return;
		}
		out.close();
	}

	private String GetMovieList(int page, int pageSize, String order) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();
			if (order.equals("tr"))
				order = "r2.rating desc";
			String shiftAmount = Integer.toString(page * pageSize);
			String query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, "
					+ "s.name as starName, s.id as stid, g.name as genreName, r.rating as rating "
					+ "from (select * from movies m2, ratings r2 where r2.movieId = m2.id" + " order by " + order
					+ " limit " + pageSize + " offset " + shiftAmount + ") "
					+ "as m left join genres_in_movies ge on ge.movieId = m.id left join genres g on g.id = ge.genreId "
					+ "left join ratings r on r.movieId = m.id left join stars_in_movies st on st.movieId = m.id "
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

	private String searchAdv(int page, int pageSize, String title, String year, String director, String star,
			String order) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String shiftAmount = Integer.toString(page * pageSize);

			String query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, s.name as starName, s.id as stid, g.name as genreName, r.rating as rating from "
					+ "(select distinct m2.id, m2.director, m2.year, m2.title from movies m2, stars_in_movies st2, stars s2 where s2.id = st2.starsId and st2.movieId = m2.id "
					+ "and s2.name like '%" + star + "%' " + "and m2.title like '%" + title + "%' "
					+ "and m2.year like '%" + year + "%' " + "and m2.director like '%" + director + "%' " + "order by "
					+ order + " limit " + pageSize + " offset " + shiftAmount + ") as m "
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

	private String searchGenre(int page, int pageSize, String genre, String order) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String shiftAmount = Integer.toString(page * pageSize);
			String query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, s.name as starName, s.id as stid, g.name as genreName, r.rating as rating "
					+ "from (select distinct m2.id, m2.director, m2.year, m2.title from movies m2, genres_in_movies ge2, genres g2 "
					+ "where ge2.movieId = m2.id and ge2.genreId = g2.id and g2.name = '" + genre + "' " + "order by "
					+ order + " limit " + pageSize + " offset " + shiftAmount + ") as m "
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

	private String search(int page, int pageSize, String title, String order, int truefalse) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String shiftAmount = Integer.toString(page * pageSize);
			String query = "";
			if (truefalse == 0) {
				query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, s.name as starName, s.id as stid, g.name as genreName, r.rating as rating "
						+ "from (select distinct m2.id, m2.director, m2.year, m2.title from movies m2 where m2.title like '"
						+ title + "' " + "order by " + order + " limit " + pageSize + " offset " + shiftAmount
						+ ") as m " + "left join genres_in_movies ge on ge.movieId = m.id "
						+ "left join genres g on g.id = ge.genreId " + "left join ratings r on r.movieId = m.id "
						+ "left join stars_in_movies st on st.movieId = m.id "
						+ "left join stars s on s.id = st.starsId";
			} else {
				StringTokenizer st = new StringTokenizer(title, ".,:!?' ");
				String regex = "(";
				String edrec = "(";
				String token = "";
				int len = 0;
				while (st.hasMoreTokens()) {
					token = st.nextToken().toLowerCase();
					len = Math.max(token.length() / 5, 1);
					regex += "m2.title REGEXP '[[:<:]]" + token + "'";
					edrec += "edrec('" + token + "',m2.title," + len + ") = 1";
					if (st.hasMoreTokens()) {
						regex += " and ";
						edrec += " and ";
					}
				}
				regex += ")";
				edrec += ")";
				query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, s.name as starName, s.id as stid, g.name as genreName, r.rating as rating "
						+ "from (select distinct m2.id, m2.director, m2.year, m2.title from movies m2 where " + regex
						+ " or " + edrec + " order by " + order + " limit " + pageSize + " offset " + shiftAmount
						+ ") as m " + "left join genres_in_movies ge on ge.movieId = m.id "
						+ "left join genres g on g.id = ge.genreId " + "left join ratings r on r.movieId = m.id "
						+ "left join stars_in_movies st on st.movieId = m.id "
						+ "left join stars s on s.id = st.starsId";

			}
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

	private String searchList(int page, int pageSize, JsonArray cart) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();

			String shiftAmount = Integer.toString(page * pageSize);

			String idList = "";
			int len = cart.size() - 1;
			for (int i = 0; i <= len; ++i) {
				JsonElement ce = cart.get(i);
				JsonObject co = ce.getAsJsonObject();
				String movieString = co.get("movieId").getAsString();
				idList += ("m2.id = '" + movieString + "'");
				if (i < len)
					idList += " or ";
			}

			String query = "select m.id as movieId, m.title as title, m.year as year, m.director as director, s.name as starName, s.id as stid, g.name as genreName, r.rating as rating from "
					+ "(select distinct m2.id, m2.director, m2.year, m2.title from movies m2 where (" + idList + ")"
					+ "order by m2.title limit " + pageSize + " offset " + shiftAmount + ") as m "
					+ "left join genres_in_movies ge on ge.movieId = m.id left join genres g on g.id = ge.genreId left join ratings r on r.movieId = m.id "
					+ "left join stars_in_movies st on st.movieId = m.id left join stars s on s.id = st.starsId;";

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

	private String findSug(String kw) {
		try {

			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Declare our statement
			Statement statement = dbcon.createStatement();
			StringTokenizer st = new StringTokenizer(kw, ".,:!?' ");
			String regexMovie = "(";
			String regexStar = "(";
			String token = "";
			String edrecMovie = "(";
			String edrecStar = "(";
			int len = 0;
			while (st.hasMoreTokens()) {
				token = st.nextToken().toLowerCase();
				len = Math.max(token.length() / 5, 1);
				regexMovie += "title REGEXP '[[:<:]]" + token + "'";
				regexStar += "name REGEXP '[[:<:]]" + token + "'";
				edrecMovie += "edrec('" + token + "',title," + len + ") = 1";
				edrecStar += "edrec('" + token + "',name," + len + ") = 1";
				if (st.hasMoreTokens()) {
					regexMovie += " and ";
					regexStar += " and ";
					edrecMovie += " and ";
					edrecStar += " and ";
				}
			}
			regexMovie += ")";
			regexStar += ")";
			edrecMovie += ")";
			edrecStar += ")";
			String query = "select * from movies where " + regexMovie + " or " + edrecMovie;

			// Perform the query
			ResultSet rs = statement.executeQuery(query);
			int count = 0;
			JsonArray jsonArray = new JsonArray();
			while (rs.next() && count < 10) {
				jsonArray.add(generateJsonObject(rs.getString("id"), rs.getString("title"), "movie"));
				++count;
			}
			if (count < 10) {
				Statement statement2 = dbcon.createStatement();
				String query2 = "select * from stars where " + regexStar + " or " + edrecStar;

				// Perform the query
				ResultSet rs2 = statement2.executeQuery(query2);
				while (rs2.next() && count < 10) {
					jsonArray.add(generateJsonObject(rs2.getString("id"), rs2.getString("name"), "star"));
					++count;
				}
				rs2.close();
				statement2.close();
			}
			rs.close();
			statement.close();

			dbcon.close();

			return jsonArray.toString();

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} // end while
			return new String();
		} // end catch SQLException
	}

	private static JsonObject generateJsonObject(String id, String name, String type) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", name);

		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", type);
		additionalDataJsonObject.addProperty("id", id);

		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}

	private String checkout(String fn, String ln, String add, String card, String exp, JsonArray cart) {
		try {
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();
			String query = "select ct.id as cusId from customers ct, creditcards cd where cd.id = ct.ccId and "
					+ "cd.id = '" + card + "' and ct.firstName = '" + fn + "' and ct.lastName = '" + ln
					+ "' and ct.address = '" + add + "' and cd.expiration = '" + exp + "';";
			ResultSet rs = statement.executeQuery(query);
			List<String> result = new ArrayList<String>();
			if (rs.next()) {
				result.add("yes");
				String cusId = rs.getString("cusId");
				String query2 = "insert into sales (customerId,movieId,salesDate) values";
				int len = cart.size();
				for (int i = 0; i < len; ++i) {
					JsonElement ce = cart.get(i);
					JsonObject co = ce.getAsJsonObject();
					String movieString = co.get("movieId").getAsString();
					String newquery = query2 + "('" + cusId + "', '" + movieString + "', CURDATE());";
					statement.executeUpdate(newquery);
				}
				statement.close();
			} else {
				result.add("no");
			}

			rs.close();
			statement.close();
			dbcon.close();

			return gson.toJson(result);

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} // end while
			return new String();
		}
	}

}