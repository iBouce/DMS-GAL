package com.ibouce.dms;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LicencePricing implements Initializable {

    public StackPane stackContainer;
    public static StackPane stackContainerr;
    public AnchorPane pricingPane;
    public Button btnUltimate;
    public Button btnDemo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stackContainerr=stackContainer;
    }

    public void btnUltimate(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/licence.fxml"));
        stackContainerr.getChildren().add(root);
        FadeTransition ft = new FadeTransition(Duration.millis(500),root);
        ft.setOnFinished(event -> {
            stackContainerr.getChildren().remove(pricingPane);
        });
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public void btnDemo(ActionEvent actionEvent) throws InterruptedException {
        //When t1 finish Create SQLite Tables, t2 start open Login
        Thread t1 = new Thread(() -> {
            //sqliteCreateTables();
            JOptionPane.showMessageDialog(null,"MenBa3d Nakhdemha !");
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
                            stackContainerr.getChildren().add(root);
                            FadeTransition ft = new FadeTransition(Duration.millis(600),root);
                            ft.setOnFinished(event -> {
                                stackContainerr.getChildren().remove(pricingPane);
                            });
                            ft.setFromValue(0);
                            ft.setToValue(1);
                            ft.play();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        t1.start();
        t1.join();
        t2.start();
        t2.join();
    }

}