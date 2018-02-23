import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.HashMap;
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
	List<String> fidList = new ArrayList<String>();
	List<String> starList = new ArrayList<String>();
	List<String> dirList = new ArrayList<String>();
	List<String> titleList = new ArrayList<String>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempval = "";
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("f")) {
			fidList.add(tempval);
			dirList.add(tempDir);
		} else if (qName.equalsIgnoreCase("a")) {
			starList.add(tempval);
		} else if (qName.equalsIgnoreCase("is")) {
			tempDir = tempval;
		} else if (qName.equalsIgnoreCase("t")) {
			titleList.add(tempval);
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

			int starmovLen = fidList.size();
			for (int i = 0; i < starmovLen - 1; ++i) {
				try {
					psInsertRecord.setString(1, starList.get(i));
					psInsertRecord.setString(2, fidList.get(i));
					psInsertRecord.setString(3, titleList.get(i));
					psInsertRecord.setString(4, dirList.get(i));
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
}

class ActorHandler extends DefaultHandler {

	boolean actor = false;
	boolean dob = false;
	String tempval;
	List<String> actList = new ArrayList<String>();
	List<String> dobList = new ArrayList<String>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		tempval = "";
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (qName.equalsIgnoreCase("stagename")) {
				actList.add(tempval);
			} else if (qName.equalsIgnoreCase("dob")) {
				dobList.add(tempval);
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
			int starLen = actList.size();
			for (int i = 0; i < starLen - 1; ++i) {
				try {
					try {
						by = Integer.parseInt(dobList.get(i));
					} catch (NumberFormatException e) {
						by = 2018;
					}
					psInsertRecord.setInt(1, by);
					psInsertRecord.setString(2, actList.get(i));
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
}
