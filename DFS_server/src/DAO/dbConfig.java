package DAO;

import java.sql.*;

/**
 *
 * @author Aryan Mehta
 */
public class dbConfig {
    private Connection dbConn;
    
    public dbConfig(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/DFS","root","1234");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getDbConn() {
        return dbConn;
    }
}
