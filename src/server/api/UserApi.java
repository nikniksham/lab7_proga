package server.api;

import java.security.MessageDigest;
import java.sql.*;

public class UserApi extends BaseApi {
    public static String register(String login, String user_password) {
        String result = "ok";
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
//            rs = stmt.executeQuery("");
            String queue = "insert into users (id, login, password, status) values (nextval('UserSeq'), '" + login + "', '" + user_password + "', 0)";
//            System.out.println(queue);
            stmt.executeUpdate(queue);
//            System.out.println(stmt.getResultSet());
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
            result = e.getMessage();
        } catch (Exception e) { // SQLException sqlEx
//            sqlEx.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e) { /*can't do anything */ }
            try { stmt.close(); } catch(Exception e) { /*can't do anything */ }
            try { rs.close(); } catch(Exception e) { /*can't do anything */ }
        }
        return result;
    }

    public static boolean login(String login, String user_password) {
        String query = "select users.password from users where users.login = '"+login+"' limit 1";
//        System.out.println(query);
        boolean res = false;
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
//                System.out.println(rs.getString(1) + " " + user_password);
                res = rs.getString(1).equals(user_password);
            }

        } catch (Exception e) { // SQLException sqlEx
//            sqlEx.printStackTrace();
            e.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e) { /*can't do anything */ }
            try { stmt.close(); } catch(Exception e) { /*can't do anything */ }
            try { rs.close(); } catch(Exception e) { /*can't do anything */ }
        }
        return res;
    }
}
