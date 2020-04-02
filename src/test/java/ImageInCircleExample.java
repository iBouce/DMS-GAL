import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ImageInCircleExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        final Pane root = new Pane();
        final ImageView imageView = new ImageView("http://upload.wikimedia.org/wikipedia/commons/thumb/5/58/Sunset_2007-1.jpg/640px-Sunset_2007-1.jpg");
        final Circle clip = new Circle(300, 200, 200);
        imageView.setClip(clip);
        root.getChildren().add(imageView);

        // enable dragging:
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        imageView.setOnMousePressed(event -> mouseAnchor.set(new Point2D(event.getSceneX(), event.getSceneY())));
        imageView.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseAnchor.get().getX();
            double deltaY = event.getSceneY() - mouseAnchor.get().getY();
            imageView.setLayoutX(imageView.getLayoutX() + deltaX);
            imageView.setLayoutY(imageView.getLayoutY() + deltaY);
            clip.setCenterX(clip.getCenterX() - deltaX);
            clip.setCenterY(clip.getCenterY() - deltaY);
            mouseAnchor.set(new Point2D(event.getSceneX(), event.getSceneY()));
        });

        final Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}