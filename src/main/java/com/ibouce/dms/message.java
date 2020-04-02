package com.ibouce.dms;

import database.mysqlConnection;
import function.Functions;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.*;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import static database.mysqlConnection.insertMessage;
import static database.mysqlConnection.selectUsersID;
import static function.Functions.readFile;
import static function.Functions.repositoryDir;
import static properties.properties.readPropLogin;

public class message implements Initializable {

    public ListView listUsers;
    public static ListView listUserss;
    public Label lblUsername;
    public TextArea txtMessage;
    public TextFlow txtFlow;
    public ScrollPane scrollPane;
    public ScrollPane scrollPaneFile;
    public TextFlow txtFlowFiles;

    Connection conn;
    Integer sender = null,receiver = null;

    //public ListView<String> listViewMessage;
    //private final ObservableList<String> data = FXCollections.observableArrayList("","","","","");

    public message(){
        conn = mysqlConnection.getConnect();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listUserss=listUsers;
        selectUsers();
        txtMessage.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
            if(t.getCode()== KeyCode.ENTER) {

                if (txtMessage.getText().isEmpty() || lblUsername.getText().equals("@Username")){
                    Functions.Shaker shaker = new Functions.Shaker(txtMessage);
                    shaker.shake();
                }else{
                    //select sender username
                    try {
                        Statement st2 = conn.createStatement();
                        ResultSet rs2 = st2.executeQuery("select * from user where user_name = '"+lblUsername.getText()+"'");
                        Integer receiver = null;
                        while (rs2.next()) {
                            receiver = rs2.getInt("user_id");
                        }
                        insertMessage(selectUsersID(Integer.valueOf(readPropLogin())), receiver,txtMessage.getText(),0,"0", new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime()));
                        populateMessage();
                        txtMessage.clear();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void selectUsers() {

        List<String> results = new ArrayList<>();
        ObservableList<String> ids = null;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select user_name,user_type from user where user_id != '"+readPropLogin()+"'");
            while(rs.next()){
                results.add(rs.getString("user_name"));

                ids = FXCollections.observableArrayList(results);

                Text t1 = new Text(""+rs.getString("user_name"));
                t1.setFill(Color.web("#ecf0f1"));
                t1.setFont(Font.font("Roboto", FontWeight.BOLD, 14));

                Text t2 = new Text(rs.getString("user_type"));
                t2.setFill(Color.web("#95a5a6"));
                t2.setFont(Font.font("Roboto", FontPosture.REGULAR, 12));

                VBox vBox = new VBox(t1, t2);

                ImageView img = new ImageView(new Image("/media/person_127px.png"));
                img.setFitHeight(25);
                img.setFitWidth(25);

                HBox hBox = new HBox(img, vBox);
                hBox.setAlignment(Pos.CENTER_LEFT);
                hBox.setSpacing(10);

                listUserss.getItems().add(hBox);
            }
            listUserss.setItems(ids);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }



        /*ObservableList<String> ids = FXCollections.observableArrayList(results);
        listUserss.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean bln) {
                super.updateItem(item, bln);
                if (item != null) {
                    Text t1 = new Text(""+results.get(0));
                    t1.setFill(Color.web("#ecf0f1"));
                    t1.setFont(Font.font("Roboto", FontWeight.BOLD, 14));

                    Text t2 = new Text("Administrator");
                    t2.setFill(Color.web("#95a5a6"));
                    t2.setFont(Font.font("Roboto", FontPosture.REGULAR, 12));

                    VBox vBox = new VBox(t1, t2);

                    ImageView img = new ImageView(new Image("/media/person_127px.png"));
                    img.setFitHeight(25);
                    img.setFitWidth(25);

                    HBox hBox = new HBox(img, vBox);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setSpacing(10);

                    setGraphic(hBox);
                }
            }
        });
        listUserss.setItems(ids);
        */

    }

    public void listOnMouseClicked(MouseEvent mouseEvent) {
        lblUsername.setText(String.valueOf(listUsers.getSelectionModel().getSelectedItem()));
        populateMessage();
        //populateListView();
    }

    private void populateMessage() {
        txtFlow.getChildren().clear();
        //txtFlowFiles.getChildren().clear();

        HBox hbox = new HBox();
        Pane pane = new Pane();
        ImageView img;
        Text t1,t2,t3;
        Integer msgID;
        String docData;
        String msg,msgStatus = null,date;
        Integer id = null;

        //Select User ID of the contact
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select user_id from user where user_name = '"+lblUsername.getText()+"'");
            while(rs.next()){
                id = rs.getInt("user_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        //Select user messages
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT msg_id,user_receiver,user_sender,msg_text,doc_data,msg_status,msg_date FROM message where user_sender = '"+readPropLogin()+"' AND user_receiver = '"+id+"' OR user_sender = '"+id+"' AND user_receiver = '"+readPropLogin()+"' ORDER BY msg_id DESC LIMIT 30");
            while (rs.next()) {
                msgID = rs.getInt("msg_id");
                sender = rs.getInt("user_sender");
                receiver = rs.getInt("user_receiver");
                msg = rs.getString("msg_text");
                docData = rs.getString("doc_data");
                msgStatus = rs.getString("msg_status");
                date = rs.getString("msg_date");

                //select sender username
                Statement st2 = conn.createStatement();
                ResultSet rs2 = st2.executeQuery("select * from user where user_id = '"+sender+"'");
                String username = null;
                while (rs2.next()) {
                    username = rs2.getString("user_name");
                }
                /*if (sender == Integer.parseInt(readPropLogin())){
                    img = new ImageView("/resource/media/icons8-name-100.png");
                }else {
                    img = new ImageView("/resource/media/icons8-name-101.png");
                }*/
                t1 = new Text("\n"+"\n"+"  "+ username);
                t2 = new Text("  "+ date + "\n");
                t3 = new Text("  "+ msg);
                //vbox.getChildren().addAll(t1,t2,new hyperlink().downloadLink(docID));
                //hbox.getChildren().addAll(img,vbox);
                if (msgStatus.equals("0")){
                    txtFlow.getChildren().addAll(t1,t2,t3);
                }else{
                    //vbox.getChildren().addAll(t1,t2,t3);
                    //txtFlow.getChildren().addAll(img,text_1,text_2, new hyperlink().downloadLink(docID),text_3);
                    txtFlow.getChildren().addAll(t1,t2, new hyperlinkOpen().downloadLinkOpen(msgID,msg));
                }
                t1.setFill(Color.web("#2c3e50"));
                t1.setFont(Font.font("Roboto", FontWeight.BOLD, 14));
                t2.setFill(Color.web("#95a5a6"));
                t2.setFont(Font.font("Roboto", FontPosture.REGULAR, 12));
                t3.setFill(Color.web("#34495e"));
                t3.setFont(Font.font("Roboto", FontPosture.ITALIC, 12));
                //img.setFitHeight(25);
                //img.setFitWidth(25);
                //hbox.setHgrow(pane, Priority.ALWAYS);
                //hbox.setAlignment(Pos.CENTER_LEFT);
            }

            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        //scrollPane.vvalueProperty().bind(txtFlow.heightProperty());
        //scrollPaneFile.vvalueProperty().bind(txtFlowFiles.heightProperty());
    }

    public void btnSend(ActionEvent actionEvent) throws SQLException {
        if (txtMessage.getText().isEmpty() || lblUsername.getText().equals("@Username")){
            Functions.Shaker shaker = new Functions.Shaker(txtMessage);
            shaker.shake();
        }else{
            //select sender username
            Statement st2 = conn.createStatement();
            ResultSet rs2 = st2.executeQuery("select * from user where user_name = '"+lblUsername.getText()+"'");
            Integer receiver = null;
            while (rs2.next()) {
                receiver = rs2.getInt("user_id");
            }
            //insertMessage(selectUsersID(Integer.valueOf(readPropLogin())), receiver,txtMessage.getText(),0,"0", new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime()));
            //Insert Document
            Integer finalReceiver = receiver;
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO message(user_sender,user_receiver,msg_text,doc_data,msg_status,msg_date) VALUES(?,?,?,?,?,?)");
                ps.setInt(1, Integer.valueOf(readPropLogin()));
                ps.setInt(2, finalReceiver);
                ps.setString(3, txtMessage.getText());
                ps.setBytes(4, null);
                ps.setString(5, "0");
                ps.setString(6, new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime()));
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            populateMessage();
            txtMessage.clear();
        }
    }

    public void btnImport(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        Platform.runLater(() -> {
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
            if (selectedFiles != null && selectedFiles.size() > 0) {
                for (int i = 0; i < selectedFiles.size(); i++) {
                    File file = new File(String.valueOf(selectedFiles.get(i)));

                    //Insert Message
                    try {
                        //select sender username
                        Statement st2 = conn.createStatement();
                        ResultSet rs2 = st2.executeQuery("select * from user where user_name = '"+lblUsername.getText()+"'");
                        Integer receiver = null;
                        while (rs2.next()) {
                            receiver = rs2.getInt("user_id");
                        }
                        Integer finalReceiver = receiver;
                        PreparedStatement ps = conn.prepareStatement("INSERT INTO message(user_sender,user_receiver,msg_text,doc_data,msg_status,msg_date) VALUES(?,?,?,?,?,?)");
                        ps.setInt(1, Integer.valueOf(readPropLogin()));
                        ps.setInt(2, finalReceiver);
                        ps.setString(3, file.getName());
                        ps.setBytes(4, readFile(file.getAbsolutePath()));
                        ps.setString(5, "1");
                        ps.setString(6, new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime()));
                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                populateMessage();
            } else {
                System.out.println("No file selected");
            }
        });
    }

    public void anchorPaneOnmouseMouved(MouseEvent mouseEvent) {
        populateMessage();
    }

    class Cell extends ListCell<String> {
        HBox hbox = new HBox();
        VBox vbox = new VBox();
        ImageView img = new ImageView();
        Text t1 = new Text();
        Text t2 = new Text();
        Text t3 = new Text();
        Pane pane = new Pane();

        public Cell(){
            super();

            t1.setFill(Color.web("#2c3e50"));
            t1.setFont(Font.font("Roboto", FontWeight.BOLD, 14));
            t2.setFill(Color.web("#95a5a6"));
            t2.setFont(Font.font("Roboto", FontPosture.REGULAR, 10));
            t3.setFill(Color.web("#34495e"));
            t3.setFont(Font.font("Roboto", FontPosture.ITALIC, 12));

            img.setFitHeight(25);
            img.setFitWidth(25);

            hbox.setHgrow(pane, Priority.ALWAYS);
            hbox.setAlignment(Pos.CENTER_LEFT);

            vbox.getChildren().addAll(t1,t2,t3);
            hbox.getChildren().addAll(img,vbox);

        }

        public void updateItem(String item,boolean empty){
            super.updateItem(item,empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty){

                //Select sender messages and display it in textFlow
                String sqlUser = "SELECT user_sender,msg_text,doc_id,msg_date FROM message where user_receiver = '"+lblUsername.getText()+"' OR user_sender = '"+lblUsername.getText()+"' ORDER BY msg_id DESC LIMIT 10";
                try {
                    Integer sender,docID;String msg,date;
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sqlUser);
                    while (rs.next()) {

                        t1.setText(String.valueOf(rs.getInt("user_sender")));
                        t2.setText(rs.getString("msg_date"));
                        t3.setText(rs.getString("msg_text"));
                        sender = rs.getInt("user_sender");
                        msg = rs.getString("msg_text");
                        docID = rs.getInt("doc_id");
                        date = rs.getString("msg_date");

                    }

                    rs.close();
                    st.close();
                    //conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                img.setImage(new Image("/media/icons8-name-100.png"));
                //t1.setText("aze");
                //t2.setText("qsd");
                //t3.setText("ggggggg");
            }
            setGraphic(hbox);
        }
    }

    public void populateListView(){
        //data.clear();
        //listViewMessage.setItems(data);
        //listViewMessage.setCellFactory(param ->new Cell());
    }

    public class hyperlinkOpen {
        public Hyperlink downloadLinkOpen(Integer id,String name){
            Hyperlink hl = new Hyperlink(name);
            hl.setOnAction(event -> {

                String filePath = repositoryDir +System.getProperty("file.separator")+ name;
                File f = new File(filePath);
                if(f.exists() && !f.isDirectory() && f.length() != 0) {
                    try {
                        Desktop.getDesktop().open(new File(filePath));
                    } catch (IOException e) {
                        System.out.println("File downloaded but failed to open. No application is associated with the file specified for this operation.");
                    }
                }else{
                    System.out.println("Download Document.");
                    //Download Doc that doc_id = id of last id document added when import
                    ResultSet rs = null;
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM message WHERE msg_id='"+id+"'");
                        rs = pstmt.executeQuery();
                        while (rs.next()) {
                            String doc_name = rs.getString("msg_text");
                            InputStream input = rs.getBinaryStream("doc_data");
                            byte byteArray[] = new byte[input.available()];
                            input.read(byteArray);
                            FileOutputStream outPutStream = new FileOutputStream(repositoryDir +System.getProperty("file.separator")+doc_name);
                            outPutStream.write(byteArray);
                        }

                    } catch (SQLException | IOException e) {
                        System.out.println(e);
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }

            });
            return hl;
        }
    }

}
