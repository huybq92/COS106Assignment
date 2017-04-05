package assignmentcos106;
import java.sql.*;

public class ConnectionMySQL {
    private static final String driver ="com.mysql.jdbc.Driver";
    static Connection conn = null;
    static Statement stmt = null;
    //establish connection
    static void getConnection(String url, String uname, String pword)
    {
        try
        {
            Class.forName(driver).newInstance();
            ConnectionMySQL.conn = DriverManager.getConnection(url,uname,pword);
            ConnectionMySQL.stmt = ConnectionMySQL.conn.createStatement();
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e)
        {
            System.err.println("ESTABLISHING CONNECTION error: " +e.getMessage());
        }
    }//End of getConnection
    
    //closeConnection
    static void closeConnection()
    {
        try {
            ConnectionMySQL.stmt.close();
            ConnectionMySQL.conn.close();
        } catch (SQLException ex) {
            System.err.println("CLOSING CONNECTION error: " +ex.getMessage());
        }
    }// End of closeConnection
}//Class ends
