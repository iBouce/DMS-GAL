package com.ibouce.dms;

import database.mysqlConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.documentModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static function.Functions.repositoryDir;

public class Search implements Initializable {
    public AnchorPane searchPane;
    public TableView<documentModel> tableDoc;
    public TableColumn<documentModel, Integer> colID;
    public TableColumn<documentModel, String> ColName;
    public TableColumn<documentModel, String> colOCR;
    public TableColumn<documentModel, String> colExtension;
    public TableColumn<documentModel, String> colSize;
    public TableColumn<documentModel, Date> colDate;
    public TableColumn<documentModel, Integer> colUser;
    public TableColumn<documentModel, String> colType;
    public TableColumn<documentModel, Blob> colData;
    public TextField txtSearch;
    public Label lblInfo;
    public ProgressBar progressBar;

    ObservableList<documentModel> observableList = FXCollections.observableArrayList();
    Connection conn;

    public Search(){
        conn = mysqlConnection.getConnect();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            txtSearch.requestFocus();
            checkPopulateTable();
        });
    }

    public void searchFilter(){

        FilteredList<documentModel> filteredData = new FilteredList<>(observableList, d -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(documentModel -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (documentModel.getDoc_name().toLowerCase().contains(lowerCaseFilter) ) {
                    return true;
                } /*else if (documentModel.getDoc_ocr().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }*/ else if (String.valueOf(documentModel.getDoc_date()).contains(lowerCaseFilter))
                    return true;
                else
                    return false;
            });
            SortedList<documentModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableDoc.comparatorProperty());
            tableDoc.setItems(sortedData);
            Platform.runLater(() -> lblInfo.setText("Total Files Counts : "+ tableDoc.getItems().size()));
        });

    }

    public void checkPopulateTable(){
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                progressBar.setVisible(true);
                populateTableDoc();
                return null;
            }
            @Override
            public void done(){
                progressBar.setVisible(false);
                /*if (conn[0] == null){
                    System.out.println("Server Connection Failed");
                }else{
                    System.out.println("Server Conn Established");
                    //Populate Data Table document
                    final Task<Void> task = new Task<Void>() {
                        @Override
                        public Void call() {
                            Platform.runLater(() -> {

                            });
                            return null;
                        }
                    };
                    task.setOnSucceeded(e -> {
                        //lblDBInfo.textProperty().unbind();
                        progressBar.setVisible(false);
                    });
                    final Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();
                }*/
            }
        };
        sw.execute();

    }

    public void populateTableDoc(){

        observableList.clear();
        colID.setCellValueFactory(new PropertyValueFactory<>("doc_id"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("doc_user_id"));
        ColName.setCellValueFactory(new PropertyValueFactory<>("doc_name"));
        colOCR.setCellValueFactory(new PropertyValueFactory<>("doc_ocr"));
        colType.setCellValueFactory(new PropertyValueFactory<>("doc_type"));
        colExtension.setCellValueFactory(new PropertyValueFactory<>("doc_extension"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("doc_size"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("doc_date"));
        //colData.setCellValueFactory(new PropertyValueFactory<>("doc_data"));

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT doc_id,user_id,doc_name,doc_ocr,doc_type,doc_extension,doc_size,doc_date_modify FROM document");
            rs = pstmt.executeQuery();

            String name = null;
            while (rs.next()) {

                Statement st = conn.createStatement();
                ResultSet rs2 = st.executeQuery("select user_name from user where user_id = '"+rs.getInt("user_id")+"'");
                while(rs2.next()){
                    name = rs2.getString("user_name");
                }

                observableList.add(new documentModel(
                        rs.getInt("doc_id"),
                        name,
                        rs.getString("doc_name"),
                        rs.getString("doc_ocr"),
                        rs.getString("doc_type"),
                        rs.getString("doc_extension"),
                        rs.getString("doc_size"),
                        rs.getDate("doc_date_modify")
                ));
            }
        }catch (Exception e){
            System.out.println(e);
        }finally {
            try {
                pstmt.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        tableDoc.setItems(observableList);
        Platform.runLater(() -> lblInfo.setText("Total Files Counts : "+ tableDoc.getItems().size()));
    }

    public void txtSearchOnKeyReleased(KeyEvent keyEvent) {
        searchFilter();
        /*String SQL = "Select * from document where doc_name = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(SQL);
            //ResultSet rs = pst.executeQuery();
            pst.setString(1, "%" + txtSearch.getText() + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                observablelist.add(new documentModel(
                        rs.getInt("doc_id"),
                        rs.getInt("user_id"),
                        rs.getString("doc_name"),
                        rs.getString("doc_ocr"),
                        rs.getString("doc_type"),
                        rs.getString("doc_extension"),
                        rs.getString("doc_size"),
                        rs.getDate("doc_date_modify"),
                        rs.getBytes("doc_data")
                ));
            }
            tableDoc.setItems(observablelist);
            Platform.runLater(() -> lblInfo.setText("Total Files Counts : "+ tableDoc.getItems().size()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        populateTableDoc();*/
    }

    public void tableDocOnMouseClicked(MouseEvent mouseEvent) {
        documentModel doc = tableDoc.getSelectionModel().getSelectedItem();

        if (mouseEvent.getClickCount() == 1) {
            if (observableList.isEmpty()){
                lblInfo.setText("Total Files Counts : "+ tableDoc.getItems().size());
            }else{
                File file = new File(repositoryDir +System.getProperty("file.separator")+doc.getDoc_name());
                if (file.exists()){
                    lblInfo.setText("File "+ doc.getDoc_name() +" is already downloaded !");
                }else{
                    lblInfo.setText("Total Files Counts : "+ tableDoc.getItems().size());
                }
            }
        }
        if (mouseEvent.getClickCount() == 2) {
            final Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    ResultSet rs = null;
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM document WHERE doc_id=?");
                        pstmt.setInt(1, doc.getDoc_id());
                        rs = pstmt.executeQuery();
                        File file = new File(repositoryDir +System.getProperty("file.separator")+doc.getDoc_name());
                        Platform.runLater(() -> lblInfo.setText("Downloading file " + file.getAbsolutePath()));
                        while (rs.next()) {
                            InputStream input = rs.getBinaryStream("doc_data");
                            byte byteArray[] = new byte[input.available()];
                            input.read(byteArray);
                            FileOutputStream outPutStream = new FileOutputStream(repositoryDir +System.getProperty("file.separator")+doc.getDoc_name());
                            outPutStream.write(byteArray);
                            Platform.runLater(() -> lblInfo.setText("File ready to open  " + repositoryDir +System.getProperty("file.separator")+doc.getDoc_name()));
                        }
                        Platform.runLater(() -> {
                            try {
                                Desktop.getDesktop().open(new File(repositoryDir +System.getProperty("file.separator")+doc.getDoc_name()));
                            } catch (IOException e) {
                                Platform.runLater(() -> lblInfo.setText("File downloaded but failed to open. No application is associated with the file specified for this operation."));
                            }
                        });
                    } catch (SQLException | IOException e) {
                        System.out.println(e);
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    return null;
                }
            };

            final Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

}
