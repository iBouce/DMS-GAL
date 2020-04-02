package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;

import static properties.properties.*;

public class mysqlConnection {

    private static Connection conn = null;

    final private static String user = readPropMysqlUser();
    final private static String password = readPropMysqlPassword();
    final private static String database = readPropMysqlDB();

    private  mysqlConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String host = readPropConfig("host");
            int port = Integer.parseInt(readPropConfig("port"));
            conn = DriverManager.getConnection("jdbc:mysql://"+ host +":"+ port +"/"+database+"?&autoReconnect=true&useSSL=false&jdbcCompliantTruncation=false&serverTimezone=UTC", ""+user,""+ password);
            System.out.println("MySQL - Connection Established");
        } catch (SQLException e) {
            //mysqlConnectionErrorAlert(e);
            //JOptionPane.showMessageDialog(null, "SQLException "+e);
            System.out.println("MySQL - Connection Failed : "+e);
        } catch (ClassNotFoundException e) {
            //JOptionPane.showMessageDialog(null, "ClassNotFoundException "+e);
            System.out.println("MySQL - Could Not Load Drivers : "+e);
        } catch (IllegalAccessException | InstantiationException e) {
            //JOptionPane.showMessageDialog(null, "IllegalAccessException | InstantiationException "+e);
            e.printStackTrace();
        }
    }

    public static Connection getConnect() {
        if (conn == null){new mysqlConnection();}return conn;
    }

    public static void createTables() {
        conn = mysqlConnection.getConnect();

        String sqlUser = "CREATE TABLE IF NOT EXISTS user (user_id INTEGER(200) NOT NULL AUTO_INCREMENT," +
                "user_name CHAR(255), user_password CHAR(255), user_type CHAR(255)," +
                "user_status BOOLEAN,UNIQUE(user_id))";

        String sqlProfile = "CREATE TABLE IF NOT EXISTS profile (profile_id CHAR(200) NOT NULL," +
                "profile_name CHAR(255), profile_birthday DATE, profile_address CHAR(255)," +
                "profile_number INTEGER(200),profile_email CHAR(255),profile_post CHAR(255),profile_image LONGBLOB," +
                "user_user_id INTEGER(200) NOT NULL,company_company_id INTEGER(200) NOT NULL,UNIQUE(profile_id))";

        String sqlCompany = "CREATE TABLE IF NOT EXISTS company (company_id INTEGER(200) NOT NULL," +
                "company_name CHAR(255),company_image LONGBLOB,UNIQUE(company_id))";

        String sqlDocument = "CREATE TABLE IF NOT EXISTS document (doc_id INTEGER(200) NOT NULL AUTO_INCREMENT," +
                "user_id INTEGER(200),doc_name CHAR(255),doc_ocr CHAR(255),doc_type CHAR(255),doc_extension CHAR(255)," +
                "doc_size CHAR(255), doc_date_modify DATE, doc_data LONGBLOB,UNIQUE(doc_id))";

        String sqlTypeDocument = "CREATE TABLE IF NOT EXISTS document_type (doc_type_id INTEGER(200) NOT NULL AUTO_INCREMENT," +
                "user_id INTEGER(200),doc_type_name CHAR(255),UNIQUE(doc_type_id))";

        String sqlMessage = "CREATE TABLE IF NOT EXISTS message (msg_id INTEGER(200) NOT NULL AUTO_INCREMENT," +
                "user_sender INTEGER(200),user_receiver INTEGER(200),msg_text CHAR(255), doc_data LONGBLOB," +
                "msg_status CHAR(255),msg_date CHAR(255),UNIQUE(msg_id))";

        String sqlReminder = "CREATE TABLE IF NOT EXISTS reminder (rmd_id INTEGER(200) NOT NULL AUTO_INCREMENT," +
                "rmd_user INTEGER(200),rmd_date DATE,rmd_time TIME, rmd_note CHAR(255), UNIQUE(rmd_id))";

        String sqlTest = "CREATE TABLE IF NOT EXISTS test (test_id INTEGER(200) NOT NULL AUTO_INCREMENT," +
                "test_name CHAR(255),test_description CHAR(255), test_data LONGBLOB, UNIQUE(test_id))";

        try {
            PreparedStatement ps1 = conn.prepareStatement(sqlUser);
            PreparedStatement ps2 = conn.prepareStatement(sqlProfile);
            PreparedStatement ps3 = conn.prepareStatement(sqlCompany);
            PreparedStatement ps4 = conn.prepareStatement(sqlDocument);
            PreparedStatement ps5 = conn.prepareStatement(sqlTypeDocument);
            PreparedStatement ps6 = conn.prepareStatement(sqlMessage);
            PreparedStatement ps7 = conn.prepareStatement(sqlReminder);
            PreparedStatement ps8 = conn.prepareStatement(sqlTest);
            ps1.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
            ps4.executeUpdate();
            ps5.executeUpdate();
            ps6.executeUpdate();
            ps7.executeUpdate();
            ps8.executeUpdate();
            System.out.println("Tables Creation Established !");
            ps1.close();
            ps2.close();
            ps3.close();
            ps4.close();
            ps5.close();
            ps6.close();
            ps7.close();
            ps8.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void insertUsers(String username,String pwd,String type,Boolean status) {
        conn = mysqlConnection.getConnect();
        /*String sqlUser = "INSERT INTO user(user_name,user_password,user_type,user_status)" +
                " VALUES ('"+username+"','"+pwd+"','"+type+"','"+status+"')";*/

        String sqlUser = "INSERT INTO user(user_name,user_password,user_type,user_status) VALUES (?,?,?,?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sqlUser);
            ps.setString(1, username);
            ps.setString(2, pwd);
            ps.setString(3, type);
            ps.setBoolean(4, status);
            ps.executeUpdate();
            System.out.println("User Insertion Established !");
            ps.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public static void updateUser(Integer userID, String username,String pwd, String type,Boolean status) {
        conn = mysqlConnection.getConnect();
        String sqlUser = "update user set user_name = ?,user_password = ?,user_type = ?,user_status = ? where user_id = '"+userID+"' ";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(sqlUser);
            preparedStmt.setString(1, username);
            preparedStmt.setString(2, pwd);
            preparedStmt.setString(3, type);
            preparedStmt.setBoolean(4, status);
            preparedStmt.executeUpdate();
            preparedStmt.close();
            //conn.close();
            System.out.println("User Updated");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
    public static void deleteUser(Integer userID){
        try {
            String query = "delete from user where user_id = '"+userID+"'";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.execute();
            //conn.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public static int selectUsersID(Integer prop_login) {
        conn = mysqlConnection.getConnect();
        Integer id = 0;
        String sqlUser = "SELECT user_id FROM User where user_id = '"+prop_login+"'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                id = rs.getInt("user_id");
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;
    }
    public static String selectUserName(Integer readPropLoginID) {
        conn = mysqlConnection.getConnect();
        String name = null;
        String sqlUser = "SELECT user_name FROM User WHERE user_id = '"+readPropLoginID+"' ";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                name = rs.getString("user_name");
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return name;
    }
    public static String selectUserType(Integer prop_login) {
        conn = mysqlConnection.getConnect();
        String type = null;
        String sqlUser = "SELECT user_type FROM User where user_id = '"+prop_login+"'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                type = rs.getString("user_type");
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return type;
    }
    public static Boolean selectUserStatus(Integer prop_login) {
        conn = mysqlConnection.getConnect();
        Boolean status = null;
        String sqlUser = "SELECT user_status FROM User where user_id = '"+prop_login+"'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                status = rs.getBoolean("user_status");
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return status;
    }
    public static int selectLastUsersID() {
        conn = mysqlConnection.getConnect();
        Integer id = 0;
        String sqlUser = "SELECT user_id FROM User ORDER BY user_id DESC LIMIT 1";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                id = rs.getInt("user_id");
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;
    }
    public static int selectTotalUsers() {
        conn = mysqlConnection.getConnect();
        Integer numberRow = 0;
        String query = "select count(*) from user";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                numberRow = rs.getInt("count(*)");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return numberRow;
    }
    public static boolean validateLogin(Integer id, String password) {
        conn = mysqlConnection.getConnect();
        String SELECT_QUERY = "SELECT * FROM user WHERE user_id = ? and user_password = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            preparedStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void insertCompany(Integer companyID,String name,File image) {
        conn = mysqlConnection.getConnect();
        String sqlUser = "REPLACE INTO company (company_id,company_name,company_image) VALUES (?,?,?)";
        try {
            //FileInputStream fis = new FileInputStream(image);
            PreparedStatement ps = conn.prepareStatement(sqlUser);
            ps.setInt(1, companyID);
            ps.setString(2, name);
            //ps.setBinaryStream (3, fis, (int) image.length());
            ps.setBinaryStream (3, null);
            ps.executeUpdate();
            System.out.println("Company Insertion Established !");
            ps.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public static String selectCompanyImage(ImageView img) {
        conn = mysqlConnection.getConnect();
        String companyName=null;
        String sqlUser = "SELECT company_name,company_image FROM company where company_id = 1";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                companyName = rs.getString(1);
                InputStream imageFile = rs.getBinaryStream(2);
                Image image = new Image(imageFile);
                img.setImage(image);
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return companyName;
    }
    public static void updateCompany(Integer companyID,String name, File image) {

        conn = mysqlConnection.getConnect();
        String sqlProfile = "update company set company_id = ?,company_name = ?,company_image = ? where company_id = '"+companyID+"' ";
        try {
            FileInputStream fis = new FileInputStream(image);
            PreparedStatement ps = conn.prepareStatement(sqlProfile);
            ps.setInt(1, companyID);
            ps.setString(2, name);
            ps.setBinaryStream (3, fis, (int) image.length());

            ps.executeUpdate();
            System.out.println("Company Update Established !");
            ps.close();
            //conn.close();
        } catch (SQLException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void insertProfile(String profileID,String fullname, Date birthday, String address, Integer number, String email, String post, File image,Integer userID,Integer companyID) {

        conn = mysqlConnection.getConnect();
        String sqlUser = "REPLACE INTO profile (profile_id,profile_name,profile_birthday,profile_address,profile_number,profile_email,profile_post,profile_image,user_user_id,company_company_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try {
            //FileInputStream fis = new FileInputStream(image);
            PreparedStatement ps = conn.prepareStatement(sqlUser);
            ps.setString(1, profileID);
            ps.setString(2, fullname);
            ps.setDate(3, birthday);
            ps.setString(4, address);
            ps.setInt(5, number);
            ps.setString(6, email);
            ps.setString(7, post);
            ps.setBinaryStream (8, null);
            ps.setInt(9, userID);
            ps.setInt (10, companyID);
            ps.executeUpdate();
            System.out.println("Profile Insertion Established !");
            ps.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public static void updateProfile(Integer userID,String fullname, Date birthday, String address, String email, Integer number, String post, File image) {

        conn = mysqlConnection.getConnect();
        String sqlProfile = "update profile set profile_id = ?,profile_name = ?,profile_birthday = ?,profile_address = ?," +
                "profile_email = ?,profile_number = ?,profile_post = ?,profile_image = ? where user_user_id = '"+userID+"' ";
        try {
            FileInputStream fis = new FileInputStream(image);
            PreparedStatement ps = conn.prepareStatement(sqlProfile);
            ps.setInt(1, userID);
            ps.setString(2, fullname);
            ps.setDate(3, birthday);
            ps.setString(4, address);
            ps.setString(5, email);
            ps.setInt(6, number);
            ps.setString(7, post);
            ps.setBinaryStream (8, fis, (int) image.length());

            ps.executeUpdate();
            System.out.println("Profile Insertion Established !");
            ps.close();
            //conn.close();
        } catch (SQLException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    public static void deleteProfile(Integer userID){
        try {
            String query = "delete from profile where user_id = '"+userID+"'";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.execute();
            //conn.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public static void selectProfile(Integer prop_login,String name, String address) {
        conn = mysqlConnection.getConnect();
        String sqlUser = "SELECT profile_name,profile_address FROM profile where profile_id = '"+prop_login+"'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                //id = rs.getInt("user_id");
                name= rs.getString("user_name");
                //birthday rs.getDate("user_name");
                address = rs.getString("user_address");
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public static void selectProfileImage(Integer prop_login,ImageView img) {
        conn = mysqlConnection.getConnect();
        String sqlUser = "SELECT profile_image FROM profile where user_user_id = '"+prop_login+"'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                InputStream imageFile = rs.getBinaryStream(1);
                Image image = new Image(imageFile);
                img.setImage(image);
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void insertMessage(Integer usersender, Integer userreciever, String msgtext, Integer doc_id, String msgstatus, String date) {
        conn = mysqlConnection.getConnect();
        String sqlUser = "INSERT INTO message(user_sender,user_receiver,msg_text,doc_id,msg_status,msg_date)" +
                " VALUES ('"+usersender+"','"+userreciever+"','"+msgtext+"','"+doc_id+"','"+msgstatus+"','"+date+"')";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlUser);
            ps.executeUpdate();
            System.out.println("Message Insertion Established !");
            ps.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void insertDocType(Integer userid,String doctype) {
        conn = mysqlConnection.getConnect();
        String sqlDocType = "INSERT INTO document_type (user_id,doc_type_name)" +
                " VALUES ('"+userid+"','"+doctype+"')";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlDocType);
            ps.executeUpdate();
            System.out.println("Document_Type Insertion Established !");
            ps.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void selectDocTypeName(ListView<String> listView,ComboBox<String> cb , Integer prop_login) {
        ObservableList<String> items = FXCollections.observableArrayList();
        listView.setItems(items);
        cb.setItems(items);

        conn = mysqlConnection.getConnect();
        String sqlUser = "SELECT doc_type_name FROM document_type where user_id = '"+prop_login+"'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                items.add(rs.getString("doc_type_name"));
            }
            while (rs.next()) {
                cb.getItems().addAll(rs.getString("doc_type_name"));
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static int selectTotalDocType() {
        conn = mysqlConnection.getConnect();
        Integer numberRow = 0;
        String query = "select count(*) from document_type";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                numberRow = rs.getInt("count(*)");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return numberRow;
    }

    public static void insertDocs(Integer user_id, String name, String ocr, String type,String extension, String size, Date date, byte[] data) {
        conn = mysqlConnection.getConnect();
        String sqlUser = "INSERT INTO document(user_id,doc_name,doc_ocr,doc_type,doc_extension,doc_size,doc_date_modify,doc_data)" +
                " VALUES ('"+user_id+"','"+name+"','"+ocr+"','"+type+"','"+extension+"','"+size+"','"+date+"','"+data+"')";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlUser);
            ps.executeUpdate();
            System.out.println("Doc Insertion Established !");
            ps.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static int selectTotalDocs() {
        conn = mysqlConnection.getConnect();
        Integer numberRow = 0;
        String query = "select count(*) from document";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                numberRow = rs.getInt("count(*)");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return numberRow;
    }

    public static int selectUploadedDocs(Integer user_id) {
        conn = mysqlConnection.getConnect();
        Integer numberRow = 0;
        String query = "select count(*) from document where user_id = '"+user_id+"'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                numberRow = rs.getInt("count(*)");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return numberRow;
    }

    public static void searchDocs(String text) {

    }

    public static void selectUpdateUser(Integer prop_login, String username,String email,String code, String structure) {
        conn = mysqlConnection.getConnect();

        String sqlUser = "update user set user_name = '"+username+"',user_mail = '"+email+"',user_password = '"+code+"',user_structure = '"+structure+"'  where user_id = '"+prop_login+"' ";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(sqlUser);
            preparedStmt.executeUpdate();
            preparedStmt.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String []args) throws SQLException {
        mysqlConnection.getConnect();
        //insertUsers("Bouce", "0000","Administrator",true);
        //createTables();
//      updateProfile(2,"aze","", "address", "email", 123, "post", new File("")) {
        //Statement stmt = con.createStatement();
        //stmt.execute("CREATE TRIGGER notifyMe AFTER INSERT ON message FOR EACH ROW");
        //con.close();
    }

}