package model;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

import static com.ibouce.dms.Principal.principalPanee;
import static database.mysqlConnection.deleteUser;
import static database.mysqlConnection.updateUser;

public class userModel {

    Integer userID;
    String userName, password, type;
    Boolean status;
    Button btnProfile,btnEdit, btnDelete;
    HBox action;

    public userModel(Integer userid, String name, String password, String type, Boolean status , HBox action){
        this.userID = userid;
        this.userName = name;
        this.password = password;
        this.type = type;
        this.status = status;
        this.action = action;

        btnProfile = new Button("Profile");
        btnProfile.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        btnProfile.setStyle("-fx-background-color: #ecf0f1;");
        btnProfile.setOnAction(event -> {
            AnchorPane pane = null;
            try {
                pane = FXMLLoader.load(getClass().getResource("/profile.fxml"));
                principalPanee.getChildren().setAll(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnEdit = new Button("Edit");
        btnEdit.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        btnEdit.setStyle("-fx-background-color: #3498db;");
        btnEdit.setOnAction(event -> {
            updateUser(getUserID(),getUserName(),getPassword(), getType(),getStatus());
        });

        btnDelete = new Button("Delete");
        btnDelete.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        btnDelete.setStyle("-fx-background-color: #e74c3c;");
        btnDelete.setOnAction(event -> {
            deleteUser(getUserID());
        });

        action.setAlignment(Pos.CENTER);
        action.setSpacing(5);
        action.setPadding(new Insets(5, 0, 5, 0));
        action.getChildren().addAll(btnProfile,btnEdit, btnDelete);

    }


    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public HBox getAction() {
        return action;
    }

    public void setAction(HBox action) {
        this.action = action;
    }

}