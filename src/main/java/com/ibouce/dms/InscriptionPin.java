package com.ibouce.dms;

import function.Functions;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InscriptionPin implements Initializable {
    public Button btnSubGo;
    public Button btnSubBack;
    public TextField txtFirstNumber;
    public TextField txtSecondNumber;
    public TextField txtThirdNumber;
    public TextField txtFourthNumber;
    public Label lblSubscribeInfo;
    public AnchorPane pinPane;
    public static String password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblSubscribeInfo.setText(InscriptionName.name);
        Functions.addTextLimiter(txtFirstNumber,1);
        Functions.addTextLimiter(txtSecondNumber,1);
        Functions.addTextLimiter(txtThirdNumber,1);
        Functions.addTextLimiter(txtFourthNumber,1);
        Functions.addTextRestriction(txtFirstNumber);
        Functions.addTextRestriction(txtSecondNumber);
        Functions.addTextRestriction(txtThirdNumber);
        Functions.addTextRestriction(txtFourthNumber);
    }

    public void btnSubGo(ActionEvent actionEvent) throws IOException {
        if(txtFirstNumber.getText().equals("") || txtSecondNumber.getText().equals("")
                || txtThirdNumber.getText().equals("") || txtFourthNumber.getText().equals("")){
            Functions.Shaker shaker1 = new Functions.Shaker(txtFirstNumber);
            Functions.Shaker shaker2 = new Functions.Shaker(txtSecondNumber);
            Functions.Shaker shaker3 = new Functions.Shaker(txtThirdNumber);
            Functions.Shaker shaker4 = new Functions.Shaker(txtFourthNumber);
            shaker1.shake();
            shaker2.shake();
            shaker3.shake();
            shaker4.shake();
        }else{
            password = txtFirstNumber.getText()+txtSecondNumber.getText()+txtThirdNumber.getText()+txtFourthNumber.getText();
            Parent root = FXMLLoader.load(getClass().getResource("/setup_database.fxml"));
            Login.loginStackPanee.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(1000),root);
            ft.setOnFinished(event -> {
                Login.loginStackPanee.getChildren().remove(pinPane);
            });
            ft.setNode(pinPane);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    public void btnSubBack(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/inscription_name.fxml"));
        Login.loginStackPanee.getChildren().add(root);
        FadeTransition ft = new FadeTransition(Duration.millis(1000),root);
        ft.setOnFinished(event -> {
            Login.loginStackPanee.getChildren().remove(pinPane);
        });
        ft.setNode(pinPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public void txtFirstNumberKeyReleased(KeyEvent keyEvent) {
        if(txtFirstNumber.getText().equals("")) {
            txtFirstNumber.requestFocus();
            Functions.Shaker shaker = new Functions.Shaker(txtFirstNumber);
            shaker.shake();
        }else{
            txtSecondNumber.requestFocus();
        }
    }

    public void txtSecondNumberKeyReleased(KeyEvent keyEvent) {
        if(txtSecondNumber.getText().equals("")) {
            txtFirstNumber.requestFocus();
            Functions.Shaker shaker = new Functions.Shaker(txtSecondNumber);
            shaker.shake();
        }else{
            txtThirdNumber.requestFocus();
        }
    }

    public void txtThirdNumberKeyReleased(KeyEvent keyEvent) {
        if(txtThirdNumber.getText().equals("")) {
            txtSecondNumber.requestFocus();
            Functions.Shaker shaker = new Functions.Shaker(txtThirdNumber);
            shaker.shake();
        }else{
            txtFourthNumber.requestFocus();
        }
    }

    public void txtFourthNumberKeyReleased(KeyEvent keyEvent) {
        if(txtFourthNumber.getText().equals("")) {
            txtThirdNumber.requestFocus();
            Functions.Shaker shaker = new Functions.Shaker(txtFourthNumber);
            shaker.shake();
        }
    }

}
