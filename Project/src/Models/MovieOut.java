package Models;
import java.util.*;

public class MovieOut {
	String movieId;
	String title;
	String director;
	String year;
	List<String> list_of_genres = new ArrayList<String>();
	List<String> list_of_stars = new ArrayList<String>();
	String rating;
	
	//set functions
	public void setMovieId(String id) {
		this.movieId = id;
	}
	
	public void setTitle(String t) {
		this.title = t;
	}
	
	public void setDirector(String d) {
		this.director = d;
	}
	
	public void setYear(String y) {
		this.year = y;
	}
	
	public void setListOfGenres(List<String> log) {
		this.list_of_genres = log;
	}
	
	public void setListStars(List<String> los) {
		this.list_of_stars = los;
	}
	
	public void setRating(String r) {
		this.rating = r;
	}
	
	//get functions
	public String getMovieId() {
		return this.movieId;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDirector() {
		return this.director;
	}
	
	public String getYear() {
		return this.year;
	}
	
	public List<String> getListOfGenres(){
		return this.list_of_genres;
	}
	
	public List<String> getListOfStars(){
		return list_of_stars;
	}
	
	public String getRating() {
		return rating;
	}
	
	//public funcions
	public void addGenre(String g) {
		if(!list_of_genres.contains(g))
			list_of_genres.add(g);
	}
	
	public void addStar(String s) {
		if(!list_of_stars.contains(s))
			list_of_stars.add(s);
	}
	
}
