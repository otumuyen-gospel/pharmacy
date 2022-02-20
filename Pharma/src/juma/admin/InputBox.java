package juma.admin;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InputBox extends Stage {
    public BorderPane pane = new BorderPane();
    Label label = new Label("Enter Server Internet Protocol");
    TextField input = new TextField();
    Button cancel = new Button("Cancel");
    Button ok = new Button("Ok");
    public InputBox(){
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
        label.setWrapText(true);
    }
    public void run(String text) {
        label.setText(text);
        VBox b= new VBox(label,input);
        b.setSpacing(10);
        b.setAlignment(Pos.CENTER_LEFT);
        pane.setCenter(b);
        cancel.setId("cancel");
        cancel.setOnAction(e->{
            input.setText("");
        });
        ok.setId("ok");
        ok.setOnAction(e->{
            this.close();
        });
        HBox bottom = new HBox(cancel,ok);
        bottom.setSpacing(10);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        pane.setBottom(bottom);
        this.showAndWait();
    }
    public String get(){
        return input.getText();
    }
    public void set(String value){
        input.setText(value);
    }

}
