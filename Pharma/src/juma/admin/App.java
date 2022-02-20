package juma.admin;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import juma.api.Control;

public class App extends Application {
    public static Stage stage;
    public static BorderPane pane = new BorderPane();
    @Override
    public void start(Stage stage) {
        App.stage = stage;
        App.stage.setScene(new Scene(pane));
        App.stage.initStyle(StageStyle.UNIFIED);
        Rectangle2D rect = Screen.getPrimary().getVisualBounds();
        App.stage.setX(rect.getMinX());
        App.stage.setY(rect.getMinY());
        App.stage.setWidth(rect.getWidth());
        App.stage.setHeight(rect.getHeight());
        Control control = new Control();
        control.run();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
