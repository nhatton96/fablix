import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.xml.parsers.ParserConfigurationException;

class StarMovie {

	public static void main(String[] args) {
		CastHandler cast = new CastHandler();
        cast.parseDocument();
        cast.addToDataBase();
		
        ActorHandler act = new ActorHandler();
        act.parseDocument();
        act.addToDataBase();
		
		
	}
}

class CastHandler extends DefaultHandler {
	String tempDir = "";
	String tempval;
	List<Movie> moli = new ArrayList<Movie>();
	Movie tempMo;
	

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempval = "";
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("f")) {
			tempMo = new Movie();
			tempMo.setId(tempval);
			tempMo.setDir(tempDir);
		} else if (qName.equalsIgnoreCase("a")) {
			tempMo.setStar(tempval);
			moli.add(tempMo);
		} else if (qName.equalsIgnoreCase("is")) {
			tempDir = tempval;
		} else if (qName.equalsIgnoreCase("t")) {
			tempMo.setTitle(tempval);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		tempval = new String(ch, start, length);
	}
	
	public void parseDocument() {

		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			sp.parse("../XMLParser/XML/casts124.xml", this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public void addToDataBase() {
		try {
			String loginUser = "mytestuser";
			String loginPasswd = "mypassword";
			String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			PreparedStatement psInsertRecord = null;
			dbcon.setAutoCommit(false);
			String call = "{call add_star_id(?,?,?,?)}";
			psInsertRecord = dbcon.prepareStatement(call);
			int[] iNoRows = null;

			Iterator<Movie> iter = moli.iterator();
			while (iter.hasNext()) {
				Movie mo = iter.next();
				try {
					psInsertRecord.setString(1,mo.getStar());
					psInsertRecord.setString(2, mo.getId());
					psInsertRecord.setString(3, mo.getTITLE());
					psInsertRecord.setString(4, mo.getDir());
					psInsertRecord.addBatch();
				} catch (Exception e) {
					System.out.println(e);
				}

			}
			iNoRows = psInsertRecord.executeBatch();
			dbcon.commit();
			dbcon.close();

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	class Movie{
		String id;
		String dir;
		String star;
		String title;

		public void setId(String ID) {
			this.id = ID;
		}

		public void setDir(String DIR) {
			this.dir = DIR;
		}

		public void setStar(String STAR) {
			this.star = STAR;
		}
		
		public void setTitle(String TITLE) {
			this.title = TITLE;
		}
		public String getDir() {
			return this.dir;
		}

		public String getStar() {
			return this.star;
		}
		public String getId() {
			return this.id;
		}

		public String getTITLE() {
			return this.title;
		}
	}
}

class ActorHandler extends DefaultHandler {

	String tempval;
	List<Star> starList = new ArrayList<Star>();
	Star star;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempval = "";
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (qName.equalsIgnoreCase("stagename")) {
				star = new Star();
				star.setName(tempval);
			} else if (qName.equalsIgnoreCase("dob")) {
				star.setBirth(tempval);
				starList.add(star);
			}
		} catch (Exception e) {
			e.toString();
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		tempval = new String(ch, start, length);
	}

	public void parseDocument() {

		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			sp.parse("../XMLParser/XML/actors63.xml", this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public void addToDataBase() {
		try {
			String loginUser = "mytestuser";
			String loginPasswd = "mypassword";
			String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			PreparedStatement psInsertRecord = null;
			dbcon.setAutoCommit(false);
			String call = "{call add_star(?,?)}";
			psInsertRecord = dbcon.prepareStatement(call);
			int[] iNoRows = null;

			int by = 0;
			Iterator<Star> iter = starList.iterator();
			while(iter.hasNext()) {
				Star tempStar = iter.next();
				try {
					try {
						by = Integer.parseInt(tempStar.getBirth());
					} catch (NumberFormatException e) {
						by = 2018;
					}
					psInsertRecord.setInt(1, by);
					psInsertRecord.setString(2, tempStar.getName());
					psInsertRecord.addBatch();
				} catch (Exception e) {
					System.out.println(e);
				}

			}
			iNoRows = psInsertRecord.executeBatch();
			dbcon.commit();
			dbcon.close();

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	class Star{
		String name;
		String birthYear;

		public void setName(String name) {
			this.name = name;
		}

		public void setBirth(String by) {
			this.birthYear = by;
		}

		public String getName() {
			return this.name;
		}

		public String getBirth() {
			return this.birthYear;
		}
	}
}
