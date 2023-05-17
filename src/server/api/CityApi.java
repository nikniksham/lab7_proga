package server.api;

import my_programm.enums.Climate;
import my_programm.enums.StandardOfLiving;
import my_programm.obj.City;
import my_programm.obj.Human;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Map;

public class CityApi extends BaseApi {
    public static Hashtable<Integer, City> readTable() {
        Hashtable<Integer, City> table = new Hashtable<Integer, City>();
        String query = "select * from city";

        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String coordinates = rs.getString(3);
                Timestamp time = rs.getTimestamp(4);
                long area = rs.getLong(5);
                long population = rs.getLong(6);
                int meterAboveSeaLevel = rs.getInt(7);
                int carCode = rs.getInt(8);

                Integer climate_id = rs.getInt(9);
                Climate climate = null;
                if (climate_id != null) {
                    climate = Climate.getById(climate_id);
                }

                Integer standardOfLiving_id = rs.getInt(10);
                StandardOfLiving standardOfLiving = null;
                if (standardOfLiving_id != null) {
                    standardOfLiving = StandardOfLiving.getById(standardOfLiving_id);
                }

                Integer governor_id = rs.getInt(11);
                Human governor = null;
                if (governor_id != null) {
                    governor = GovernorApi.get_governor(governor_id);
                }

                Integer creator_id = rs.getInt(12);
                table.put(id, new City(id, name, coordinates, area, population, meterAboveSeaLevel, carCode, climate, standardOfLiving, governor, creator_id));

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

        return table;

    }

    public static void saveTable(Hashtable<Integer, City> table) {
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            int CREATOR = 1;

            for (Map.Entry<Integer,City> entry : table.entrySet()) {
                // getKey() getValue()
                City city = entry.getValue();
                rs = stmt.executeQuery("select * from city where city.id = "+entry.getKey()+");");
                if (rs.next()) {
                    Human gov = city.getGovernor();
                    if (gov != null) {
                        ResultSet rs2 = stmt.executeQuery("select * from governor where governor.id = " + gov.getId()+";");
                        if (rs2.next()) {
                            stmt.executeUpdate("update governor set id = "+ gov.getId()+", name = '"+gov.getName()+"', birthday = '"+gov.getBirthday()+"' where governor.id = 1;");
                        }
                        stmt.executeUpdate("insert into governor (id, name, birthday) values ("+gov.getId()+", '"+gov.getName()+"', "+gov.getBirthday()+");");
                    }
                    String query = "update city id = "+city.getId()+", name = '"+city.getName()+"', coordinates = '"+city.getCoordinates()+"', creationdate = '"+city.getCreationDate()+"', " +
                            "area = "+city.getArea()+", population = "+city.getPopulation()+", meterAboveSeaLevel = "+city.getMetersAboveSeaLevel()+", carCode = "+city.getCarCode()+", " +
                            "climate_id = "+Climate.getIdByName(city.getClimate())+", standardOfLiving_id = "+StandardOfLiving.getIdByName(city.getStandardOfLiving())+", " +
                            "governor_id = "+city.getGovernor().getId()+", creator_id = "+CREATOR+" where city.id = "+city.getId()+";";
                    System.out.println(query);
                    stmt.executeUpdate(query);
                }
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

    public static int get_next_id() {
        String query = "SELECT nextval('CitySeq');";
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

//    public static String insertValue(City city) {
//        String result = "ok";
//        try {
//            int CREATOR = 1;
//            con = DriverManager.getConnection(url, user, password);
//            stmt = con.createStatement();
//            String query = "insert into city (id, name, coordinates, creationdate, area, population, meterAboveSeaLevel, carCode, climate_id, standardOfLiving_id, governor_id, creator_id) values ("+city.getId()+", '"+city.getName()+"', '"
//                    +city.getCoordinates()+"', '"+city.getCreationDate()+"', " +city.getArea()+", "+city.getPopulation()+", "+city.getMetersAboveSeaLevel()+", "+city.getCarCode()+", "
//                    +Climate.getIdByName(city.getClimate())+", "+StandardOfLiving.getIdByName(city.getStandardOfLiving())+", "+city.getGovernor().getId()+", "+CREATOR+");";
//            System.out.println(query);
//            stmt.executeUpdate(query);
//        } catch (SQLException e) {
////            System.out.println(e.getMessage());
//            result = e.getMessage();
//        } catch (Exception e) { // SQLException sqlEx
////            sqlEx.printStackTrace();
//        } finally {
//            try { con.close(); } catch(Exception e) { /*can't do anything */ }
//            try { stmt.close(); } catch(Exception e) { /*can't do anything */ }
//            try { rs.close(); } catch(Exception e) { /*can't do anything */ }
//        }
//        return result;
//    }
}
