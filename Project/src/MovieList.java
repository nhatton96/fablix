import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MovieList extends HttpServlet{
	public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("text/html"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        out.println("<HTML><HEAD><TITLE>MovieDB</TITLE></HEAD>");
        out.println("<BODY><H1>MovieDB</H1>");

        try {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT r.*, m.*, gim.*, g.*, sim.*, s.* FROM \r\n" + 
		            		"(SELECT * FROM ratings ORDER BY rating DESC LIMIT 20) AS  r\r\n" + 
		            		"INNER JOIN movies AS m ON r.movieId = m.id\r\n" + 
		            		"INNER JOIN genres_in_movies gim \r\n" + 
		            		"ON gim.movieId = r.movieId\r\n" + 
		            		"INNER JOIN genres g \r\n" + 
		            		"ON gim.genreId = g.id\r\n" + 
		            		"INNER JOIN stars_in_movies sim\r\n" + 
		            		"ON sim.movieId = r.movieId\r\n" + 
		            		"INNER JOIN stars s\r\n" + 
		            		"ON sim.starsId = s.id;";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            out.println("<TABLE border>");

            // Iterate through each row of rs
            while (rs.next()) {
                String m_title = rs.getString("title");
                String m_year = rs.getString("year") + " " + rs.getString("name");
                String m_director = rs.getString("director");
                String m_list_of_genres = rs.getString("name");
                String m_list_of_stars = rs.getString("birthYear");
                String m_rating = rs.getString("birthYear");
                out.println("<tr>" + "<td>" + m_id + "</td>" + "<td>" + m_name + "</td>" + "<td>" + m_dob + "</td>"
                        + "</tr>");
            }

            out.println("</TABLE>");

            rs.close();
            statement.close();
            dbcon.close();
        } catch (SQLException ex) {
            while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            } // end while
        } // end catch SQLException

        catch (java.lang.Exception ex) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
            return;
        }
        out.close();
    }
}
