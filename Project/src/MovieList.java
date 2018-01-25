import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/m")
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

        out.println("<HTML><HEAD>"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/MovieList.css\">"
        		+ "<TITLE>MovieDB</TITLE></HEAD>");
        out.println("<BODY><H1 class=\"header-center\">Movie List</H1>");

        try {
        	//render css
        	
        			
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT r.*, m.*, gim.*, g.name AS genreName, sim.*, s.name AS starName FROM \r\n" + 
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

            out.println("<TABLE border align=\"center\"; bgcolor:\"#FFFFF\">");
            
            
            //titles
            out.println("<tr id=\"listTitles\"  bgcolor=\"#2DCA67\">" + 
	            		"<td>Title</td>" + 
	        			"<td>Year</td>" + 
	        			"<td>Director</td>"+
	        			"<td>Genres</td>"+
	        			"<td>Stars</td>"+
	        			"<td>Rating</td>"+
	        			"</tr>");
            // Iterate through each row of rs
            List<MovieOut> movieOutList = new ArrayList<MovieOut>();
            
            while (rs.next()) {
            	
            	String check = rs.getString("movieId");
            	boolean alreadyAdded = false;
            	int index = 0;
            	for(int i = 0; i < movieOutList.size(); i++) {
                	String moId = movieOutList.get(i).getMovieId();
                	if(moId.equals(check)) {
                		alreadyAdded = true;
                    	index = i;
                	}	
                }
            	
            	if(alreadyAdded) {
            		movieOutList.get(index)
            		.addGenre(rs.getString("genreName"));
            		
            		movieOutList.get(index)
        			.addStar(rs.getString("starName"));
            		
            	}
            	else {
            		MovieOut mo = new MovieOut();
                	mo.setMovieId(rs.getString("movieId"));
                    mo.setTitle(rs.getString("title"));
                    mo.setYear(rs.getString("year"));
                    mo.setDirector(rs.getString("director"));
                    mo.setRating(rs.getString("rating"));
                    mo.addGenre(rs.getString("genreName"));
                    mo.addStar(rs.getString("starName"));
                    
                    movieOutList.add(mo);
            	}          	
            }
            
            //print table
            for(int i = 0; i < movieOutList.size(); i++) {
            	MovieOut mo = movieOutList.get(i);
            	out.println("<tr id=\"rcorners1\">" + 
            			"<td>" + mo.getTitle() + "</td>" + 
            			"<td>" + mo.getYear() + "</td>" + 
            			"<td>" + mo.getDirector() + "</td>"+
            			"<td>" + mo.getListOfGenres() + "</td>"+
            			"<td>" + mo.getListOfStars() + "</td>"+
            			"<td>" + mo.getRating() + "</td>"+
            			"</tr>");
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
