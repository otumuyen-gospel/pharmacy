/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.cashier;

import juma.admin.App;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author user1
 */
public class Blank extends Cashier{
    public Blank(){
        super();
    }
    @Override
    protected void Middle(){
        //current link
        Label blank = new Label("Home / Access Denied");
        blank.setId("blank3");
        HBox classLink = new HBox(blank);
        classLink.setId("link");
        VBox center = new VBox(classLink,this.denied());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
    }
    
    public StackPane denied(){
        StackPane box = new StackPane();
        box.getStylesheets().add("/juma/styles/blank.css");
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(App.stage.getWidth()/1.1);
        box.setId("blank");
        Label caption = new Label("Access Denied");
        caption.setId("blankTitle");
        ImageView bg = new ImageView(getClass().getResource("/juma/resources/bg2.jpg").toExternalForm());
        bg.setFitWidth(1000);
        caption.setAlignment(Pos.TOP_LEFT);
        box.getChildren().addAll(bg,caption);
        return box;
    }
    
}
