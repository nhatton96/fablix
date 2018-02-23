
import java.util.Random;

public class Film {

    private String id;

    private String title;

    private String director;

    private int year;

    private String categories;

    public Film(){
        Random rand = new Random();

        int  n = rand.nextInt(10000) + 1;
        id = "zz" + Integer.toString(n);
        title = "NULL";
        director = "NULL";
        categories = "Thriller";
        year = 1990;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getYear() {
        return year;
    }

    public String getCategories() {
        return categories;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public void addToCategories(String category){
        this.categories = category;
    }

}
