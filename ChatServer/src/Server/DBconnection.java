package Server;

import DBConnection.DBHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public
class DBconnection {

    public static
    String GetUserData(String username, String password) throws SQLException, ClassNotFoundException {
        Connection        connection = DBHandler.getConnection();
        String            q1         = "SELECT * FROM chatDB where username= ? and password= ?";
        PreparedStatement pst        = connection.prepareStatement(q1);
        pst.setString(1, username);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            connection.close();
            return "CORRECT";
        }
        connection.close();
        return "FALSE";
    }

    public static
    String SaveUserData(String username, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DBHandler.getConnection();
        // Check Same Username
        String            q1  = "SELECT * FROM chatDB where username= ?";
        PreparedStatement pst = connection.prepareStatement(q1);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return "FALSE";
        } else {
            // Saving Data
            String insert = "INSERT INTO chatDB(username,password)" + "VALUES (?,?)";
            pst = connection.prepareStatement(insert);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();
            connection.close();
            return "CORRECT";
        }
    }

    public static
    void SaveChatData(String from, String to, String messages) throws SQLException, ClassNotFoundException {
        Connection        connection = DBHandler.getConnection();
        String            q1         = "SELECT * FROM chatHistory where fromClient= ? and toClient= ?";
        PreparedStatement pst        = connection.prepareStatement(q1);
        pst.setString(1, from);
        pst.setString(2, to);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            q1 = "UPDATE chatHistory set messages=concat(messages,?)  where fromClient= ? and toClient= ?";
            pst = connection.prepareStatement(q1);
            pst.setString(1, "#" + messages);
            pst.setString(2, from);
            pst.setString(3, to);
            pst.executeUpdate();
            connection.close();
        } else {
            String insert = "INSERT INTO chatHistory(fromClient,toClient,messages)" + "VALUES (?,?,?)";
            pst = connection.prepareStatement(insert);
            pst.setString(1, from);
            pst.setString(2, to);
            pst.setString(3, messages);
            pst.executeUpdate();
            connection.close();
        }
    }

    public static
    String GetChatData(String from, String to) throws SQLException, ClassNotFoundException {
        Connection        connection = DBHandler.getConnection();
        String            q1         = "SELECT messages from chatHistory where fromClient= ? and toClient= ?";
        PreparedStatement pst        = connection.prepareStatement(q1);
        pst.setString(1, from);
        pst.setString(2, to);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            String str = rs.getString("messages");
            connection.close();
            return str;
        }
        connection.close();
        return null;
    }

    public static
    String GetGroupData() throws SQLException, ClassNotFoundException {
        Connection        connection = DBHandler.getConnection();
        String            q1         = "SELECT * from chatHistory where toClient= ?";
        PreparedStatement pst        = connection.prepareStatement(q1);
        pst.setString(1, "null");
        ResultSet     rs            = pst.executeQuery();
        StringBuilder stringBuilder = new StringBuilder();
        while (rs.next()) {
            stringBuilder.append(rs.getString("fromClient")).append("#");
        }
        connection.close();
        return stringBuilder.toString();
    }

    public static
    void ChangeGroupName(String newGroupName, String oldGroupName) throws SQLException, ClassNotFoundException {
        Connection        connection = DBHandler.getConnection();
        String            q1         = "UPDATE chatHistory set fromClient=?  where fromClient= ?";
        PreparedStatement pst        = connection.prepareStatement(q1);
        pst.setString(1, newGroupName);
        pst.setString(2, oldGroupName);
        pst.executeUpdate();
        connection.close();
    }
}