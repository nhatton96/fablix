package Controllers;

import Models.Customer;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class EmployeeService {

    //Databse info
    String loginUser = "mytestuser";
    String loginPasswd = "mypassword";
    String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

    public EmployeeService() {}

    public Customer find(String email, String password) {

        try {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/master");
            DataSource dss = (DataSource) envCtx.lookup("jdbc/slave");
            // the following commented lines are direct connections without pooling
            // Class.forName("org.gjt.mm.mysql.Driver");
            // Class.forName("com.mysql.jdbc.Driver").newInstance();
            // Connection dbcon = DriverManager.getConnection(loginUrl, loginUser,
            // loginPasswd);

            Connection dbcon = ds.getConnection();
            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "select * from employees where email = '"+email+"' and password = '"+password+"';";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            Customer customer = new Customer(rs.getString("email"), rs.getString("password"));

            rs.close();
            statement.close();
            dbcon.close();

            return customer;

        }
        catch (java.lang.Exception ex) {
            return new Customer();
        }
    }
}
