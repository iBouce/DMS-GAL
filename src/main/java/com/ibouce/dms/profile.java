package com.ibouce.dms;

import database.mysqlConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

import static database.mysqlConnection.*;
import static properties.properties.readPropConfig;

public class profile implements Initializable {
    public TextField txtFullName;
    public DatePicker dateBirthday;
    public TextField txtAddress;
    public TextField txtEmail;
    public TextField txtNumber;
    public TextField txtPost;
    public TextField txtCompanyName;
    public ImageView imgProfile;
    public ImageView imgCompany;
    Connection conn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            //selectProfile(Integer.valueOf(readPropConfig("login")),txtFullName.getText(),txtAddress.getText());
            txtCompanyName.setText(selectCompanyImage(imgCompany)+"");
            selectCompanyImage(imgCompany);
            populateProfile();
        });
    }

    public void populateProfile(){
        conn = mysqlConnection.getConnect();
        //String id = readPropConfig("computer_id");
        int id = Integer.parseInt(readPropConfig("login"));
        String sqlUser = "SELECT * FROM profile where user_user_id = '"+id+"'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlUser);
            while (rs.next()) {
                txtFullName.setText(rs.getString("profile_name"));
                java.sql.Date deadlineDatePrompt = rs.getDate("profile_birthday");
                dateBirthday.setValue(deadlineDatePrompt.toLocalDate());
                txtAddress.setText(rs.getString("profile_address"));
                txtNumber.setText(rs.getString("profile_number"));
                txtEmail.setText(rs.getString("profile_email"));
                txtPost.setText(rs.getString("profile_post"));
                InputStream imageFile = rs.getBinaryStream("profile_image");
                Image image = new Image(imageFile);
                imgProfile.setImage(image);
            }
            rs.close();
            st.close();
            //conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void btnImportImage(ActionEvent actionEvent) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        List<File>  selectedFiles = fileChooser.showOpenMultipleDialog(null);
            if (selectedFiles != null && selectedFiles.size() > 0) {
                for (int i = 0; i < selectedFiles.size(); i++) {
                    File file = new File(String.valueOf(selectedFiles.get(i)));
                    File image = new File(file.getAbsolutePath());
                    java.sql.Date gettedDatePickerDate = java.sql.Date.valueOf(dateBirthday.getValue());
                    updateProfile(Integer.parseInt(readPropConfig("login")),txtFullName.getText(),
                            gettedDatePickerDate,txtAddress.getText(),txtEmail.getText(),
                            Integer.parseInt(txtNumber.getText()),txtPost.getText(),image);
                }
            } else {
                System.out.println("No file selected !");
            }
    }

    public void btnImportCompanyPicture(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        List<File>  selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null && selectedFiles.size() > 0) {
            for (int i = 0; i < selectedFiles.size(); i++) {
                File file = new File(String.valueOf(selectedFiles.get(i)));
                File image = new File(file.getAbsolutePath());
                updateCompany(1,txtCompanyName.getText(),image);
            }
        } else {
            System.out.println("No file selected !");
        }
    }
}