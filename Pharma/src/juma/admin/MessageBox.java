package juma.admin;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MessageBox extends Stage {
    public BorderPane pane = new BorderPane();
    Label message = new Label();
    Button ok = new Button("Ok");
    public MessageBox(){
        pane.setId("root");
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("/juma/styles/dialog.css");
        this.setScene(scene);
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(App.stage);
        Rectangle2D rect = Screen.getPrimary().getBounds();
        this.setX(rect.getMaxX()/3);
        this.setY(rect.getMinY()+30);
        this.setWidth(rect.getWidth()/3);
        this.setHeight(rect.getHeight()/4);
        message.setWrapText(true);
    }
    public void run(String text) {
        message.setText(text);
        pane.setCenter(message);
        ok.setId("cancel");
        ok.setOnAction(e->{
            this.close();
        });
       HBox bottom = new HBox(ok);
        bottom.setSpacing(10);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        pane.setBottom(bottom);
        this.showAndWait();
    }

}
