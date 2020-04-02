package com.ibouce.dms;

import database.mysqlConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.documentModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

import static database.mysqlConnection.*;
import static function.Functions.*;
import static properties.properties.readPropLogin;

public class Archive implements Initializable {

    public AnchorPane archivePane;
    public VBox vboxDragDrop;
    public ProgressIndicator progressBar;
    public Button btnArchive;
    public TabPane tabPane;
    public Tab tabImport;
    public Tab tabPopulateTable;
    public ListView listView;
    public Button btnPopulateTable;
    public TextField txtTypeDoc;
    public ListView<String> listViewTypeDoc;
    public Label lblInfo;
    public ComboBox cbDocumentType;
    public TableView<documentModel> tableDoc;
    public TableColumn<documentModel, Integer> colID;
    public TableColumn<documentModel, String> colDocType;
    public TableColumn<documentModel, String> colDocName;
    public TableColumn<documentModel, String> colDocOCR;
    public TableColumn<documentModel, String> colDocExtension;
    public TableColumn<documentModel, String> colDocSize;
    public TableColumn<documentModel, Date> colDocDate;
    public TextArea txtLogInfo;
    public ImageView imgDoc;
    public Label lblDocID;
    public ComboBox cbDocType;
    public TextField txtDocName;
    public TextArea txtDocOCR;
    public Label lblIDocExtension;
    public Label lblIDocSize;
    public Label lblDocDate;
    public TextField txtSearch;
    public ProgressBar progressbarImport;

    public static List<File> selectedFiles;
    public static File file;
    public VBox paneOverview;


    Connection conn;
    ObservableList<documentModel> observableList = FXCollections.observableArrayList();

    public Archive(){
        conn = mysqlConnection.getConnect();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            cbDocumentType.requestFocus();
            populateTypeDocListView();
            checkPopulateTable();
        });
        text();
        vboxDragDrop.setOnDragOver(event -> {
            if (event.getGestureSource() != vboxDragDrop && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        vboxDragDrop.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                listView.getItems().clear();
                    for (File file:db.getFiles()) {
                        listView.getItems().add(file.getAbsolutePath() + " - " + getFileSizeMegaBytes(file));
                        //lblInfo.setText("Total Document count : "+ selectedFiles.size());
                        //Insert Document
                        try {
                            PreparedStatement ps = conn.prepareStatement("INSERT INTO document(user_id,doc_name,doc_ocr,doc_type,doc_extension,doc_size,doc_date_modify,doc_data) VALUES(?,?,?,?,?,?,?,?)");
                            ps.setInt(1, selectUsersID(Integer.valueOf(readPropLogin())));
                            ps.setString(2, file.getName());
                            ps.setString(3, "Empty");
                            ps.setString(4, listViewTypeDoc.getSelectionModel().getSelectedItem());
                            ps.setString(5, fileExtension(file.getAbsolutePath()));
                            ps.setString(6, getFileSizeMegaBytes(file));
                            ps.setDate(7, new Date(file.lastModified()));
                            ps.setBytes(8, readFile(file.getAbsolutePath()));
                            ps.executeUpdate();
                            ps.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        btnArchive.setDisable(false);
                    }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
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
            }
        };
        sw.execute();
    }

    public void populateTableDoc(){

        observableList.clear();
        colID.setCellValueFactory(new PropertyValueFactory<>("doc_id"));
        //colUser.setCellValueFactory(new PropertyValueFactory<>("doc_user_id"));
        colDocName.setCellValueFactory(new PropertyValueFactory<>("doc_name"));
        colDocOCR.setCellValueFactory(new PropertyValueFactory<>("doc_ocr"));
        colDocType.setCellValueFactory(new PropertyValueFactory<>("doc_type"));
        colDocExtension.setCellValueFactory(new PropertyValueFactory<>("doc_extension"));
        colDocSize.setCellValueFactory(new PropertyValueFactory<>("doc_size"));
        colDocDate.setCellValueFactory(new PropertyValueFactory<>("doc_date"));
        //colData.setCellValueFactory(new PropertyValueFactory<>("doc_data"));

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT doc_id,user_id,doc_name,doc_ocr,doc_type,doc_extension,doc_size,doc_date_modify FROM document where user_id = '"+readPropLogin()+"'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                observableList.add(new documentModel(
                        rs.getInt("doc_id"),
                        rs.getString("user_id"),
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

    public void populateTypeDocListView(){
        selectDocTypeName(listViewTypeDoc,cbDocumentType, Integer.valueOf(readPropLogin()));
        txtSearch.requestFocus();
    }

    public void btnImportDocs(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        if (cbDocumentType.getSelectionModel().getSelectedItem() == null || cbDocumentType.getSelectionModel().getSelectedItem().equals("Document Type")){
            System.out.println("Select document type to continue importing !");
        }else{
            //Insert Data
            Platform.runLater(() -> {
                lblInfo.setText("");
                selectedFiles = fileChooser.showOpenMultipleDialog(null);
                if (selectedFiles != null && selectedFiles.size() > 0) {
                    listView.getItems().clear();
                    for (File selectedFile : selectedFiles) {
                        File file = new File(String.valueOf(selectedFile));
                        listView.getItems().add(cbDocumentType.getValue() + " - " + file.getAbsolutePath() + " - " + getFileSizeMegaBytes(file));
                        //Insert Document
                        Platform.runLater(() -> {
                            try {
                                PreparedStatement ps = conn.prepareStatement("INSERT INTO document(user_id,doc_name,doc_ocr,doc_type,doc_extension,doc_size,doc_date_modify,doc_data) VALUES(?,?,?,?,?,?,?,?)");
                                ps.setInt(1, selectUsersID(Integer.valueOf(readPropLogin())));
                                ps.setString(2, file.getName());
                                ps.setString(3, "Empty");
                                ps.setString(4, String.valueOf(cbDocumentType.getSelectionModel().getSelectedItem()));
                                ps.setString(5, fileExtension(file.getAbsolutePath()));
                                ps.setString(6, getFileSizeMegaBytes(file));
                                ps.setDate(7, new Date(file.lastModified()));
                                ps.setBytes(8, readFile(file.getAbsolutePath()));
                                ps.executeUpdate();
                                ps.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            btnArchive.setDisable(false);
                            Platform.runLater(() -> progressbarImport.setVisible(false));
                        });
                    }
                } else {
                    progressbarImport.setVisible(false);
                    lblInfo.setText("File is not Valid !");
                }
            });
        }
    }

    public void btnArchiveOnAction(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            txtSearch.requestFocus();
            btnArchive.setDisable(true);
            listView.getItems().clear();
            tabPane.getSelectionModel().select(tabPopulateTable);
            lblInfo.setText("");
            checkPopulateTable();
        });
    }

    public void btnAddTypeDocOnAction(ActionEvent actionEvent) {
        insertDocType(selectUsersID(Integer.valueOf(readPropLogin())),txtTypeDoc.getText());
        populateTypeDocListView();
    }

    public void btnEditDocument(ActionEvent actionEvent) {
        String value = lblDocID.getText();
        String sql = "UPDATE document SET doc_name = ?,doc_ocr = ? WHERE doc_id = '"+value+"'";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtDocName.getText());
            pstmt.setString(2, txtDocOCR.getText());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        checkPopulateTable();
    }

    public void btnDeleteDocument(ActionEvent actionEvent) {
        String value = lblDocID.getText();
        try {
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM document WHERE doc_id='"+value+"'";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        populateTableDoc();
    }

    public void tableDocOnMouseClicked(MouseEvent mouseEvent) {
        documentModel doc = tableDoc.getSelectionModel().getSelectedItem();
        if (mouseEvent.getClickCount() == 1) {
            if (observableList.isEmpty()){
                lblInfo.setText("Total Files Counts : "+ tableDoc.getItems().size());
            }else{
                if (doc.getDoc_extension().equals("jpg") || doc.getDoc_extension().equals("jpeg")){
                    imgDoc.setImage(new Image("/media/jpg_127px.png"));
                }else if(doc.getDoc_extension().equals("png")){
                    imgDoc.setImage(new Image("/media/png_127px.png"));
                } else if(doc.getDoc_extension().equals("pdf")) {
                    imgDoc.setImage(new Image("/media/pdf_127px.png"));
                }else if(doc.getDoc_extension().equals("mp3")) {
                    imgDoc.setImage(new Image("/media/mp3_127px.png"));
                }else if(doc.getDoc_extension().equals("docx") || doc.getDoc_extension().equals("doc")) {
                    imgDoc.setImage(new Image("/media/microsoft_word_127px.png"));
                }else if(doc.getDoc_extension().equals("xslx") || doc.getDoc_extension().equals("xls")) {
                    imgDoc.setImage(new Image("/media/microsoft_excel_127px.png"));
                }else if(doc.getDoc_extension().equals("pptx") || doc.getDoc_extension().equals("ppt")) {
                    imgDoc.setImage(new Image("/media/microsoft_powerpoint_127px.png"));
                }else if(doc.getDoc_extension().equals("txt")) {
                    imgDoc.setImage(new Image("/media/txt_127px.png"));
                }else if(doc.getDoc_extension().equals("zip")) {
                    imgDoc.setImage(new Image("/media/zip_127px.png"));
                }else {
                    imgDoc.setImage(new Image("/media/file_127px.png"));
                }

                File file = new File(repositoryDir +System.getProperty("file.separator")+doc.getDoc_name());
                if (file.exists()){
                    lblDocID.setText(String.valueOf(doc.getDoc_id()));
                    cbDocType.setValue(doc.getDoc_type());
                    txtDocName.setText(doc.getDoc_name());
                    txtDocOCR.setText(doc.getDoc_ocr());
                    lblIDocExtension.setText(doc.getDoc_extension());
                    lblIDocSize.setText(doc.getDoc_size());
                    lblDocDate.setText(String.valueOf(doc.getDoc_date()));
                    lblInfo.setText("File "+ doc.getDoc_name() +" is already downloaded !");
                }else{
                    lblDocID.setText(String.valueOf(doc.getDoc_id()));
                    cbDocType.setValue(doc.getDoc_type());
                    txtDocName.setText(doc.getDoc_name());
                    txtDocOCR.setText(doc.getDoc_ocr());
                    lblIDocExtension.setText(doc.getDoc_extension());
                    lblIDocSize.setText(doc.getDoc_size());
                    lblDocDate.setText(String.valueOf(doc.getDoc_date()));
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

    public void btnPopulateTableOnAction(ActionEvent actionEvent) {
        tabPane.getSelectionModel().select(tabPopulateTable);
        checkPopulateTable();
    }

    public void txtSearchOnKeyReleased(KeyEvent keyEvent) {
        searchFilter();
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

    public void text(){

    }

}
