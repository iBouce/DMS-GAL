package com.ibouce.dms;

import database.mysqlConnection;
import function.Functions;
import insidefx.undecorator.UndecoratorScene;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static database.mysqlConnection.*;
import static com.ibouce.dms.Login.mainStage;
import static com.ibouce.dms.Principal.BorderPanee;
import static com.ibouce.dms.Principal.principalPanee;
import static function.Functions.countFilesInDirectory;
import static function.Functions.repositoryDir;
import static properties.properties.readPropConfig;
import static properties.properties.readPropLogin;

public class Dashboard implements Initializable {

    public PieChart pieChart;
    public AnchorPane chartPane;
    public LineChart<Number,Number> lineChart;
    public Label lblCountUsers;
    public AnchorPane dashboardPane;
    public Label lblMysqlConnStatus;
    public Button btnCheckServer;

    private static final Integer STARTTIME = 30;
    public Label lblTotalDoc;
    public Label btnDocUploaded;
    public Label btnDocDownload;
    public TextArea txtLog;
    public Label lblCountDocType;
    public AnchorPane anchorPaneNews;
    public Label lblNews;
    public Button btnManageUsers;
    public static Button btnManageUserss;
    public ListView listViewReminder;
    public AnchorPane paneLogInfo;
    public ImageView companyImage;
    private Timeline timeline;
    private Integer timeSeconds = STARTTIME;

    Connection conn;

    public Dashboard(){
        conn = mysqlConnection.getConnect();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnManageUserss = btnManageUsers;
        Platform.runLater(() -> {
            checkRoles();
            selectCompanyImage(companyImage);
            Functions.Logger.readLog(txtLog);
            isDBConnected();
            populatePieChart();
            populateLineChart();
            lblCountDocType.setText(String.valueOf(selectTotalDocType()));
            lblCountUsers.setText(String.valueOf(selectTotalUsers()));
            lblTotalDoc.setText(String.valueOf(selectTotalDocs()));
            btnDocUploaded.setText(""+selectUploadedDocs(selectUsersID(Integer.valueOf(readPropLogin()))));
            btnDocDownload.setText(String.valueOf(countFilesInDirectory(repositoryDir)));
        });
    }

    public void mysqlCheckTimer(){
        if (timeline != null) {
            timeline.stop();
        }
        timeSeconds = STARTTIME;
        btnCheckServer.setText(timeSeconds.toString());
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        // KeyFrame event handler
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (EventHandler) event1 -> {
                            timeSeconds--;
                            // update timerLabel
                            btnCheckServer.setText("Refresh in "+timeSeconds.toString());
                            if (timeSeconds <= 0) {
                                timeline.stop();
                            }
                        }));
        timeline.playFromStart();
    }

    public void checkMysqlConn(){
        final Connection[] conn = {null};
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                Platform.runLater(() -> {
                    lblMysqlConnStatus.setText("Status");
                    conn[0] = mysqlConnection.getConnect();
                });
                return null;
            }
            @Override
            public void done(){
                Platform.runLater(() -> {
                    if (conn[0] == null){
                        lblMysqlConnStatus.setText("Off");
                        lblMysqlConnStatus.setTextFill(Color.web("#e74c3c"));
                    }else{
                        lblMysqlConnStatus.setText("On");
                        lblMysqlConnStatus.setTextFill(Color.web("#27ae60"));
                    }
                });
            }
        };
        sw.execute();
    }

    public void isDBConnected(){
        PauseTransition wait = new PauseTransition(Duration.seconds(STARTTIME));
        wait.setOnFinished((e) -> {
            Platform.runLater(() -> {
                checkMysqlConn();
                mysqlCheckTimer();
            });
            wait.playFromStart();
        });
        wait.play();
    }

    public void populatePieChart() {
        File file = new File("c:");
        long totalSpace = file.getTotalSpace(); //total disk space in bytes.
        long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
        long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Total Space", totalSpace),
                new PieChart.Data("Free Space", freeSpace));
        pieChart.setData(pieChartData);
    }

    public void populateLineChart() {

        XYChart.Series series1 = new XYChart.Series();
        lineChart.getData().addAll(series1);
        XYChart.Data<Integer, Integer> data = new XYChart.Data();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT doc_date_modify, count(*) " +
                    "FROM document GROUP BY MONTH(doc_date_modify) ORDER BY doc_date_modify ASC");
            while(rs.next()) {
                series1.getData().add(new XYChart.Data(String.valueOf(rs.getDate(1)),rs.getInt(2)));
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
        //series1.getData().add(data);

        /*data.getNode().setOnMouseClicked(e -> {
            System.out.println("Click on data " + data.getXValue() +" "+ data.getYValue());
        });
        data.getNode().setOnMouseEntered(event -> {
            //lineChart.setTitle("Click on data " +  "[" + data.getXValue() + ", "+ data.getYValue() +"]");
            data.getNode().setStyle("-fx-background-color : #27ae60;");
        });
        data.getNode().setOnMouseExited(event -> {
            //lineChart.setTitle("");
            data.getNode().setStyle("-fx-background-color : #34495e;");
        });*/

        //sc.setAnimated(false);
        //sc.setCreateSymbols(true);
        //sc.prefWidthProperty().bind(chartPane.widthProperty());
        //sc.prefHeightProperty().bind(chartPane.heightProperty());
        //chartPane.getChildren().addAll(sc);

    }

    public void btnCheckServer(ActionEvent actionEvent) {
        checkMysqlConn();
    }

    public void btnContactUsers(ActionEvent actionEvent) throws IOException {
        BoxBlur blur = new BoxBlur(5,5,5);
        BorderPanee.setEffect(blur);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(mainStage);
        stage.setResizable(false);
        stage.setOnHiding( event -> {BorderPanee.setEffect(null);} );
        Region root = FXMLLoader.load(getClass().getResource("/messages.fxml"));
        final UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
        stage.setScene(undecoratorScene);
        stage.show();
    }

    public void btnAddDocument(ActionEvent actionEvent) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/archive.fxml"));
            principalPanee.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnOpenRepository(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().open(new File(repositoryDir));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void btnAddReminder(ActionEvent actionEvent) throws IOException {
        BoxBlur blur = new BoxBlur(5,5,5);
        BorderPanee.setEffect(blur);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(mainStage);
        stage.setResizable(false);
        stage.setOnHiding( event -> {BorderPanee.setEffect(null);} );
        Region root = FXMLLoader.load(getClass().getResource("/reminder.fxml"));
        final UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
        stage.setScene(undecoratorScene);
        stage.show();
    }

    public void btnManageUsers(ActionEvent actionEvent) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/user.fxml"));
        principalPanee.getChildren().setAll(pane);
    }

    public static void checkRoles(){

        if(selectUserStatus(Integer.valueOf(readPropConfig("login"))).equals(true)){
            System.out.println("status true");
            if(selectUserType(Integer.valueOf(readPropConfig("login"))).equals("Administrator")){
                btnManageUserss.setVisible(true);
                System.out.println("btn visible true");
            }else{
                btnManageUserss.setVisible(false);
                System.out.println("btn visible false");
            }
        }else{
            System.out.println("status false");
        }
    }

}
