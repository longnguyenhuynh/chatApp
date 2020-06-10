package DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHandler extends Configs{

    public static
    Connection getConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + Configs.dbhost + ":" + Configs.dbport + "/" + Configs.dbname;
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(connectionString, Configs.dbuser, Configs.dbpass);
    }
}
