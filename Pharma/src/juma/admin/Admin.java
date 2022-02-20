/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.admin;

import java.sql.ResultSet;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import juma.api.Database;
import juma.api.FetchLogo;
import juma.api.GenerateId;
import juma.api.Notification;
import juma.api.ProfileModel;
import juma.api.Session;

/**
 *
 * @author user1
 */
public class Admin extends BorderPane{
    BorderPane top = new BorderPane();
    VBox side  = new VBox();
    protected Label logo = new Label();
    protected Label user = new Label();
    protected Label logout = new Label();
    protected  Label calculator = new Label();
    protected Label out_of_date = new Label();
    protected Label out_of_stock = new Label();
    protected Label dashboard = new Label("Dashboard");
    protected Label sales = new Label("New Sales");
    protected Label brand = new Label("Brands");
    protected Label product = new Label("Products");
    protected Label supplier = new Label("Suppliers");
    protected Label runner = new Label("Users");
    protected Label salesQuery = new Label("Sales Query");
    protected Label backup = new Label("Backup");
    protected Label settings  = new Label("Settings");
    protected ImageView photo = new ImageView(getClass().getResource("/juma/resources/team1.png").toExternalForm());
    FetchLogo fl = new FetchLogo();
    VBox photoView = new VBox();
    protected ProfileModel profile = ProfileModel.fetch();
    public Admin(){
        this.prefWidthProperty().bind(App.stage.widthProperty().subtract(34));
        this.prefHeightProperty().bind(App.stage.heightProperty());
        this.getStylesheets().add("/juma/styles/dashboard.css");
        this.setId("dashboard");
        this.Top();
        this.Side();
        this.Middle();
    }
    protected void Top(){
        out_of_stock.setVisible(false);
        new Notification().outOfStock(out_of_stock);
        out_of_date.setVisible(false);
        new Notification().expiry(out_of_date);
        top.setId("top");
        this.setTop(top);
        fl.get();
        logo.setText(fl.title);
        ImageView vlog = new ImageView(fl.white);
        vlog.setFitHeight(48);vlog.setFitWidth(48);
        logo.setGraphic(vlog);
        logo.setGraphicTextGap(10);
        logo.setId("logo");
        top.setLeft(logo);
        
        StackPane one = new StackPane(new ImageView(getClass().getResource("/juma/resources/alertwhite.png").toExternalForm())
                ,out_of_date);
        one.setOnMouseClicked(e->{new Expiry().run();});
        one.setId("stack");
        one.setAlignment(Pos.CENTER);
        out_of_date.setId("notifier");
        out_of_date.setTranslateY(-20);
        out_of_date.setTranslateX(20);
        StackPane two = new StackPane(new ImageView(getClass().getResource("/juma/resources/alarmwhite.png").toExternalForm())
                ,out_of_stock);
         two.setId("stack");
         two.setOnMouseClicked(e->{new OutOfStock().run();});
        two.setAlignment(Pos.CENTER);
        out_of_stock.setId("notifier");
        out_of_stock.setTranslateY(-20);
        out_of_stock.setTranslateX(20);
        
        calculator.setTooltip(new Tooltip("Calculator"));
        calculator.setGraphic(new ImageView(getClass().getResource("/juma/resources/calculatorwhite.png").toExternalForm()));
        calculator.setContentDisplay(ContentDisplay.TOP);
        calculator.setId("profile");
        calculator.setOnMouseClicked(e->{new Calculator().run();});
        
        logout.setTooltip(new Tooltip("LOG OUT"));
        logout.setGraphic(new ImageView(getClass().getResource("/juma/resources/logoutwhite.png").toExternalForm()));
        logout.setContentDisplay(ContentDisplay.TOP);
        logout.setId("profile");
        logout.setOnMouseClicked(e->{
            QuestionBox q = new QuestionBox();
                QuestionBox.Option option = q.run("this will log you out");
                if(option == QuestionBox.Option.OK){
                    App.pane.getChildren().clear();
                    App.pane.setCenter(new Login());
                    Session.destroy();
                    GenerateId.reset();
                    System.gc();
                }
           
        });
        user.setText("Name : "+new Database().getUser()+"     Role : "+
                new Database().getRole());
        user.setId("account");
        HBox profilebox = new HBox(user,calculator,two,one,logout);
        profilebox.setSpacing(40);
        profilebox.setAlignment(Pos.CENTER_RIGHT);
        top.setRight(profilebox);
    }
    protected void Side(){
        try{
            if(profile.getPhoto().length() > 0){
                photo = new ImageView();
                photo.setImage(new Image(profile.getPhoto().getBinaryStream()));
            }
        }catch(Exception e){}
        side.setId("side");
        this.setLeft(side);
        side.setSpacing(1);
        dashboard.setId("dash1");
        dashboard.setOnMouseClicked(e->{
            App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Admin());
            App.pane.setCenter(wrapper);
        });
        sales.setId("sales1");
        sales.setOnMouseClicked(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Sales());
            App.pane.setCenter(wrapper);
        });
        brand.setId("brand1");
        brand.setOnMouseClicked(e->{
            App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Brand());
            App.pane.setCenter(wrapper);
        });
        product.setId("product1");
        product.setOnMouseClicked(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Product());
            App.pane.setCenter(wrapper);
        });
        supplier.setId("supplier1");
        supplier.setOnMouseClicked(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Supplier());
            App.pane.setCenter(wrapper);
        });
        runner.setId("runners1");
        runner.setOnMouseClicked(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Runners());
            App.pane.setCenter(wrapper);
        });
        salesQuery.setId("salesQuery1");
        salesQuery.setOnMouseClicked(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new SalesQuery());
            App.pane.setCenter(wrapper);
        });
        backup.setId("backup1");
        backup.setOnMouseClicked(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Backup());
            App.pane.setCenter(wrapper);
        });
        settings.setId("settings1");
        settings.setOnMouseClicked(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Settings());
            App.pane.setCenter(wrapper);
        });
        photo.setOnMouseClicked(e->{
            App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Profile());
            App.pane.setCenter(wrapper);
        });
        photo.setFitHeight(90);
        photo.setFitWidth(90);
        photoView.setId("photo");
        photoView.getChildren().add(photo);
        
        side.getChildren().addAll(photoView,dashboard,sales,brand,product,supplier,salesQuery,
                backup,settings,runner);
    }
    protected void Middle(){
        //current link
        dashboard.setId("dash2");
        dashboard = new Label("Home / Dashboard");
        dashboard.setId("dash3");
        HBox classLink = new HBox(dashboard);
        classLink.setId("link");
        VBox center = new VBox(classLink,this.Boxes());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
        
    }
    private GridPane Boxes(){
        long totalInvoice = 0,totalProduct = 0,totalUser = 0,totalSupplier = 0;
         Database b = new Database();
        try{
           b.getDataConnection();
           b.getStatement();
           String sql = "select count(*) from invoice";
           ResultSet rs = b.getResultSet(sql);
           if(rs.next()){
               totalInvoice = rs.getInt(1);
           }
           sql = "select count(*) from product";
           rs = b.getResultSet(sql);
           if(rs.next()){
               totalProduct = rs.getInt(1);
           }
           sql = "select count(*) from profile";
           rs = b.getResultSet(sql);
           if(rs.next()){
               totalUser = rs.getInt(1);
           }
           sql = "select count(*) from supplier";
           rs = b.getResultSet(sql);
           if(rs.next()){
               totalSupplier = rs.getInt(1);
           }
           rs.close();
        }catch(Exception e){
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
        }
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(15));
        grid.setHgap(20);
        grid.setVgap(50);
        Button box1 = new Button(totalInvoice+"\n\nInvoice Query");
        box1.setOnAction(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new SalesQuery());
            App.pane.setCenter(wrapper);
        });
        box1.setId("box1");
        grid.add(box1,0,0);
        Button box2 = new Button(totalProduct+"\n\nTotal Products");
        box2.setOnAction(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Product());
            App.pane.setCenter(wrapper);
        });
        box2.setId("box2");
        grid.add(box2,1,0);
        Button box3 = new Button(totalUser+"\n\nTotal Users");
        box3.setOnAction(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Runners());
            App.pane.setCenter(wrapper);
        });
        box3.setId("box3");
        grid.add(box3,2,0);
        Button box4 = new Button(totalSupplier+"\n\nTotal Suppliers");
        box4.setOnAction(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Supplier());
            App.pane.setCenter(wrapper);
        });
        box4.setId("box4");
        grid.add(box4,0,1);
        Button box5 = new Button("Simple\n\nCalculator");
        box5.setOnAction(e->{
           new Calculator().run();
        });
        box5.setId("box5");
        grid.add(box5,1,1);
        Button box6 = new Button("Profile \n\nSettings");
        box6.setOnAction(e->{
           App.pane.getChildren().clear();
            Wrapper wrapper = new Wrapper();
            wrapper.setWrapper(new Profile());
            App.pane.setCenter(wrapper);
        });
        box6.setId("box6");
        grid.add(box6,2,1);
        return grid;
    }
    
}
