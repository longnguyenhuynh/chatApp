package Server;

import DBConnection.DBHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public
class DBconnection {
    private static DBHandler handler;

    public static
    String RetriveData(String username, String password) throws SQLException, ClassNotFoundException {
        //Retrive Data from Database
        Connection        connection = handler.getConnection();
        String q1 = "SELECT * from chatDB where username= ? and password= ?";
        PreparedStatement pst = connection.prepareStatement(q1);
        pst.setString(1, username);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();

        int count = 0;

        while (rs.next()) {
            count = count + 1;
        }
        connection.close();
        if (count == 1) {
            return "CORRECT";
        }
        return "FALSE";
    }

    public static
    String SavingData(String username, String password) throws SQLException, ClassNotFoundException {
        // Saving Data
        String insert = "INSERT INTO chatDB(username,password)" + "VALUES (?,?)";
        Connection connection = handler.getConnection();
        PreparedStatement pst = connection.prepareStatement(insert);

        pst.setString(1, username);
        pst.setString(2, password);

        pst.executeUpdate();
    }
}