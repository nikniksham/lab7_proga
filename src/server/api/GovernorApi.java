package server.api;

import my_programm.enums.Climate;
import my_programm.enums.StandardOfLiving;
import my_programm.obj.City;
import my_programm.obj.Human;

import javax.swing.plaf.PanelUI;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

public class GovernorApi extends BaseApi {
    public static Human get_governor(int governor_id) {
        String query = "select * from governor where governor.id = " + governor_id + ";";
        Human governor = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
//            stmt.executeUpdate(query2);
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                Timestamp birthdate = rs.getTimestamp(3);
                governor = new Human(governor_id, birthdate, name);
            }

        } catch (Exception e) { // SQLException sqlEx
//            sqlEx.printStackTrace();
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception e) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (Exception e) { /*can't do anything */ }
            try {
                rs.close();
            } catch (Exception e) { /*can't do anything */ }
        }

        return governor;

    }

    public static int get_next_id() {
        String query = "SELECT nextval('GoverSeq');";
        int id = 0;
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt(1);
            }

        } catch (Exception e) { // SQLException sqlEx
//            sqlEx.printStackTrace();
            e.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e) { /*can't do anything */ }
            try { stmt.close(); } catch(Exception e) { /*can't do anything */ }
            try { rs.close(); } catch(Exception e) { /*can't do anything */ }
        }
        return id;
    }

    public static String saveOrUpdate(Human gov) {
        String result = "ok";
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from governor where governor.id = " + gov.getId()+";");
            if (rs.next()) {
                stmt.executeUpdate("update governor set id = "+ gov.getId()+", name = '"+gov.getName()+"', birthday = '"+gov.getBirthday()+"' where governor.id = 1;");
            }
            stmt.executeUpdate("insert into governor (id, name, birthday) values ("+gov.getId()+", '"+gov.getName()+"', "+gov.getBirthday());
        } catch (Exception e) { // SQLException sqlEx
//            sqlEx.printStackTrace();
            e.printStackTrace();
            result = e.getMessage();
        } finally {
            try { con.close(); } catch(Exception e) { /*can't do anything */ }
            try { stmt.close(); } catch(Exception e) { /*can't do anything */ }
            try { rs.close(); } catch(Exception e) { /*can't do anything */ }
        }
        return result;
    }
}
