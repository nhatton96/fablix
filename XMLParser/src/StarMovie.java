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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

class StarMovie {

	public static void main(String[] args) {

		ActorHandler act = new ActorHandler();
		act.runExample();
		act.addToDataBase();

		CastHandler cast = new CastHandler();
		cast.runExample();
		cast.addToDataBase();
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
		if (qName.equalsIgnoreCase("f")) {
			tempMo = new Movie();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("f")) {
			moli.add(tempMo);
			tempMo.setId(tempval);
			tempMo.setDir(tempDir);
		} else if (qName.equalsIgnoreCase("a")) {
			tempMo.setStar(tempval);
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

	private void printData() {

		System.out.println("No of movie '" + moli.size() + "'.");

		// Iterator it = myEmpls.iterator();
		Iterator it = moli.iterator();
		// while(it.hasNext()) {
		// System.out.println(it.next().toString());
		// }
	}

	public void runExample() {
		parseDocument();
		printData();
	}

	private void parseDocument() {

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
			PreparedStatement psInsertRecord2 = null;
			dbcon.setAutoCommit(false);
			String call = "{call add_star_id(?,?,?,?)}";
			String call2 = "{call add_star_id2(?,?,?,?)}";
			psInsertRecord = dbcon.prepareStatement(call);
			psInsertRecord2 = dbcon.prepareStatement(call2);
			int[] iNoRows = null;
			int[] iNoRows2 = null;

			String mid = " ";
			String tempmid = "  ";

			Iterator<Movie> iter = moli.iterator();
			while (iter.hasNext()) {
				Movie mo = iter.next();
				tempmid = mo.getId();

				if (mid.equals(tempmid)) {
					try {
						psInsertRecord2.setString(1, mo.getStar());
						psInsertRecord2.setString(2, mid);
						psInsertRecord2.setString(3, mo.getTITLE());
						psInsertRecord2.setString(4, mo.getDir());
						psInsertRecord2.addBatch();
					} catch (Exception e) {
						System.out.println(e);
					}
				} else {
					try {
						mid = tempmid;
						psInsertRecord.setString(1, mo.getStar());
						psInsertRecord.setString(2, mid);
						psInsertRecord.setString(3, mo.getTITLE());
						psInsertRecord.setString(4, mo.getDir());
						psInsertRecord.addBatch();
					} catch (Exception e) {
						System.out.println(e);
					}
				}

			}
			System.out.println("Execute batch");
			iNoRows = psInsertRecord.executeBatch();
			System.out.println("Commit 1 batch");
			dbcon.commit();
			System.out.println("Execute 2 batch");
			iNoRows2 = psInsertRecord2.executeBatch();
			System.out.println("Commit 2 batch");
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

	class Movie {
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
	List<Star> starList;
	Star star;

	public ActorHandler() {
		starList = new ArrayList<Star>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempval = "";
		if (qName.equalsIgnoreCase("actor")) {
			star = new Star();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (qName.equalsIgnoreCase("actor")) {
				starList.add(star);
			} else if (qName.equalsIgnoreCase("stagename")) {
				star.setName(tempval);
			} else if (qName.equalsIgnoreCase("dob")) {
				star.setBirth(tempval);
			}
		} catch (Exception e) {
			e.toString();
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		tempval = new String(ch, start, length);
	}

	private void parseDocument() {

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

	private void printData() {

		System.out.println("No of star '" + starList.size() + "'.");

		// Iterator it = myEmpls.iterator();
		Iterator it = starList.iterator();
		// while(it.hasNext()) {
		// System.out.println(it.next().toString());
		// }
	}

	public void runExample() {
		parseDocument();
		printData();
	}

	public void addToDataBase() {
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			PreparedStatement psInsertRecord = null;
			dbcon.setAutoCommit(false);
			String call = "{call add_star(?,?)}";
			psInsertRecord = dbcon.prepareStatement(call);
			int[] iNoRows = null;

			int by = 0;
			Iterator<Star> iter = starList.iterator();

			Statement statement = dbcon.createStatement();
			Star tempStar = iter.next();
			String query = "insert into stars(id, name, birthYear) " + "values(" + "'nn11111111', '"
					+ tempStar.getName() + "', " + tempStar.getBirth() + ");";
			statement.executeUpdate(query);
			statement.close();
			while (iter.hasNext()) {
				tempStar = iter.next();
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
			System.out.println("Execute batch");
			iNoRows = psInsertRecord.executeBatch();
			System.out.println("Commit batch");
			dbcon.commit();
			System.out.println("Done");
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

	class Star {
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