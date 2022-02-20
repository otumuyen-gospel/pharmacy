package juma.admin;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressBox extends Stage {
    public BorderPane pane = new BorderPane();
    Label loader = new Label();
    public ProgressBox(){
        pane.setId("root");
        Scene scene = new Scene(pane);
        scene.setFill(null);
        scene.getStylesheets().add("/juma/styles/progress.css");
        this.setScene(scene);
        this.initStyle(StageStyle.TRANSPARENT);
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(App.stage);
        Rectangle2D rect = Screen.getPrimary().getVisualBounds();
        this.setX(rect.getMinX());
        this.setY(rect.getMinY());
        this.setWidth(rect.getWidth());
        this.setHeight(rect.getHeight());
        loader.setWrapText(true);
    }
    public void run() {
        loader.setText("please wait your request is processing...");
        ImageView icon = new ImageView(getClass().getResource("/juma/resources/progress.gif").toExternalForm());
        icon.setFitHeight(100);
        icon.setFitWidth(100);
        loader.setGraphic(icon);
        loader.setGraphicTextGap(10);
        loader.setContentDisplay(ContentDisplay.TOP);
        pane.setCenter(loader);
        this.show();
    }

}
