/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.admin;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import juma.api.Database;

/**
 *
 * @author user1
 */
public class Settings extends Admin{
    File blue;File white;
    TextField ip;
    TextField name;
    ChoiceBox currency;
    TextField contact;
    TextField systememail;
    String sql;ImageView whitePic;ImageView bluePic;
    Database db = new Database();
    ProgressBox progress = new ProgressBox();
    int column0;String column1,column2,column3,column4,column5;Blob column6,column7;
    public Settings(){
        super();
        this.getSettings();
    }
    @Override
    protected void Middle(){
        //current link
        settings.setId("settings2");
        settings = new Label("Home / System Settings");
        settings.setId("settings3");
        HBox classLink = new HBox(settings);
        classLink.setId("link");
        VBox center = new VBox(classLink,this.setFields(),this.sysInfo());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
        this.setCenter(center);
        
    }
    private GridPane setFields(){
        GridPane p = new GridPane();
        p.setId("runners");
        p.getStylesheets().add("/juma/styles/settings.css");
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        currency = new ChoiceBox();
        name = new TextField();
        contact = new TextField();
        systememail = new TextField();
        currency.getItems().addAll("Naira(N)","Dollar($)");
        Button submit = new Button("Change Settings");
        Label caption = new Label("System Settings");
        caption.setId("addTitle");
        p.add(caption, 0, 0, 2, 1);
        p.add(new VBox(new Label("System Name"),name), 0, 1);
        p.add(new VBox(new Label("Currency"),currency), 1, 1);
        p.add(new VBox(new Label("Telephone"),contact), 0, 2);
        p.add(new VBox(new Label("System Email"),systememail), 1, 2);
        p.add(this.blueLogo(), 0, 3); p.add(this.whiteLogo(), 1, 3);
        p.add(submit, 1, 4);
        submit.setOnAction(e->{
            this.runSettings();
        });
        return p;
    }
    public void getSettings(){
        progress.run();
        new Thread(() -> {
             try{
                 db.getDataConnection();
                 db.getStatement();
                 ResultSet rs = db.getResultSet("select * from settings");
                 if(rs.next()){
                     column0 = rs.getInt(1);
                     column1 = rs.getString(2);
                     column2 = rs.getString(3);
                     column3 = rs.getString(4);
                     column4 = rs.getString(5);
                     column5 = rs.getString(6);
                     column6 = rs.getBlob(7);
                     column7 = rs.getBlob(8);
                 }
                 rs.close();
                 Platform.runLater(() -> {
                     progress.close();
                     name.setText(column1);
                     currency.setValue(column3);
                     contact.setText(column4);
                     systememail.setText(column5);
                     ip.setText(column2);
                     try {
                         if(column7 != null && column7.length() > 0){
                             bluePic.setImage(new Image(column7.getBinaryStream()));
                         }else{
                             bluePic.setImage(new Image(getClass().
                                     getResource("/juma/resources/logoblue.png").toExternalForm()));
                         }
                         if(column6 != null && column6.length() > 0){
                             whitePic.setImage(new Image(column6.getBinaryStream()));
                         }else{
                             whitePic.setImage(new Image(getClass().
                                     getResource("/juma/resources/logowhite.png").toExternalForm()));
                         }
                         
                     } catch (SQLException ex) {
                  
                     }
                     
                 });
             }catch(Exception e){
                 Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("error fetching System settings");
                });
             }finally{
                 db.closeResultSet();
                 db.closeStatement();
                 db.closeConnection();
             }
         }).start();
    }
    public void runSettings(){
        if(name.getText() != null && !name.getText().isEmpty()){
             if(currency.getValue() != null){
                 if(contact.getText() != null && !contact.getText().isEmpty()){
                     if(systememail.getText() != null && !systememail.getText().isEmpty()){
                         if(ip.getText() != null && !ip.getText().isEmpty()){
                             progress.run();
                             new Thread(() -> {
                                 try{
                                     db.getDataConnection();
                                     db.setAutoCommit();
                                     db.getStatement();
                                     ResultSet rs = db.getResultSet("select * from settings");
                                     if(rs.next()){
                                         sql = "update settings set name='"+name.getText()+"',ip='"+ip.getText()+
                                                 "',currency='"+currency.getValue()+"',"
                                                 + "telephone='"+contact.getText()+"',email='"+systememail.getText()+"'";
                                                 
                                                
                                     }else{
                                          sql = "insert into settings values(0,'"+name.getText()+"','"+ip.getText()+"','"+
                                          currency.getValue()+"','"+contact.getText()+"','"+
                                                  systememail.getText()+"','','')";   
                                     }
                                     db.performSingleUpdate(sql);
                                     if(blue !=null){
                                         sql = "update settings set blue=?";
                                         db.createBlob(blue, sql, null);//upload blue logo
                                     }
                                     if(white !=null){
                                         sql = "update settings set white=?";
                                         db.createBlob(white, sql, null);//upload white logo
                                     }
                                     db.commit();
                                     rs.close();
                                     Platform.runLater(() -> {
                                         progress.close();
                                         new MessageBox().run("Operation Successful");
                                         App.pane.getChildren().clear();
                                         Wrapper wrapper = new Wrapper();
                                         wrapper.setWrapper(new Settings());
                                         App.pane.setCenter(wrapper);
                                     });
                                     
                                 }catch(Exception e){
                                     Platform.runLater(()->{
                                        progress.close();
                                        new MessageBox().run("error updating system settings");
                                     });
                                 }finally{
                                     db.closeResultSet();
                                     db.closeStatement();
                                     db.closeConnection();
                                 }
                             }).start();

                         }else{
                             new MessageBox().run("Click The Ping Button At The Bottom to set System IP");
                         }
                     }else{
                         new MessageBox().run("Please Enter Valid System Email");
                     }
                 }else{
                     new MessageBox().run("Please Enter Company Phone Number");
                 }
             }else{
                  new MessageBox().run("Please Choose Currency Type");
             }
        }else{
            new MessageBox().run("Please Enter Company Name");
        }
       
    }
    private GridPane sysInfo(){
        GridPane p = new GridPane();
        p.setId("runners");
        p.getStylesheets().add("/juma/styles/settings.css");
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        ip = new TextField();
        ip.setId("ip");
        ip.setEditable(false);
        Button submit = new Button("Ping");
        submit.setOnAction(e->{
            try {
                InetAddress address = InetAddress.getLocalHost();
                if(address.isLoopbackAddress()){
                    new MessageBox().run("To get The Accurate System IP Your Internet Must Be Turn On Or "
                        + "You Must be Connected To A Wifi");
                }else{
                     ip.setText(address.getHostAddress());
                }
                
            } catch (UnknownHostException ex) {
                new MessageBox().run("error pinging ip address");
            }
        });
        Label caption = new Label("System Internet Protocol ");
        caption.setId("addTitle");
        p.add(caption, 0, 0, 2, 1);
        p.add(new VBox(new Label("System Internet Protocol"),ip), 0, 1);
        p.add(submit, 0, 2);
        return p;
    }
    private HBox whiteLogo(){
        HBox box = new HBox();
        box.setStyle("-fx-background-color:yellow;-fx-padding:10px;-fx-cursor:hand;-fx-pref-width:525px;");
        box.setPadding(new Insets(20));
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);
        whitePic = new ImageView();
        whitePic.setFitHeight(48);
        whitePic.setFitWidth(48);
        Label caption = new Label("change white logo");
        box.setOnMouseClicked(e->{
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg",".jpeg",".gif"));
            chooser.setTitle("upload photo");
            white = chooser.showOpenDialog(App.stage);
            try {
                if(white != null){
                      whitePic.setImage(new Image("file:///"+white.getCanonicalPath()));
                }
            } catch (IOException ex) {
                new MessageBox().run(ex.getMessage());
            }
        });
        box.getChildren().addAll(whitePic,caption);
        return box;
        
    }
   private HBox blueLogo(){
        HBox box = new HBox();
        box.setStyle("-fx-background-color:yellow;-fx-padding:10px;-fx-cursor:hand;-fx-pref-width:525px;");
        box.setPadding(new Insets(20));
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);
        bluePic = new ImageView();
        bluePic.setFitHeight(48);
        bluePic.setFitWidth(48);
        Label caption = new Label("change blue logo");
        box.setOnMouseClicked(e->{
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg",".jpeg",".gif"));
            chooser.setTitle("upload photo");
            blue = chooser.showOpenDialog(App.stage);
            try {
                if(blue != null){
                      bluePic.setImage(new Image("file:///"+blue.getCanonicalPath()));
                }
            } catch (IOException ex) {
                new MessageBox().run(ex.getMessage());
            }
        });
        box.getChildren().addAll(bluePic,caption);
        return box;
        
    }
}
