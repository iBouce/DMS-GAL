package function;

import insidefx.undecorator.UndecoratorScene;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Pattern;

//import static test.Test.lblFileSizeProgresss;

public class Functions {

    public static boolean createRootDir() throws IOException {
        File theDir = new File(System.getenv("APPDATA")+System.getProperty("file.separator")+"iBouceAppDev");
        boolean result = true;
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            result = true;
            try{
                theDir.mkdir();
                result = false;
            }
            catch(SecurityException se){
                //handle it
            }
        }
        return result;
    }

    public static void openFXML(String fxmlpath){

        try {
            Region root = FXMLLoader.load(Application.class.getResource(fxmlpath));
            //Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage stage = new Stage();
            final UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
            undecoratorScene.setFadeInTransition();
            stage.setScene(undecoratorScene);
            stage.show();

            /*
            Parent blah = FXMLLoader.load(getClass().getResource(fxmlpath));
            Scene scene = new Scene(blah);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(scene);
            appStage.show();
            */
            /*
            //Stage sstage = (Stage) loginPane.getScene().getWindow();
            //sstage.close();
            Stage mainStage = new Stage();
            mainStage.setTitle("Document Management System");
            mainStage.getIcons().add(new Image("/media/binder_127px.png"));
            Region root = FXMLLoader.load(Application.class.getResource("/principal.fxml"), null, new JavaFXBuilderFactory());
            final UndecoratorScene undecoratorScene = new UndecoratorScene(mainStage, root);
            undecoratorScene.setFadeInTransition();
            mainStage.setScene(undecoratorScene);
            mainStage.setOnCloseRequest(eventt -> {
                mainStage.close();
                System.exit(0);
            });
            mainStage.show();
            */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Creates parent directories if necessary. Then returns file */
    public static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory + System.getProperty("file.separator") + filename);
    }

    public static String rootDir = System.getenv("APPDATA") + System.getProperty("file.separator")+"iBouceAppDev";
    public static String repositoryDir = System.getenv("APPDATA") + System.getProperty("file.separator")+"iBouceAppDev" +System.getProperty("file.separator")+"repository";
    public static String propDir = System.getenv("APPDATA") + System.getProperty("file.separator")+"iBouceAppDev" +System.getProperty("file.separator")+"prop-config.xml";
    public static String loggerDir = System.getenv("APPDATA") + System.getProperty("file.separator")+"iBouceAppDev" +System.getProperty("file.separator")+"logger.txt";

    public static String createRepositoryDir() {
        File theDir = new File(""+ repositoryDir);
        String dir = "Repository Directory - " + theDir.getAbsolutePath();
        if (!theDir.exists()) {
            System.out.println("Creating directory: " + theDir.getName());
            boolean result = false;
            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                System.out.printf(String.valueOf(se));
                dir = String.valueOf(se);
            }
            if(result) {
                dir = theDir.getAbsolutePath();
                System.out.printf("Directory created ",dir);
            }
        }
        return dir;
    }

    public static class Logger {
        public static void log(String message) {
            PrintWriter out = null;
            try {
                out = new PrintWriter(new FileWriter(loggerDir, true), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.write("\n"+message);
            out.close();
        }

        public static void readLog(TextArea txtArea){
            try {
                Scanner s = new Scanner(new File(loggerDir)).useDelimiter("\\s+");
                while (s.hasNext()) {
                    txtArea.setText(txtArea.getText() + " " + s.nextLine()+"\n");
                    /*if (s.hasNextInt()) {
                        txtArea.appendText(s.nextInt() + " ");
                    } else {
                        txtArea.appendText(s.next() + " ");
                    }*/
                }
            } catch (IOException e) {
                System.err.println(e);
                e.printStackTrace();
            }
        }
    }

    public static class getStringSizeLengthFile {

        public static String getStringSizeLengthFile(long size) {
            DecimalFormat df = new DecimalFormat("0.00");
            float sizeKb = 1024.0f;
            float sizeMb = sizeKb * sizeKb;
            float sizeGb = sizeMb * sizeKb;
            float sizeTerra = sizeGb * sizeKb;
            if(size < sizeMb)
                return df.format(size / sizeKb)+ " Ko";
            else if(size < sizeGb)
                return df.format(size / sizeMb) + " Mo";
            else if(size < sizeTerra)
                return df.format(size / sizeGb) + " Go";
            return "";
        }

    }

    public static class Shaker {
        private TranslateTransition tt;

        public Shaker(Node node) {
            tt = new TranslateTransition(Duration.millis(50), node);
            tt.setFromX(0f);
            tt.setByX(10f);
            tt.setCycleCount(2);
            tt.setAutoReverse(true);
        }

        public void shake() {
            tt.playFromStart();
        }
    }

    public static String DateTimeFormatter(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    public static int countFilesInDirectory(String filePath){
        File f = new File(filePath);
        int count = 0;
        for (File file : f.listFiles()) {
            if (file.isFile()) {
                count++;
            }
        }
        return count;
    }

    public static String getDiskSpace() throws IOException {
        NumberFormat nf = NumberFormat.getNumberInstance();
        FileStore store = null;
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            System.out.print(root + ": ");
            try {
                store = Files.getFileStore(root);
                System.out.println("available=" + nf.format(store.getUsableSpace())
                        + ", total=" + nf.format(store.getTotalSpace()));
            } catch (IOException e) {
                System.out.println("error querying space: " + e.toString());
            }
        }
        return nf.format(store.getTotalSpace());
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static void addTextRestriction(final TextField tf) {
        tf.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getControlNewText().matches("\\d*"))
                return null;
            else
                return c;
        }
        ));
    }

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }

    public static void fadeIn(final TextField txt){
        DoubleProperty opacity = txt.opacityProperty();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0))
        );
        fadeIn.play();
    }

    public static String getFileSizeMegaBytes(File file) {
        return getStringSizeLengthFile.getStringSizeLengthFile(file.length());
    }

    public static String fileExtension(String text){
        String type = text.replaceAll("^.*\\.(.*)$", "$1");
        return type;
    }

    public static void mysqlAlert(SQLException e){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("MySQL Connection Failed -" + e);
        alert.showAndWait();
    }

    public static void mysqlConnectionErrorAlert(SQLException e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("MySQL Server");
        alert.setContentText("MySQL - Connection Failed !");

        /*DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Functions.class.getResource("/resource/css/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog");*/

        Exception ex = new SQLException(e);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        Label label = new Label("The exception stacktrace was:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static byte[] readFile(String file) {
        ByteArrayOutputStream bos = null;
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[2048];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
                ByteArrayOutputStream finalBos = bos;
                //Platform.runLater(() -> lblFileSizeProgresss.setText("File Size Progress %" + (finalBos.size() * 100)/f.length()));
            }
            //Platform.runLater(() -> lblFileSizeProgresss.setText("File Size Progress"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }

    public static byte[] readFilee(String file) {
        int len;
        int size = 1024;
        byte[] buf = new byte[1024];
        try {
        FileInputStream is = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
        buf = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }

}
