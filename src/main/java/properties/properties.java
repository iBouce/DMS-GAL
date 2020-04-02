package properties;

import java.io.*;
import java.util.Properties;

import static function.Functions.propDir;

public class properties {

    public static void createProp(){
        try (OutputStream out = new FileOutputStream(propDir+"")) {
            Properties properties = new Properties();
            //***********Account**************************************
            properties.setProperty("login", "0");
            properties.setProperty("computer_id", UUID.uuid());
            properties.setProperty("licence_key", "");
            properties.setProperty("date", java.util.Calendar.getInstance().getTime().toString().substring(0, 10));
            //***********MySQL***************************************
            properties.setProperty("user", "admin");
            properties.setProperty("password", "BoomBoom@2710");
            properties.setProperty("port", "3306");
            properties.setProperty("host", "127.0.0.1");
            properties.setProperty("database", "mydb");
            //**********************************************************

            properties.storeToXML(out, "iBouce - GED Configuration File !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readPropConfig(String propertyValue){
        String value = null;
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            value = properties.getProperty(propertyValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void writePropConfig(String propertyValue, String text){
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            in.close();
            try (OutputStream out = new FileOutputStream(propDir)) {
                properties.setProperty(propertyValue, text);
                properties.storeToXML(out,"File Updated" + java.util.Calendar.getInstance().getTime().toString().substring(0, 10));
            } catch (IOException eee) {
                eee.printStackTrace();
            }
        } catch (IOException ee) {
            ee.printStackTrace();
        }
    }

    public static void readAllProp(){
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            for (String property : properties.stringPropertyNames()) {
                String value = properties.getProperty(property);
                System.out.println(property + "=" + value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readPropDate(){
        String date = null;
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            date = properties.getProperty("date");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static String readPropMysqlUser(){
        String user = null;
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            user = properties.getProperty("user");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }
    public static String readPropMysqlPassword(){
        String Password = null;
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            Password = properties.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Password;
    }
    public static String readPropMysqlPort(){
        String Port = null;
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            Port = properties.getProperty("port");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Port;
    }
    public static String readPropMysqlHost(){
        String Host = null;
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            Host = properties.getProperty("host");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Host;
    }
    public static String readPropMysqlDB(){
        String database = null;
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            database = properties.getProperty("database");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return database;
    }
    public static String readPropLogin(){
        String login = null;
        try (InputStream in = new FileInputStream(propDir)) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            login = properties.getProperty("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return login;
    }

}