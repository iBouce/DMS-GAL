package com.ibouce.dms;

import database.mysqlConnection;
import function.Functions;
import insidefx.undecorator.UndecoratorScene;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import static database.mysqlConnection.*;
import static properties.properties.readPropConfig;

public class Login implements Initializable {

    public AnchorPane mainPane;
    public TextField txtFirstNumber;
    public TextField txtSecondNumber;
    public TextField txtThirdNumber;
    public TextField txtFourthNumber;
    public Text txtUserNameAndType;
    public AnchorPane loginPane;
    public static Stage mainStage;
    public Hyperlink hyperlinkNewAccount;
    public StackPane loginStackPane;
    public static StackPane loginStackPanee;
    public ProgressIndicator progressBar;
    public String name;
    public ImageView imageProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginStackPanee=loginStackPane;
        Functions.addTextLimiter(txtFirstNumber,1);
        Functions.addTextLimiter(txtSecondNumber,1);
        Functions.addTextLimiter(txtThirdNumber,1);
        Functions.addTextLimiter(txtFourthNumber,1);
        Functions.addTextRestriction(txtFirstNumber);
        Functions.addTextRestriction(txtSecondNumber);
        Functions.addTextRestriction(txtThirdNumber);
        Functions.addTextRestriction(txtFourthNumber);

        //Check prop-config.xml login value if 0 OR user table rows 0 disable text fields
        Platform.runLater(() -> {
            if (readPropConfig("login").equals("0") /*|| selectUsersID() == 0*/){
                txtFirstNumber.setDisable(true);txtSecondNumber.setDisable(true);
                txtThirdNumber.setDisable(true);txtFourthNumber.setDisable(true);
                txtUserNameAndType.setText("Create Account");
            }else {
                txtFirstNumber.setDisable(false);txtSecondNumber.setDisable(false);
                txtThirdNumber.setDisable(false);txtFourthNumber.setDisable(false);
                hyperlinkNewAccount.setDisable(true);
                checkMysqlConn();
                txtUserNameAndType.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
                    if(t.getCode()== KeyCode.ENTER) {
                        checkMysqlConn();
                    }
                });
            }
        });
    }

    public void checkMysqlConn(){
        final Connection[] conn = {null};
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                progressBar.setVisible(true);
                txtFirstNumber.setDisable(true);txtSecondNumber.setDisable(true);
                txtThirdNumber.setDisable(true);txtFourthNumber.setDisable(true);
                txtUserNameAndType.setText("Check Server Connection");
                conn[0] = mysqlConnection.getConnect();
                return null;
            }
            @Override
            public void done(){
                progressBar.setVisible(false);
                if (conn[0] == null){
                    txtFirstNumber.setDisable(true);txtSecondNumber.setDisable(true);
                    txtThirdNumber.setDisable(true);txtFourthNumber.setDisable(true);
                    txtUserNameAndType.setText("Server Connection Failed");
                }else{
                    selectProfileImage(Integer.valueOf(readPropConfig("login")),imageProfile);
                    txtFirstNumber.setDisable(false);txtSecondNumber.setDisable(false);
                    txtThirdNumber.setDisable(false);txtFourthNumber.setDisable(false);
                    txtUserNameAndType.setText(selectUserName(Integer.valueOf(readPropConfig("login"))));
                    //SELECT FROM MYSQL DATABASE ID AND PASSWORD and validate login
                    enterDashboard();
                }
            }
        };
        sw.execute();
    }

    public void enterDashboard() {
        Integer id = selectUsersID(Integer.valueOf(readPropConfig("login")));
        String pwd = txtFirstNumber.getText()+txtSecondNumber.getText()+txtThirdNumber.getText()+txtFourthNumber.getText();
        boolean flag;
        flag = validateLogin(id,pwd);
        if (!flag) {
            Functions.Shaker shaker1 = new Functions.Shaker(txtFirstNumber);
            Functions.Shaker shaker2 = new Functions.Shaker(txtSecondNumber);
            Functions.Shaker shaker3 = new Functions.Shaker(txtThirdNumber);
            Functions.Shaker shaker4 = new Functions.Shaker(txtFourthNumber);
            shaker1.shake();
            shaker2.shake();
            shaker3.shake();
            shaker4.shake();
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Stage sstage = (Stage) loginPane.getScene().getWindow();
                        sstage.close();
                        Stage mainStage = new Stage();
                        mainStage.setTitle("Document Management System");
                        mainStage.getIcons().add(new Image("/media/binder_127px.png"));
                        Region root = FXMLLoader.load(getClass().getResource("/principal.fxml"));
                        final UndecoratorScene undecoratorScene = new UndecoratorScene(mainStage, root);
                        undecoratorScene.setFadeInTransition();
                        mainStage.setScene(undecoratorScene);
                        mainStage.setOnCloseRequest(event -> {
                            mainStage.close();
                            System.exit(0);
                        });
                        mainStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void txtFirstNumberKeyReleased(KeyEvent keyEvent) {
        if(txtFirstNumber.getText().equals("")) {
            txtFirstNumber.requestFocus();
            Functions.Shaker shaker = new Functions.Shaker(txtFirstNumber);
            shaker.shake();
        }else{
            txtSecondNumber.requestFocus();
            txtFirstNumber.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
                if(t.getCode()== KeyCode.ENTER) {
                    Platform.runLater(() -> checkMysqlConn());
                }
            });
        }
    }

    public void txtSecondNumberKeyReleased(KeyEvent keyEvent) {
        if(txtSecondNumber.getText().equals("")) {
            txtFirstNumber.requestFocus();
            Functions.Shaker shaker = new Functions.Shaker(txtSecondNumber);
            shaker.shake();
        }else{
            txtThirdNumber.requestFocus();
            txtSecondNumber.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
                if(t.getCode()== KeyCode.ENTER) {
                    Platform.runLater(() -> checkMysqlConn());
                }
            });
        }
    }

    public void txtThirdNumberKeyReleased(KeyEvent keyEvent) {
        if(txtThirdNumber.getText().equals("")) {
            txtSecondNumber.requestFocus();
            Functions.Shaker shaker = new Functions.Shaker(txtThirdNumber);
            shaker.shake();
        }else{
            txtFourthNumber.requestFocus();
            txtThirdNumber.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
                if(t.getCode()== KeyCode.ENTER) {
                    Platform.runLater(() -> checkMysqlConn());
                }
            });
        }
    }

    public void txtFourthNumberKeyReleased(KeyEvent keyEvent) {
        if(txtFourthNumber.getText().equals("")) {
            txtThirdNumber.requestFocus();
            Functions.Shaker shaker = new Functions.Shaker(txtFourthNumber);
            shaker.shake();
        }else {
            Platform.runLater(() -> checkMysqlConn());
            /*txtFourthNumber.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
                if(t.getCode()== KeyCode.ENTER) {
                    Platform.runLater(() -> checkMysqlConn());
                }
            });*/
        }
    }

    public void hyperlinkNewAccount(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/inscription_name.fxml"));
        loginStackPane.getChildren().add(root);
        FadeTransition ft = new FadeTransition(Duration.millis(800), root);
        ft.setOnFinished(event -> {
            loginPane.getChildren().clear();
        });
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public void hyperlinkForgotPwd(ActionEvent actionEvent) {
    }
}
