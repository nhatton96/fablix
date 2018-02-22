

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class Importer extends DefaultHandler{

    List myEmpls;
    List<Movies> myMovies;

    private String tempVal;

    //to maintain context
    private Movies tempMovies;
    private Film tempFilm;
    private Employee tempEmp;


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
            sp.parse("../XMLParser/XML/mains243.xml", this);

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

            //Iterator it = myEmpls.iterator();
            Iterator<Movies> it = myMovies.iterator();
            while(it.hasNext()) {
                //System.out.println(it.next().toString());
                Movies movie = it.next();
                Iterator<Film> films = movie.getFilms().iterator();
                String call = "{call add_movie_with_genre(?,?,?,?,?)}";
                while(films.hasNext()){
                    Film film = films.next();
                    try (CallableStatement stmt = dbcon.prepareCall(call)) {
                        if(film.getCategories().equals(""))
                            film.setCategories("Thriller");
                        stmt.setString(1, film.getId());
                        stmt.setString(2, film.getTitle());
                        stmt.setInt(3, film.getYear());
                        stmt.setString(4, film.getDirector());
                        stmt.setString(5, film.getCategories());
                        stmt.execute();
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }
            }
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


    public static void main(String[] args){
        Importer spe = new Importer();
        spe.runExample();
        spe.addToDataBase();
    }

}



