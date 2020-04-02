package com.ibouce.dms;

import function.Functions;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static function.Functions.DateTimeFormatter;

public class InscriptionName implements Initializable {

    public TextField txtEmailTyped;
    public Button btnSubGo;
    public Button btnSubBack;
    public Label lblSubscribeInfo;
    public AnchorPane inscriptionEmailPane;
    public TextField txtNameTyped;
    public static String name,email;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Functions.Logger.log(DateTimeFormatter()+ " " +"Load Registration .../resource/InscriptionEmail.fxml");
        //btnSubGo.setDisable(true);
    }

    public void txtNameReleased(KeyEvent keyEvent) {
        String name = txtNameTyped.getText();
        //String email = txtEmailTyped.getText();
        if (!name.isEmpty() /*&& !email.isEmpty() && Functions.isValid(email)*/) {
            //btnSubGo.setDisable(false);
            lblSubscribeInfo.setText("Valid Information !");
            lblSubscribeInfo.setTextFill(Color.web("#27ae60"));
        }else {
            //btnSubGo.setDisable(true);
            lblSubscribeInfo.setText("Invalid Information !");
            lblSubscribeInfo.setTextFill(Color.web("#e67e22"));
            Functions.Shaker shaker = new Functions.Shaker(txtEmailTyped);
            shaker.shake();
        }
    }

    public void txtEmailReleased(KeyEvent keyEvent) {
        String email = txtEmailTyped.getText();
        String name = txtNameTyped.getText();
        if (!name.isEmpty() /*&& !email.isEmpty() && Functions.isValid(email)*/) {
            //btnSubGo.setDisable(false);
            lblSubscribeInfo.setText("Valid Information !");
            lblSubscribeInfo.setTextFill(Color.web("#27ae60"));
        }else {
            //btnSubGo.setDisable(true);
            lblSubscribeInfo.setText("Invalid Information !");
            lblSubscribeInfo.setTextFill(Color.web("#e67e22"));
            Functions.Shaker shaker = new Functions.Shaker(txtEmailTyped);
            shaker.shake();
        }
    }

    public void btnSubGo(ActionEvent actionEvent) throws IOException {
        name = txtNameTyped.getText();
        //email = txtEmailTyped.getText();
        if (!name.isEmpty() /*&& !email.isEmpty() && Functions.isValid(email)*/) {
            Parent root = FXMLLoader.load(getClass().getResource("/inscription_pin.fxml"));
            Login.loginStackPanee.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(800),root);
            ft.setOnFinished(event -> {
                Login.loginStackPanee.getChildren().remove(inscriptionEmailPane);
            });
            ft.setNode(inscriptionEmailPane);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }else {
            Functions.Shaker shaker = new Functions.Shaker(txtNameTyped);
            shaker.shake();
        }
    }

    public void btnSubBack(ActionEvent actionEvent) throws IOException {
        Stage s = (Stage) inscriptionEmailPane.getScene().getWindow();
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
    }
}