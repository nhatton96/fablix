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

@WebServlet("/api/movie")
public class MovieServlet extends HttpServlet{
	String loginUser = "mytestuser";
    String loginPasswd = "mypassword";
    String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
	
	// Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        //response.setContentType("text/html"); // Response mime type
        response.setContentType("application/json"); // Response mime type
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("ACTION");
        
        try {
        	//Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        	if("LIST".equals(action)){
        		int page = Integer.parseInt(request.getParameter("Page"));
        		int pageSize = Integer.parseInt(request.getParameter("PageSize"));
        		JsonArray movieList = GetMovieList(page, pageSize);
        		if (movieList != null) {
        			out.write(movieList.toString());
        	        response.setStatus(HttpServletResponse.SC_OK);
        	    }
        	    else {
        	        request.setAttribute("error", "Problem in MovieServlet");
        	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	    }
                
            }
        	
        }
        catch (java.lang.Exception ex) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
            return;
        }
        out.close();
    }
    
    private JsonArray GetMovieList(int page, int pageSize) {
    	try {	

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            Statement statement = dbcon.createStatement();
            
            String shiftAmount = Integer.toString(page * pageSize);
            String query = "SELECT r.*, m.*, gim.*, g.name AS genreName, sim.*, s.name AS starName FROM \r\n" + 
		            		"(SELECT * FROM ratings ORDER BY rating DESC LIMIT 20 OFFSET "+shiftAmount+") AS  r\r\n" + 
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

           
            List<MovieOut> movieOutList = new ArrayList<MovieOut>();
            JsonArray jsonArray = new JsonArray();
            
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
                    
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movieId", rs.getString("movieId"));
                    jsonObject.addProperty("title", rs.getString("title"));
                    jsonObject.addProperty("year", rs.getString("year"));
                    jsonObject.addProperty("director", rs.getString("director"));
                    jsonObject.addProperty("rating", rs.getString("rating"));
                    jsonObject.addProperty("genreName", rs.getString("genreName"));
                    jsonObject.addProperty("starName", rs.getString("starName"));
                    
                    jsonArray.add(jsonObject);

            	} 
            }
             
            rs.close();
            statement.close();
            dbcon.close();
            
            return jsonArray;
            
        } 
    	catch (SQLException ex) {
            while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            } // end while
            return new JsonArray();
        } // end catch SQLException
    }

}
