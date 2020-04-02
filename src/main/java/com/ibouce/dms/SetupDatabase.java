package com.ibouce.dms;

import database.mysqlConnection;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static database.mysqlConnection.*;
import static properties.properties.*;

public class SetupDatabase implements Initializable {
    public Button btnDBGo;
    public Button btnDBBack;
    public Label lblDBInfo;
    public Button btnDBTestConn;
    public AnchorPane dbPane;
    public TextField txtDBname;
    public TextField txtHost;
    public TextField txtPort;
    public TextField txtUser;
    public PasswordField txtPassword;
    public ProgressIndicator progressBar;

    public int id;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtUser.setText(readPropConfig("user"));
        txtPassword.setText(readPropConfig("password"));
        txtPort.setText(readPropConfig("port"));
        txtHost.setText(readPropConfig("host"));
        txtDBname.setText(readPropConfig("database"));
    }

    public void checkMysqlConn(){
        final Connection[] conn = {null};
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                Platform.runLater(() -> {
                    progressBar.setVisible(true);
                    lblDBInfo.setText("Check Server Connection");
                });
                conn[0] = mysqlConnection.getConnect();
                return null;
            }
            @Override
            public void done(){
                progressBar.setVisible(false);
                if (conn[0] == null){
                    Platform.runLater(() -> {
                        lblDBInfo.setText("Server Connection Failed.. Try Again !");
                        //update mysql Properties in prop-config.xml file from Login value =0 to Login value = 1
                        writePropConfig("user", txtUser.getText());
                        writePropConfig("password", txtPassword.getText());
                        writePropConfig("port", txtPort.getText());
                        writePropConfig("host", txtHost.getText());
                        writePropConfig("database", txtDBname.getText());
                        writePropConfig("login", String.valueOf(id));
                    });
                }else{
                    Platform.runLater(() -> {
                        lblDBInfo.setText("Server Conn Established");
                        btnDBGo.setVisible(true);
                    });


                    //Create DB and Insert User Data and Select User_ID
                    final Task<Void> task = new Task<Void>() {
                        @Override
                        public Void call() {
                            Platform.runLater(() -> {
                                //Create Tables
                                createTables();
                                //Insert User Info
                                insertUsers(InscriptionName.name,InscriptionPin.password,"Administrator",true);
                                //Select user_id to update prop_config.xml login value from 0 to user-id number
                                id = selectLastUsersID();
                                insertCompany(1,"",null);
                                insertProfile(readPropConfig("computer_id"),InscriptionName.name, null, "", 0, "", "", new File("/media/person_127px.png"),id,1);

                                updateMessage("MySQL - Connection Established");
                            });
                            return null;
                        }
                    };
                    Platform.runLater(() -> {
                        lblDBInfo.textProperty().bind(task.messageProperty());
                    });
                    task.setOnSucceeded(e -> {
                        Platform.runLater(() -> {
                            lblDBInfo.textProperty().unbind();
                        });
                        //update mysql Properties in prop-config.xml file from Login value =0 to Login value = 1
                        writePropConfig("user", txtUser.getText());
                        writePropConfig("password", txtPassword.getText());
                        writePropConfig("port", txtPort.getText());
                        writePropConfig("host", txtHost.getText());
                        writePropConfig("database", txtDBname.getText());
                        writePropConfig("login", String.valueOf(id));
                        //Enable button Go to Login
                        btnDBTestConn.setDisable(true);
                        btnDBGo.setDisable(false);
                    });
                    final Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();


                }
            }
        };
        sw.execute();
    }

    public void btnDBTestConn(ActionEvent actionEvent) {
        checkMysqlConn();
    }

    public void btnDBGo(ActionEvent actionEvent) {
        //Open Login
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage s = (Stage) dbPane.getScene().getWindow();
                s.close();
                final double[] xOffset = {0};
                final double[] yOffset = {0};
                Stage stage = new Stage();
                Region root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("/login.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setResizable(false);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.initStyle(StageStyle.UNDECORATED);
                root.setOnMousePressed(event -> {
                    xOffset[0] = event.getSceneX();
                    yOffset[0] = event.getSceneY();
                });
                root.setOnMouseDragged(event -> {
                    stage.setX(event.getScreenX() - xOffset[0]);
                    stage.setY(event.getScreenY() - yOffset[0]);
                });
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        });
    }

    public void btnDBBack(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/inscription_pin.fxml"));
        Login.loginStackPanee.getChildren().add(root);
        FadeTransition ft = new FadeTransition(Duration.millis(3000));
        ft.setOnFinished(event -> {
            Login.loginStackPanee.getChildren().remove(dbPane);
        });
        ft.setNode(dbPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

}
