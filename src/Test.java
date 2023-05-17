import java.security.MessageDigest;
import java.sql.*;

public class Test {
    private static String user = "postgres";
    private static String password = "root";
    private static String url = "jdbc:postgresql://localhost:5432/superpuper";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public static void main(String[] args) {
        System.out.println(sha256("test123"));
        System.out.println(sha256("test123"));
        String query = "select * from users";
        String query2 = "insert into users (login, password, last_ip, status) values ('niknik', '123456', '---', 0)";

        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
//            stmt.executeUpdate(query2);
            while (rs.next()) {
                String name = rs.getString(1);
                String password = rs.getString(2);
                String last_ip = rs.getString(3);
                int status = rs.getInt(4);
                System.out.println(name + " " + last_ip + " " + password + " " + status);
            }

        } catch (Exception e) { // SQLException sqlEx
//            sqlEx.printStackTrace();
            e.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e) { /*can't do anything */ }
            try { stmt.close(); } catch(Exception e) { /*can't do anything */ }
            try { rs.close(); } catch(Exception e) { /*can't do anything */ }
        }
    }

    public static String sha256(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}