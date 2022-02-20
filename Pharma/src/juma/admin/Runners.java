/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.admin;

import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import juma.api.Database;

/**
 *
 * @author user1
 */
public class Runners extends Admin{
    boolean isShowing = false;
    String message = "";
    GridPane pane;
    VBox center;
    Table table;
    public Runners(){
        super();
    }
    @Override
    protected void Middle(){
        //current link
        runner.setId("runners2");
        runner = new Label("Home / System Runners");
        runner.setId("runners3");
        HBox classLink = new HBox(runner);
        classLink.setId("link");
        pane = this.AddUser();
        center = new VBox(classLink,this.users());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
    }
    public VBox users(){
        VBox box = new VBox();
        box.getStylesheets().add("/juma/styles/runners.css");
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(App.stage.getWidth()/1.1);
        box.setId("runners");
        Label caption = new Label("View System Users");
        caption.setId("runnersTitle");
        
        Button add = new Button("Add New User");
        HBox addBox = new HBox(add);
        addBox.setAlignment(Pos.CENTER_RIGHT);
        
        table = this.getTable();
        box.getChildren().addAll(addBox,caption,table);
        add.setOnAction(e->{
            if(isShowing){
                center.getChildren().remove(1);
                isShowing = false;
               add.setText("Add New User");
            }else{
                center.getChildren().add(1,pane);
                isShowing = true;
                add.setText("Hide Form");
            }
           
        });
        return box;
    }
    public ArrayList fetchUser(){
        ArrayList list = new ArrayList();
        Database b= new Database();
        try{
            b.getDataConnection();
            b.getStatement();
            ResultSet rs = b.getResultSet("select * from profile");
            while(rs.next()){
                list.add(rs.getInt("id"));
                list.add(rs.getString("fullname"));
                list.add(rs.getString("role"));
                list.add(rs.getString("contact"));
                list.add(rs.getString("email"));
                list.add(rs.getString("joined"));
                list.add(rs.getLong("sales"));
                list.add(rs.getString("username"));
            }
            
        }catch(Exception e){
            
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
            System.gc();
        }
        return list;
    }
    public void delete(int id,String user){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                String sql = "delete from profile where id="+id;
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                if(b.performBatchUpdate(new String[]{sql}).length > 0){
                    message ="deleted successfully";
                }else{
                    message ="delete unsuccessful";
                }
                b.commit();
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run(message);
                     if(!user.equals(new Database().getUser())){
                         App.pane.getChildren().clear();
                         Wrapper wrapper = new Wrapper();
                         wrapper.setWrapper(new Runners());
                         App.pane.setCenter(wrapper);
                     }else{
                          App.pane.getChildren().clear();
                          App.pane.setCenter(new Login());
                     }
                });
                
            }catch(Exception e){
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run("error trying to delete record");
                });
                b.rollBack();
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
            
        }).start();
    }
    private Table getTable(){
        Table table = new Table();
        ArrayList list = this.fetchUser();
        int rows = 0; 
        int count = 0;
        if(list != null && list.size() > 0){
            rows = list.size()/8; // number of fetch item from table divided by number of table column
        }
        table.setRowSize(rows);//row size excluding table column headers(this has to be set before table column headers
        
        table.setColumns(new String[]{"#","Full Name","Role","Contact No","E-mail","Joined Date","Sales","Action"});
        for(int i = 0; i < rows; i++){//according to the row size
            Label id = new Label(String.valueOf(list.get(count)));
            count++;
            Label name = new Label(String.valueOf(list.get(count)));
            count++;
            Label role = new Label(String.valueOf(list.get(count)));
            count++;
            Label contact = new Label(String.valueOf(list.get(count)));
            count++;
            Label email = new Label(String.valueOf(list.get(count)));
            count++;
            Label date = new Label(String.valueOf(list.get(count)));
            count++;
            Label sales = new Label(String.valueOf(list.get(count)));
            count++;
            String username = String.valueOf(list.get(count));
            count++;
            
            Label delete = new Label();
            delete.setTooltip(new Tooltip("delete"));
            delete.setGraphic(new ImageView(getClass().getResource("/juma/resources/deleteblack.png").toExternalForm()));
            delete.setGraphicTextGap(5);
            delete.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
            delete.setOnMouseClicked(e->{
                if(username.equals(new Database().getUser())){
                    QuestionBox q = new QuestionBox();
                    QuestionBox.Option option = q.run("about to delete your own account ?");
                    if(option == QuestionBox.Option.OK){
                       this.delete(Integer.parseInt(id.getText()),username);
                    }
                }else{
                    QuestionBox q = new QuestionBox();
                    QuestionBox.Option option = q.run("about to delete this account ?");
                    if(option == QuestionBox.Option.OK){
                       this.delete(Integer.parseInt(id.getText()),username);
                    }
                }
                
            });
            
            table.AddRow(new Node[]{id,name,role,contact,email,date,sales,delete});
        }
        return table;
    }
    public GridPane AddUser(){
        GridPane p = new GridPane();
        p.setId("runners");
        p.getStylesheets().add("/juma/styles/runners.css");
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        TextField name = new TextField();
        PasswordField pass = new PasswordField();
        ChoiceBox role = new ChoiceBox();
        role.getItems().addAll("Admin","Manager","SalesPerson");
        TextField contact = new TextField();
        TextField useremail = new TextField();
        DatePicker date = new DatePicker();
        Button submit = new Button("Submit");
        submit.setOnAction(e->{
            if(!name.getText().isEmpty()){
                if(pass.getText().length() == 8){
                    if(role.getValue() != null){
                        if(!contact.getText().isEmpty()){
                             if(!useremail.getText().isEmpty()){
                                 if(date.getValue() != null){
                                     String sql = "insert into profile values(0,'"+name.getText()+"','"+
                                             pass.getText()+"','','"+useremail.getText()+"','"+
                                             role.getValue().toString()+"','"+contact.getText()+"','"+
                                             date.getValue().toString()+"',0,'')";
                                     this.createNewUser(sql,name.getText(),pass.getText());
                                     
                                 }else{
                                     new MessageBox().run("please enter joined date");
                                 }
                             }else{
                                 new MessageBox().run("please enter valid email");
                             }
                            
                        }else{
                            new MessageBox().run("please enter user contact phone no");
                        }
                    }else{
                         new MessageBox().run("you must assign role to user");
                    }
                }else{
                    new MessageBox().run("password must be 8 character long no more no less");
                }
            }else{
                new MessageBox().run("username can not be empty");
            }
        });
        Label caption = new Label("Add New System Users");
        caption.setId("addTitle");
        p.add(caption, 0, 0, 2, 1);
        p.add(new VBox(new Label("UserName"),name), 0, 1);
        p.add(new VBox(new Label("Password"),pass), 1, 1);
        p.add(new VBox(new Label("Role"),role), 0, 2);
        p.add(new VBox(new Label("Contact No"),contact), 1, 2);
        p.add(new VBox(new Label("E-mail"),useremail), 0, 3);
        p.add(new VBox(new Label("Joined Date"),date), 1, 3);
        p.add(submit, 1, 4);
        return p;
    }
    public void createNewUser(String sql,String user,String pass){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
               ResultSet rs=  b.getResultSet("select * from profile where username='"+user+"' or password='"+
                       pass+"'");
               if(rs.next()){
                   message = "sorry user already exist";
               }else{
                   b.performSingleUpdate(sql);
                   message = "user created successfully";
               }
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Runners());
                    App.pane.setCenter(wrapper);
                });
            }catch(Exception e){
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("error creating new user");
                });
                b.rollBack();
            }finally{
                b.closeResultSet();
                b.closeStatement();
                b.closeConnection();
            }
        }).start();
    }
}
