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

public class QuestionBox extends Stage {
    public BorderPane pane = new BorderPane();
    Label question = new Label("how are you ?");
    Button cancel = new Button("Cancel");
    Button ok = new Button("Ok");
    public enum Option{OK,CANCEL};
    Option option;
    public QuestionBox(){
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
        question.setWrapText(true);
    }
    public Option run(String text) {
        question.setText(text);
        pane.setCenter(question);
        cancel.setId("cancel");
        cancel.setOnAction(e->{
            this.close();
            option = Option.CANCEL;
        });
        ok.setId("ok");
        ok.setOnAction(e->{
            this.close();
            option = Option.OK;
        });
        HBox bottom = new HBox(cancel,ok);
        bottom.setSpacing(10);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        pane.setBottom(bottom);
        this.showAndWait();
        return option;
    }

}
