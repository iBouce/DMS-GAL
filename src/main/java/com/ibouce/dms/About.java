package com.ibouce.dms;

import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.ibouce.dms.Principal.BorderPanee;

public class About implements Initializable {
    public AnchorPane aboutPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aboutPane.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
            if(t.getCode()== KeyCode.ESCAPE) {
                BorderPanee.setEffect(null);
                Stage stage = (Stage) aboutPane.getScene().getWindow();
                stage.close();
            }
        });
    }
}
