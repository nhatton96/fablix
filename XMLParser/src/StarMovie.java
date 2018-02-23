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

		try {
			File inputFile = new File("../XMLParser/XML/casts124.xml");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			CastHandler CastHandler = new CastHandler();
			saxParser.parse(inputFile, CastHandler);

			File inputFile2 = new File("../XMLParser/XML/actors63.xml");
			SAXParserFactory factory2 = SAXParserFactory.newInstance();
			SAXParser saxParser2 = factory2.newSAXParser();
			ActorHandler ActorHandler = new ActorHandler();
			saxParser2.parse(inputFile2, ActorHandler);

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
			int starLen = ActorHandler.actList.size();
			for (int i = 0; i < starLen - 1; ++i) {
				try {
					try {
						by = Integer.parseInt(ActorHandler.dobList.get(i));
					} catch (NumberFormatException e) {
						by = 2018;
					}
					psInsertRecord.setInt(1, by);
					psInsertRecord.setString(2, ActorHandler.actList.get(i));
					psInsertRecord.addBatch();
				} catch (Exception e) {
					System.out.println(e);
				}

			}
			iNoRows = psInsertRecord.executeBatch();
			dbcon.commit();

			PreparedStatement psInsertRecord2 = null;
			String call2 = "{call add_star_id(?,?,?,?)}";
			psInsertRecord2 = dbcon.prepareStatement(call2);
			int[] iNoRows2 = null;
			int starmovLen = CastHandler.fidList.size();
			for (int i = 0; i < starmovLen - 1; ++i) {
				try {
					psInsertRecord2.setString(1, CastHandler.starList.get(i));
					psInsertRecord2.setString(2, CastHandler.fidList.get(i));
					psInsertRecord2.setString(3, CastHandler.titleList.get(i));
					psInsertRecord2.setString(4, CastHandler.dirList.get(i));
					psInsertRecord2.addBatch();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			iNoRows2 = psInsertRecord2.executeBatch();
			dbcon.commit();
			dbcon.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class CastHandler extends DefaultHandler {

	boolean title = false;
	boolean dir = false;
	boolean fid = false;
	boolean star = false;
	String tempDir = "";
	List<String> fidList = new ArrayList<String>();
	List<String> starList = new ArrayList<String>();
	List<String> dirList = new ArrayList<String>();
	List<String> titleList = new ArrayList<String>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("f")) {
			fid = true;
		} else if (qName.equalsIgnoreCase("t")) {
			title = true;
		} else if (qName.equalsIgnoreCase("a")) {
			star = true;
		} else if (qName.equalsIgnoreCase("is")) {
			dir = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		if (fid) {
			// System.out.println("FID: " + new String(ch, start, length));
			fidList.add(new String(ch, start, length));
			dirList.add(tempDir);
			fid = false;
		} else if (star) {
			// System.out.println("Star: " + new String(ch, start, length));
			starList.add(new String(ch, start, length));
			star = false;
		} else if (dir) {
			// System.out.println("Star: " + new String(ch, start, length));
			tempDir = new String(ch, start, length);
			dir = false;
		} else if (title) {
			// System.out.println("Star: " + new String(ch, start, length));
			titleList.add(new String(ch, start, length));
			title = false;
		}
	}
}

class ActorHandler extends DefaultHandler {

	boolean actor = false;
	boolean dob = false;
	List<String> actList = new ArrayList<String>();
	List<String> dobList = new ArrayList<String>();
	HashMap<String, String> map = new HashMap<String, String>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("stagename")) {
			actor = true;
		} else if (qName.equalsIgnoreCase("dob")) {
			dob = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		if (actor) {
			// System.out.println("FID: " + new String(ch, start, length));
			actList.add(new String(ch, start, length));
			actor = false;
		} else if (dob) {
			dobList.add(new String(ch, start, length));
			dob = false;
		}
	}
}
