package DAO;

import java.sql.*;

/**
 *
 * @author Aryan Mehta
 */
public class UserDAO {

    private dbConfig db = new dbConfig();
    private PreparedStatement ps;
    private ResultSet rs;
    private Connection con = db.getDbConn();
    
    public int addUser(String username,String password,String email){
        try {
            ps = con.prepareStatement("SELECT * FROM userData WHERE username = ?");
            ps.setString(1, username);
            
            rs = ps.executeQuery();
            
            if(rs.next()){
                return 2;
            }else{
                //add the user 
                ps = con.prepareStatement("INSERT INTO userData VALUES(?,?,?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                
                return ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int verifyUser(String username,String password){
        try {
            ps = con.prepareStatement("SELECT * FROM userData WHERE username = ? and password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            
            rs = ps.executeQuery();
            if(rs.next()){
                return 1;
            }else{
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
