import java.util.ArrayList;
import java.util.List;

public class Movies {

    private String director;

    private List films;

    public Movies(){
        films = new ArrayList();
    }

    public String getDirector(){
        return director;
    }

    public List getFilms() {
        return films;
    }

    public void setDirector(String director){
        this.director = director;
    }

    public void setFilms(List films) {
        this.films = films;
    }

    public void addFilm(Film film){
        this.films.add(film);
    }

}
