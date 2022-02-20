/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.cashier;

import juma.admin.MessageBox;
import juma.admin.App;
import java.io.File;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import juma.admin.ProgressBox;
import juma.admin.Wrapper;
import juma.api.Camera;
import juma.api.Database;

/**
 *
 * @author user1
 */
public class Profile extends Cashier{
     File file = null;
     ImageView pic;
     String message = "";
    public Profile(){
        super();
    }
    @Override
    protected void Middle(){
        Label profile = new Label("Home / Profile Settings");
        profile.setId("profile3");
        HBox classLink = new HBox(profile);
        classLink.setId("link");
        VBox center = new VBox();
        center.getChildren().addAll(classLink,setProfile(),setPassword(),changePhoto());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
    }
    private VBox setProfile(){
        VBox box = new VBox();
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setId("profileBox");
        Label caption = new Label("Change Profile Settings");
        caption.setId("proTitle");
        TextField userName = new TextField(profile.getUserName());
        userName.setId("field");
        TextField fullName = new TextField(profile.getFullName());
        fullName.setId("field");
        TextField contact = new TextField(profile.getContact());
        contact.setId("field");
        TextField userEmail = new TextField(profile.getEmail());
        userEmail.setId("field");
        TextField userRole = new TextField(profile.getRole());
        userRole.setDisable(true);
        userRole.setId("field");
        TextField userDate = new TextField(profile.getJoined());
        userDate.setDisable(true);
        userDate.setId("field");
        TextField userSales = new TextField(String.valueOf(profile.getSales()));
        userSales.setDisable(true);
        userSales.setId("field");
        Button update = new Button("Update Profile");
        
        update.setOnAction(e->{
            if(!userName.getText().isEmpty()){
                if(!fullName.getText().isEmpty()){
                    if(!contact.getText().isEmpty()){
                        if(!userEmail.getText().isEmpty()){
                            String sql = "update profile set username='"+userName.getText()+"',fullname='"+
                                    fullName.getText()+"',contact='"+contact.getText()+"',email='"+
                                    userEmail.getText()+"' where username='"+new Database().getUser()+"' and "+
                                    "password='"+new Database().getPassword()+"'";
                             this.updateProfile(sql,userName.getText());
                        }else{
                            new MessageBox().run("please enter a valid email");
                        }
                        
                    }else{
                        new MessageBox().run("please enter contact phone no");
                    }
                }else{
                    new MessageBox().run("please enter fullname");
                }
            }else{
                new MessageBox().run("please enter username");
            }
           
        });
        box.getChildren().addAll(caption,new VBox(new Label("UserName"),userName),
                new VBox(new Label("FullName"),fullName),new VBox(new Label("Contact No"),contact)
                ,new VBox(new Label("User Email Address"),userEmail),new VBox(new Label("User Total Sales"),userSales),
                new VBox(new Label("User Role"),userRole),new VBox(new Label("Joined Date"),userDate),
                update);
        return box;
    }
    public void updateProfile(String sql,String user){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                if(b.performBatchUpdate(new String[]{sql}).length > 0){
                    message = "profile updated successfully";
                }else{
                    message = "profile update unsuccessful";
                }
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    b.setUser(user);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Profile());
                    App.pane.setCenter(wrapper);
                });
            }catch(Exception e){
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("error proccessing update");
                });
                b.rollBack();
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
        }).start();
    }
    private VBox setPassword(){
        VBox box = new VBox();
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setId("profileBox");
        Label caption = new Label("Change Password Settings");
        caption.setId("passTitle");
        
        PasswordField password = new PasswordField();
        password.setId("field");
        PasswordField newpassword = new PasswordField();
        newpassword.setId("field");
        PasswordField confirm = new PasswordField();
        confirm.setId("field");
        Button update = new Button("Change Password");
        update.setOnAction(e->{
            if(password.getText().equals(new Database().getPassword())){
                if(newpassword.getText().length() == 8){
                    if(confirm.getText().equals(newpassword.getText())){
                           this.updatePassword(newpassword.getText());
                    }else{
                        new MessageBox().run("confirm doesn't match new password");
                    }
                }else{
                    new MessageBox().run("new password must be eight characters long no more no less");
                }
            }else{
                new MessageBox().run("please enter your current password");
            }
           
        });
        box.getChildren().addAll(caption,new VBox(new Label("Current Password"),password),
                new VBox(new Label("New Password"),newpassword),new VBox(new Label("Confirm New Password"),confirm)
                ,update);
        return box;
    }
    public void updatePassword(String password){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                if(b.performBatchUpdate(new String[]{"update profile set password='"+password+"' where username='"+
                        b.getUser()+"' and password='"+b.getPassword()+"'"}).length > 0){
                    message = "password changed successfully";
                }else{
                    message = "password change unsuccessful";
                }
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    b.setPassword(password);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Profile());
                    App.pane.setCenter(wrapper);
                });
            }catch(Exception e){
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("error processing update");
                });
                b.rollBack();
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
        }).start();
    }
    private VBox changePhoto(){
        VBox box = new VBox();
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);
        box.setId("profileBox");
        Label caption = new Label("Change Profile Picture");
        caption.setId("picTitle");
        pic = new ImageView(getClass().getResource("/juma/resources/team1.png").toExternalForm());
        try{
            if(profile.getPhoto().length() > 0){
                pic = new ImageView();
                pic.setImage(new Image(profile.getPhoto().getBinaryStream()));
            }
        }catch(Exception e){
            new MessageBox().run(e.getMessage());
        }
        pic.setFitHeight(90);
        pic.setFitWidth(90);
        Label save = new Label("PROFILE PHOTO");
        save.setAlignment(Pos.CENTER);
        Label camera = new Label("use camera");
        camera.setGraphic(new ImageView(getClass().getResource("/juma/resources/camera.png").toExternalForm()));
        camera.setGraphicTextGap(10);
        camera.setContentDisplay(ContentDisplay.TOP);
        camera.setId("attach");
        camera.setOnMouseClicked(e->{
            Camera cam = new Camera();
        });
        Label photo = new Label("choose Photo");
        photo.setGraphic(new ImageView(getClass().getResource("/juma/resources/file.png").toExternalForm()));
        photo.setGraphicTextGap(10);
        photo.setContentDisplay(ContentDisplay.TOP);
        photo.setId("attach");
        photo.setOnMouseClicked(e->{
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg","*.jpeg","*.gif"));
            chooser.setTitle("upload photo");
            file = chooser.showOpenDialog(App.stage);
            try {
                if(file != null){
                     this.uploadPhoto(file);
                }else{
                    new MessageBox().run("No file choosen");
                }
            } catch (Exception ex) {
                new MessageBox().run(ex.getMessage());
            }
        });
        HBox b = new HBox(photo,camera);
        b.setSpacing(50);
        box.getChildren().addAll(caption,pic,save,b);
        return box;
        
    }
    public void uploadPhoto(File file){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                if(b.createBlob(file, "update profile set photo=? where username=?", b.getUser()) > 0){
                    message ="photo uploaded successfully";
                }else{
                    message ="photo upload unsuccessful";
                }
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Profile());
                    App.pane.setCenter(wrapper);
                });
            }catch(Exception e){
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("error uploading update");
                });
                b.rollBack();
            }finally{
                b.closeConnection();
            }
        }).start();
    }
}
