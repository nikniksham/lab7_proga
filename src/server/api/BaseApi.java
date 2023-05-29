package server.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class BaseApi {
    protected static String user = "postgres";
    protected static String password = "root";
    protected static String url = "jdbc:postgresql://localhost:5432/superpuper";
    protected static Connection con;
    protected static Statement stmt;
    protected static ResultSet rs;

    public static void clear() {

        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            stmt.executeUpdate("ALTER SEQUENCE CitySeq RESTART;");
            stmt.executeUpdate("ALTER SEQUENCE GoverSeq RESTART;");
            stmt.executeUpdate("ALTER SEQUENCE GoverSeq RESTART;");
            stmt.executeUpdate("DELETE FROM governor;");
            stmt.executeUpdate("DELETE FROM city;");

        } catch (Exception e) { // SQLException sqlEx
//            sqlEx.printStackTrace();
            e.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e) { /*can't do anything */ }
            try { stmt.close(); } catch(Exception e) { /*can't do anything */ }
            try { rs.close(); } catch(Exception e) { /*can't do anything */ }
        }
    }

}
