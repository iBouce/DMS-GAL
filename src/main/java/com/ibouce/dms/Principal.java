package com.ibouce.dms;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static database.mysqlConnection.*;
import static com.ibouce.dms.Login.mainStage;
import static properties.properties.readPropConfig;

public class Principal implements Initializable {
    public AnchorPane principalPane;
    public static AnchorPane principalPanee;
    public BorderPane BorderPane;
    public static BorderPane BorderPanee;
    public Label lblTime;
    public Label lblDate;
    public Label lblUsername;
    public Label lblUserType;
    public ImageView imageProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BorderPanee=BorderPane;
        principalPanee=principalPane;
        Platform.runLater(() -> {
            selectProfileImage(Integer.valueOf(readPropConfig("login")),imageProfile);
            populateTimeDate();
            lblUsername.setText(selectUserName(Integer.valueOf(readPropConfig("login"))));
            lblUserType.setText(selectUserType(Integer.valueOf(readPropConfig("login"))));
            try {
                AnchorPane pane = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
                principalPane.getChildren().setAll(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void populateTimeDate(){
        DateTimeFormatter d = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now1 = LocalDateTime.now();
        lblDate.setText(d.format(now1));

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter d2 = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now2 = LocalDateTime.now();
            lblTime.setText(d2.format(now2));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public void btnDashboard(ActionEvent actionEvent) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
        principalPane.getChildren().setAll(pane);
    }

    public void btnArchive(ActionEvent actionEvent) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/archive.fxml"));
        principalPane.getChildren().setAll(pane);
    }

    public void btnSearch(ActionEvent actionEvent) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/search.fxml"));
        principalPane.getChildren().setAll(pane);
    }

    public void btnSetting(ActionEvent actionEvent) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/setting.fxml"));
        principalPane.getChildren().setAll(pane);
    }

    public void btnAbout(ActionEvent actionEvent) throws IOException {
        BoxBlur blur = new BoxBlur(5,5,5);
        BorderPane.setEffect(blur);
        final double[] xOffset = {0};
        final double[] yOffset = {0};
        Stage stage = new Stage();
        Region root = FXMLLoader.load(getClass().getResource("/about.fxml"));
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(mainStage);
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
