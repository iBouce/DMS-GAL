package com.ibouce.dms;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTimePicker;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class Reminder implements Initializable {

    public JFXDatePicker date;
    public JFXTimePicker time;
    public JFXTextArea txtNote;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void btnSaveReminder(ActionEvent actionEvent) {
    }

    public void btnEditReminder(ActionEvent actionEvent) {
    }

    public void btnDeleteReminder(ActionEvent actionEvent) {
    }

}
