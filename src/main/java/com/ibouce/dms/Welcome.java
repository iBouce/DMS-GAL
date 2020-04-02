package com.ibouce.dms;

import com.jfoenix.controls.JFXProgressBar;
import function.Functions;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import properties.AES;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static function.Functions.*;
import static function.Functions.Logger.log;
import static properties.UUID.uuid;
import static properties.properties.createProp;
import static properties.properties.readPropConfig;

public class Welcome implements Initializable {

    public StackPane stackContainer;
    public AnchorPane rootPane;
    public Label lblInformation;
    public ProgressBar progressBar;
    public JFXProgressBar progressBarr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Check Licence from prop-config.xml file
        //Functions.Logger.log(DateTimeFormatter()+ " " +"Check Licence Key .../prop-config.fxml");
        Platform.runLater(() -> checkLicence());
    }

    public void checkLicence() {

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws InterruptedException {
                setProgress(0);
                //Check Directory
                File directory = new File(rootDir);
                if (directory.exists() && directory.isDirectory() && directory.length() != 0){
                    Platform.runLater(() -> lblInformation.setText("Checking App Directory... "+directory.getName()));
                }else {
                    directory.mkdir();
                    createRepositoryDir();
                    log("Logger file created !");
                }
                Thread.sleep(500);
                //Check Files
                File file = new File(propDir);
                if (file.exists() && !file.isDirectory() && file.length() != 0){
                    Platform.runLater(() -> lblInformation.setText("Uploading App Data... "+file.getName()));
                }else {
                    createProp();
                }
                Thread.sleep(250);
                String res = "Finished Execution";
                return res;
            }

            @Override
            public void done(){
                Platform.runLater(() -> progressBar.setProgress(Double.valueOf(100.0)));
                if (readPropConfig("licence_key").equals(AES.encrypt(uuid(), "iBouce@2710"))){
                    //Open Login
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
                                Scene scene = rootPane.getScene();
                                root.translateYProperty().set(scene.getHeight());
                                stackContainer.getChildren().add(root);
                                Timeline timeline = new Timeline();
                                KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
                                KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
                                timeline.getKeyFrames().add(kf);
                                timeline.setOnFinished(t -> {
                                    stackContainer.getChildren().remove(rootPane);
                                });
                                timeline.play();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            lblInformation.setText("Prop-config.xml file exist - Licence Key = True");
                        }
                    });
                    Platform.runLater(() -> lblInformation.setText("Connection Established !"));
                }else {
                    //Open Licence Pricing
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Parent root = FXMLLoader.load(getClass().getResource("/licence_pricing.fxml"));
                                Scene scene = rootPane.getScene();
                                root.translateYProperty().set(scene.getHeight());
                                stackContainer.getChildren().add(root);
                                Timeline timeline = new Timeline();
                                KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
                                KeyFrame kf = new KeyFrame(Duration.millis(700), kv);
                                timeline.getKeyFrames().add(kf);
                                timeline.setOnFinished(t -> {
                                    stackContainer.getChildren().remove(rootPane);
                                });
                                timeline.play();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            lblInformation.setText("Prop-config.xml file exist - Licence Key = False");
                        }
                    });
                    Platform.runLater(() -> lblInformation.setText("Licence Key Missing !"));
                }

            }
        };
        sw.execute();

    }

}