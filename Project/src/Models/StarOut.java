package Models;

import java.util.*;

public class StarOut {

	String name;
	String birthYear;
	List<String> movieNames = new ArrayList<String>();
	List<String> movieId = new ArrayList<String>();

	public void setName(String name) {
		this.name = name;
	}

	public void setBirth(String by) {
		this.birthYear = by;
	}

	public void addMovieNames(String mvName) {
		this.movieNames.add(mvName);
	}

	public void addMovieId(String mvId) {
		this.movieId.add(mvId);
	}
}
