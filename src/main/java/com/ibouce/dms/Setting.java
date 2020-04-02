package com.ibouce.dms;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static database.mysqlConnection.*;
import static properties.UUID.uuid;
import static properties.properties.*;

public class Setting implements Initializable {

    public AnchorPane settingPane;
    public TextField txtUsername;
    public PasswordField txtCodePin;
    public TextField txtEmail;
    public TextField txtStructure;
    public TextField txtStatus;
    public TextField txtMysqlUsername;
    public PasswordField txtMysqlPassword;
    public TextField txtMysqlHost;
    public TextField txtMysqlPort;
    public TextField txtMysqlDatabase;
    public Label lblID;
    public Label lblKey;
    public Label lblPropConfigPath;
    public Label lblPropConfigDate;
    public Button btnSaveSettings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSaveSettings.setVisible(false);
        Platform.runLater(() -> setSetting());
    }

    public void setSetting(){
        Platform.runLater(() -> {
            //Set Profile Information
            txtUsername.setText(selectUserName(Integer.valueOf(readPropConfig("login"))));
            txtCodePin.setText("0000");
            //txtEmail.setText(selectUsersEmail(Integer.valueOf(readPropLogin())));
            txtStructure.setText(selectUserType(Integer.valueOf(readPropLogin())));
            txtStatus.setText("Active");

            //Set Server Configuration Information
            txtMysqlUsername.setText(readPropMysqlUser());
            txtMysqlPassword.setText(readPropMysqlPassword());
            txtMysqlHost.setText(readPropMysqlHost());
            txtMysqlPort.setText(readPropMysqlPort());
            txtMysqlDatabase.setText(readPropMysqlDB());

            //Set Licence Product Information
            lblID.setText("Computer Unique ID : " + uuid());
            lblKey.setText("Licence Key : "+ readPropConfig("licence_key"));
            lblPropConfigPath.setText("prop-config.xml");
            lblPropConfigDate.setText("Activation Date : "+readPropDate());
        });
    }

    public void btnSaveSettings(ActionEvent actionEvent) {
        if(txtUsername.getText().equals("") || txtCodePin.getText().equals("") || txtEmail.getText().equals("") ||
                txtStructure.getText().equals("") || txtStatus.getText().equals("") || txtMysqlUsername.getText().equals("") ||
                txtMysqlPassword.getText().equals("") || txtMysqlPort.getText().equals("") || txtMysqlHost.getText().equals("") ||
                txtMysqlDatabase.getText().equals("")){
            btnSaveSettings.setVisible(false);
        }else{
            btnSaveSettings.setVisible(false);
            txtCodePin.setStyle("-fx-text-fill: #000;");
            txtCodePin.setStyle("-fx-background-color: transparent;");
            updateServerConfiguration();
            updateUserProfile();
        }
    }

    public void updateServerConfiguration(){
        //update Setting in prop-config.xml file
        try (InputStream in = new FileInputStream("prop-config.xml")) {
            Properties properties = new Properties();
            properties.loadFromXML(in);
            in.close();
            try (OutputStream out = new FileOutputStream("prop-config.xml")) {
                properties.setProperty("user", txtMysqlUsername.getText());
                properties.setProperty("password", txtMysqlPassword.getText());
                properties.setProperty("port", txtMysqlPort.getText());
                properties.setProperty("host", txtMysqlHost.getText());
                properties.setProperty("database", txtMysqlDatabase.getText());
                properties.storeToXML(out,"File Updated" + java.util.Calendar.getInstance().getTime().toString().substring(0, 10));
            } catch (IOException eee) {
                eee.printStackTrace();
            }
        } catch (IOException ee) {
            ee.printStackTrace();
        }
    }

    public void updateUserProfile(){
        //Update User Profile
        selectUpdateUser(Integer.valueOf(readPropLogin()),txtUsername.getText(),txtEmail.getText(),txtCodePin.getText(),txtStructure.getText());
    }

    public void checkTextField(){
        if(txtUsername.getText().equals("") || txtCodePin.getText().equals("") || txtEmail.getText().equals("") ||
                txtStructure.getText().equals("") || txtStatus.getText().equals("") || txtMysqlUsername.getText().equals("") ||
                txtMysqlPassword.getText().equals("") || txtMysqlPort.getText().equals("") || txtMysqlHost.getText().equals("") ||
                txtMysqlDatabase.getText().equals("")){
            btnSaveSettings.setVisible(false);
            txtCodePin.setStyle("-fx-text-fill: red;");
        }else{
            btnSaveSettings.setVisible(true);
            txtCodePin.setStyle("-fx-text-fill: red;");
        }
    }

    public void btnReIndex(ActionEvent actionEvent) {
        //entityManager.createNativeQuery("ALTER TABLE student AUTO_INCREMENT = 1");
    }

    public void txtUsernameOnKeyReleased(KeyEvent keyEvent) {
        checkTextField();
    }

    public void txtCodePinOnKeyReleased(KeyEvent keyEvent) {
        checkTextField();
    }

    public void txtEmailOnKeyReleased(KeyEvent keyEvent) {
        checkTextField();
    }

    public void txtStructureOnKeyReleased(KeyEvent keyEvent) {
        checkTextField();
    }

    public void btnProfile(ActionEvent actionEvent) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/profile.fxml"));
        settingPane.getChildren().setAll(pane);
    }
}
