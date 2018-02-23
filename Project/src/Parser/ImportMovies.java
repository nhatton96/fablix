package Parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import Models.MovieOut;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@WebServlet("/Parser/Movies")
public class ImportMovies extends HttpServlet {
    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Importer spe = new Importer();
        spe.runExample();
        spe.addToDataBase();
    }
}

class Importer extends DefaultHandler {

    List myEmpls;
    List<Movies> myMovies;

    private String tempVal;

    //to maintain context
    private Movies tempMovies;
    private Film tempFilm;
    //private Employee tempEmp;


    public Importer(){
        myEmpls = new ArrayList();
        myMovies = new ArrayList<Movies>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("../Project/XML/mains243.xml", this);

        }catch(SAXException se) {
            se.printStackTrace();
        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData(){

        System.out.println("No of Movies '" + myMovies.size() + "'.");

        //Iterator it = myEmpls.iterator();
        Iterator it = myMovies.iterator();
        // while(it.hasNext()) {
        //System.out.println(it.next().toString());
        // }
    }


    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if(qName.equalsIgnoreCase("directorfilms")) {
            tempMovies = new Movies();
        }
        else if(qName.equalsIgnoreCase("film")){
            tempFilm = new Film();
        }
    }


    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch,start,length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try{
            if(qName.equalsIgnoreCase("directorfilms")) {
                //add it to the list
                myMovies.add(tempMovies);
            }
            else if(qName.equalsIgnoreCase ("dirname")) {
                tempMovies.setDirector(tempVal);
            }else if(qName.equalsIgnoreCase ("film")) {
                tempMovies.addFilm(tempFilm);
            }else if (qName.equalsIgnoreCase("fid")) {
                tempFilm.setId(tempVal);
            }else if (qName.equalsIgnoreCase("t")) {
                tempFilm.setTitle(tempVal);
            }else if (qName.equalsIgnoreCase("year")) {
                tempFilm.setYear(Integer.parseInt(tempVal));
            }else if (qName.equalsIgnoreCase("dirn")) {
                tempFilm.setDirector(tempVal);
            }else if (qName.equalsIgnoreCase("year")) {
                tempFilm.setYear(Integer.parseInt(tempVal));
            }else if (qName.equalsIgnoreCase("cat")) {
                if(tempVal.equals("Dram")){
                    tempVal = "Drama";
                }
                else if (tempVal.equals("BioP")){
                    tempVal = "Biography";
                }
                else if (tempVal.equals("Romt")){
                    tempVal = "Romance";
                }
                else if (tempVal.equals("West")){
                    tempVal = "Western";
                }
                else if (tempVal.equals("Musc")){
                    tempVal = "Musical";
                }
                else if (tempVal.equals("Advt")){
                    tempVal = "Adventure";
                }
                else if (tempVal.equals("Comd")){
                    tempVal = "Comedy";
                }
                else if (tempVal.equals("Horr")){
                    tempVal = "Horror";
                }
                else if (tempVal.length() == 0){
                    tempVal = "Thriller";
                }

                tempFilm.setCategories(tempVal);
            }
        }catch (Exception e){
            e.toString();
        }


    }
    public void addToDataBase(){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        try{
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            Statement statement = dbcon.createStatement();
            PreparedStatement psInsertRecord=null;
            String sqlInsertRecord=null;
            int[] iNoRows=null;

            dbcon.setAutoCommit(false);
            String call = "{call add_movie_with_genre(?,?,?,?,?)}";
            psInsertRecord=dbcon.prepareStatement(call);
            //Iterator it = myEmpls.iterator();
            Iterator<Movies> it = myMovies.iterator();
            while(it.hasNext()) {
                //System.out.println(it.next().toString());
                Movies movie = it.next();
                Iterator<Film> films = movie.getFilms().iterator();
                while(films.hasNext()){
                    Film film = films.next();

                    if(film.getCategories().equals("NULL")){
                        System.out.println("The following film id "+film.getId()+"has Null genre attribute");
                    }else if(film.getDirector().equals("NULL")){
                        System.out.println("The following film id "+film.getId()+"has Null director attribute");
                    }else if(film.getTitle().equals("NULL")){
                        System.out.println("The following film id "+film.getId()+"has Null title attribute");
                    }

                    try {
                        psInsertRecord.setString(1, film.getId());
                        psInsertRecord.setString(2, film.getTitle());
                        psInsertRecord.setInt(3, film.getYear());
                        psInsertRecord.setString(4, film.getDirector());
                        psInsertRecord.setString(5, film.getCategories());
                        psInsertRecord.addBatch();
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }
            }
            iNoRows=psInsertRecord.executeBatch();
            dbcon.commit();
            dbcon.close();
            //String query = "CALL add_movie()";

            // Perform the query
            //ResultSet rs = statement.executeQuery(query);
            //System.out.println(rs);
        } catch (SQLException ex){
            while(ex!=null){
                System.out.println("SQL Exception:  "+ex.getMessage());
                ex=ex.getNextException();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}

class Film {

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

class Movies {

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

