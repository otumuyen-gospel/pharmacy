/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.admin;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import juma.api.Authenticate;
import juma.api.FetchLogo;

/**
 *
 * @author user1
 */
public class Login extends StackPane{
    VBox form = new VBox();
    HBox container = new HBox();
    Label logo = new Label();
    Label title = new Label();
    Label user = new Label("Username");
    TextField userField  =new TextField();
    Label pass = new Label("Password");
    PasswordField passField  =new PasswordField();
    FetchLogo fl = new FetchLogo();
    Button signin = new Button("Sign in");
    public Login(){
        this.getStylesheets().add("/juma/styles/login.css");
        this.setId("login");
        container.maxHeightProperty().bind(App.stage.heightProperty().divide(1.4));
        container.maxWidthProperty().bind(App.stage.widthProperty().divide(3));
        container.setId("container");
       
        form.prefWidthProperty().bind(container.maxWidthProperty());
        form.prefHeightProperty().bind(container.prefHeightProperty());
        form.setId("form");
        fl.get();//logo and text
        logo.setText(fl.title);
        ImageView vlog = new ImageView(fl.blue);
        vlog.setFitHeight(48);vlog.setFitWidth(48);
        logo.setGraphic(vlog);
        logo.setGraphicTextGap(10);
        logo.setContentDisplay(ContentDisplay.TOP);
        logo.setId("logo");
        title.setText("Login");
        title.setGraphic(new ImageView(getClass().getResource("/juma/resources/key.png").toExternalForm()));
        title.setGraphicTextGap(10);
        title.setId("title");
        pass.setGraphic(new ImageView(getClass().getResource("/juma/resources/lock.png").toExternalForm()));
        pass.setGraphicTextGap(10);
        VBox passbox = new VBox(pass,passField);
        passbox.setId("fieldbox");
        user.setGraphic(new ImageView(getClass().getResource("/juma/resources/face.png").toExternalForm()));
        user.setGraphicTextGap(10);
        VBox userbox = new VBox(user,userField);
        userbox.setId("fieldbox");
        signin.prefWidthProperty().bind(passField.widthProperty());
        signin.prefHeightProperty().bind(passField.heightProperty());
        VBox btnbox = new VBox(signin);
        btnbox.setId("fieldbox");
        form.getChildren().addAll(logo,title,userbox,passbox,btnbox);
        
        container.getChildren().addAll(form);
        this.getChildren().add(container);
        
        signin.setOnAction(e->{
            new Authenticate().run(userField.getText(), passField.getText());
        });
    }
}
