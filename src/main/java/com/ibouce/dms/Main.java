package com.ibouce.dms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader root = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        stage.setScene(new Scene(root.load()));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("DMS");
        stage.getIcons().add(new Image("/media/binder_127px.png"));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}