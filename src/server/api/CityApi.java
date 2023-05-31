package server.api;

import my_programm.enums.Climate;
import my_programm.enums.StandardOfLiving;
import my_programm.obj.City;
import my_programm.obj.Human;

import java.awt.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class CityApi extends BaseApi {
    public static Hashtable<Integer, City> readTable() {
        Hashtable<Integer, City> table = new Hashtable<>();
        Hashtable<Integer, Integer> govers = new Hashtable<>();
        String query = "select * from city";
        R_LOCK.lock();
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
                    govers.put(id, governor_id);
                }

                Integer creator_id = rs.getInt(12);
//                System.out.println("id - " + id + "\nname - " + name + "\ncoordinates - " + coordinates + "\ntime - " + time + "\narea - " + area + "\npopulation - " + population + "\nmeterAboveSeaLevel - " + meterAboveSeaLevel + "\ncarCode - " + carCode + "\nclimate_id - " + climate_id + "\nstandardOfLiving_id - " + standardOfLiving_id + "\ngovernor_id - " + governor_id + "\ncreator_id - " + creator_id);
                table.put(id, new City(id, name, coordinates, area, population, meterAboveSeaLevel, carCode, climate, standardOfLiving, governor, creator_id));

            }
            for (Map.Entry<Integer,Integer> entry : govers.entrySet()) {
                City city = table.get(entry.getKey());
                city.setGovernor(GovernorApi.get_governor(entry.getValue()));
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
        R_LOCK.unlock();
        return table;

    }

    public static String saveTable(Hashtable<Integer, City> table) {
        String result = "ok";
        R_LOCK.lock();
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            Hashtable<Integer, Integer> cities = new Hashtable<>();
            rs = stmt.executeQuery("select * from city");
            while (rs.next()) {
                cities.put(rs.getInt(1), rs.getInt(11));
            }

            for (Map.Entry<Integer,City> entry : table.entrySet()) {
                // getKey() getValue()
                City city = entry.getValue();
                cities.remove(city.getId());
                rs = stmt.executeQuery("select * from city where city.id = " + entry.getKey());
                Human gov = city.getGovernor();
                Integer gov_id = null;
                if (gov != null) {
                    GovernorApi.saveOrUpdate(gov);
                    gov_id = gov.getId();
                }
                if (rs.next()) {
                    String query = "update city set id = "+city.getId()+", name = '"+city.getName()+"', coordinates = '"+city.getCoordinates()+"', creationdate = '"+city.getCreationDate()+"', " +
                            "area = "+city.getArea()+", population = "+city.getPopulation()+", meterAboveSeaLevel = "+city.getMetersAboveSeaLevel()+", carCode = "+city.getCarCode()+", " +
                            "climate_id = "+Climate.getIdByName(city.getClimate())+", standardOfLiving_id = "+StandardOfLiving.getIdByName(city.getStandardOfLiving())+", " +
                            "governor_id = "+gov_id+", creator_id = "+ city.getCreator_id() +" where city.id = "+city.getId()+";";
//                    System.out.println(query);
                    stmt.executeUpdate(query);
                } else {
                    String query = "insert into city (id, name, coordinates, creationdate, area, population, meterAboveSeaLevel, carCode, climate_id, standardOfLiving_id, governor_id, creator_id) values " +
                            "("+city.getId()+", '"+city.getName()+"', '"+city.getCoordinates()+"', '"+city.getCreationDate()+"', "+city.getArea()+", "+city.getPopulation()+", "+city.getMetersAboveSeaLevel()+
                            ", "+city.getCarCode()+", " +Climate.getIdByName(city.getClimate())+", "+StandardOfLiving.getIdByName(city.getStandardOfLiving())+", "+gov_id+", "+ city.getCreator_id() + ")";
//                    System.out.println(query);
                    stmt.executeUpdate(query);
                }
            }

            for (Map.Entry<Integer,Integer> city : cities.entrySet()) {
                if (city.getValue() != null) {
                    stmt.executeUpdate("delete from governor where governor.id = "+city.getValue());
                }
                stmt.executeUpdate("delete from city where city.id = "+city.getKey());
            }
        } catch (Exception e) { // SQLException sqlEx
//            sqlEx.printStackTrace();
            e.printStackTrace();
            result = e.getMessage();
        } finally {
            try { con.close(); } catch(Exception e) { /*can't do anything */ }
            try { stmt.close(); } catch(Exception e) { /*can't do anything */ }
            try { rs.close(); } catch(Exception e) { /*can't do anything */ }
        }
        R_LOCK.unlock();
        return result;
    }

    public static int get_next_id() {
        String query = "SELECT nextval('CitySeq');";
        R_LOCK.lock();
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
        R_LOCK.unlock();
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
