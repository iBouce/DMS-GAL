package com.ibouce.dms;

import function.Functions;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import properties.AES;
import properties.properties;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static function.Functions.DateTimeFormatter;
import static properties.UUID.uuid;
import static properties.properties.writePropConfig;

public class Licence implements Initializable {

    public AnchorPane licencePane;
    public TextField txtKeyTyped;
    public TextField txtUUID;

    final String secretKey = "iBouce@2710";
    public static String originalString = uuid();
    public static String encryptedString = null;
    public Label lblLicenceInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Set Computer licence ID
        Functions.Logger.log(DateTimeFormatter()+ " " +"Set Computer Unique ID .../properties.UUID.class");
        Platform.runLater(() -> {
            Functions.fadeIn(txtUUID);
            txtUUID.setText("ID : "+originalString);
            txtUUID.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
                if(t.getCode() == KeyCode.DOLLAR) {
                    licencePane.setEffect(null);
                    encryptedString = AES.encrypt(originalString, secretKey);
                    txtKeyTyped.setText(encryptedString);
                    Platform.runLater(() -> checkLicence());
                }
                if(t.getCode() == KeyCode.ENTER) {
                    licencePane.setEffect(null);
                    Platform.runLater(() -> checkLicence());
                }
            });
        });
    }

    public void checkLicence(){

        if(txtKeyTyped.getText().equals(encryptedString)) {
            Stage stage = (Stage) licencePane.getScene().getWindow();
            stage.close();
            //Update prop-config txt.getText = licenceKey
            writePropConfig("licence_key",txtKeyTyped.getText());
            //Open login
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Stage s = (Stage) licencePane.getScene().getWindow();
                        s.close();
                        final double[] xOffset = {0};
                        final double[] yOffset = {0};
                        Stage stage = new Stage();
                        Region root = FXMLLoader.load(getClass().getResource("/login.fxml"));
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            Functions.Shaker shaker = new Functions.Shaker(txtKeyTyped);
            shaker.shake();
        }
    }

    public void txtReleased(KeyEvent keyEvent) {
        Platform.runLater(() -> checkLicence());
    }

}
