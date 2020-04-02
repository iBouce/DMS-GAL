package com.ibouce.dms;

import database.mysqlConnection;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import model.userModel;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static database.mysqlConnection.updateUser;

public class user implements Initializable {

    public TableView<userModel> tableUser;
    public TableColumn<userModel, Integer> colID;
    public TableColumn<userModel, String> colUsername;
    public TableColumn<userModel, String> colPassword;
    public TableColumn<userModel, String> coleRole;
    public TableColumn<userModel, Boolean> colStatus;
    public TableColumn colAction;
    public Label lblInfo;
    public Button btnSave;
    public Button btnPrint;

    ObservableList<userModel> observableList = FXCollections.observableArrayList();
    ObservableList<String> ComboBoxObservableList = FXCollections.observableArrayList();
    Connection conn;

    public user(){
        conn = mysqlConnection.getConnect();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {initTable();populateTableUser();});
        ComboBoxObservableList.addAll("Administrator","User");
    }

    private void initTable(){
        initCols();
    }

    private void initCols(){
        colID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        coleRole.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        editableCols();
    }

    private void editableCols(){
        colUsername.setCellFactory(TextFieldTableCell.forTableColumn());
        colUsername.setOnEditCommit((TableColumn.CellEditEvent<userModel, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setUserName(t.getNewValue());
            //btnSave.setDisable(false);
        });

        colPassword.setCellFactory(TextFieldTableCell.forTableColumn());
        colPassword.setOnEditCommit((TableColumn.CellEditEvent<userModel, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setPassword(t.getNewValue());
        });

        colStatus.setCellValueFactory(c -> {
                    userModel user = c.getValue();
                    CheckBox checkBox = new CheckBox();
                    checkBox.getStylesheets().add(getClass().getResource("/css/checkbox.css").toExternalForm());
                    checkBox.selectedProperty().setValue(user.getStatus());
                    checkBox.selectedProperty().addListener((ov, old_val, new_val) -> user.setStatus(new_val));
                    return new SimpleObjectProperty(checkBox);
        });


        coleRole.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(),ComboBoxObservableList));
        coleRole.setOnEditCommit((TableColumn.CellEditEvent<userModel, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setType(t.getNewValue());
        });

        tableUser.setEditable(true);
    }

    public void populateTableUser() {

        observableList.clear();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM user");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                observableList.add(new userModel(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_password"),
                        rs.getString("user_type"),
                        rs.getBoolean("user_status"),
                        new HBox()
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

        tableUser.setItems(observableList);
        Platform.runLater(() -> lblInfo.setText("Total Users Counts : "+ tableUser.getItems().size()));
    }

    public void btnSave(ActionEvent actionEvent) {
        System.out.println("Username : " + tableUser.getSelectionModel().getSelectedItem().getUserName());
    }

    public void btnPrint(ActionEvent actionEvent) {

    }

}