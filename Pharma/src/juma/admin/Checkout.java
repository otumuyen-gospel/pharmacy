/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.admin;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import juma.api.Cart;
import juma.api.Database;
import juma.api.GenerateId;
import juma.api.Pdf;
import juma.api.Printer;
import juma.api.Query;
import juma.api.Save;
import juma.api.Session;

/**
 *
 * @author user1
 */
public class Checkout extends Admin{
    Cart cart = new Cart();
    Table table;
    VBox boxed;
    VBox center;
    ArrayList list;
    Blob blue;Image image;
    public Checkout(){
        super();
    }
    @Override
    protected void Middle(){
        //fetch settings
         list = this.fetchHeadings();
        //current link
        sales.setId("sales2");
        sales = new Label("Home / New Sales / Checkout");
        sales.setId("sales3");
        HBox classLink = new HBox(sales);
        classLink.setId("link");
        center = new VBox(classLink,this.checkOut());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
        this.setCenter(center);
    }
    public ArrayList fetchHeadings(){
        //query database(product table) and set other items here
        Database b = new Database();
        ArrayList list = new ArrayList();
        try{
            b.getDataConnection();
            b.getStatement();
            ResultSet rs = b.getResultSet("select * from settings");
            if(rs.next()){
                list.add(rs.getString("name"));
                list.add(rs.getString("currency"));
                list.add(rs.getString("telephone"));
                list.add(rs.getString("email"));
                list.add(rs.getBlob("blue"));
            }
            rs.close();
        }catch(Exception e){
            new MessageBox().run("error fetching settings");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
            System.gc();
        }
        return list;
    }
    public VBox checkOut(){
        VBox box = new VBox();
        box.getStylesheets().add("/juma/styles/checkout.css");
        box.setSpacing(20);
        box.setId("sales");
        Label caption = new Label("Checkout");
        caption.setId("salesTitle");
        if(Session.get() != null){
            //retrive cart sessions
            cart = (Cart)Session.get();
        }else{
            //retrive create new cart
            cart = new Cart();
        }
        table = this.getTable();
        boxed = new VBox();
        boxed.getChildren().addAll(this.top(),table);
        boxed.setSpacing(20);
        boxed.setId("sales");
        box.getChildren().addAll(caption,boxed,this.bottom());
        return box;
    }
    public BorderPane top(){
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20)); 
        Label name = new Label(String.valueOf(list.get(0)));
        blue = (Blob)list.get(list.size()-1);
        try {
            image = new Image(blue.getBinaryStream());
            if(image == null || blue.length() <= 0){
                image = new Image(getClass().getResource("/juma/resources/logoblue.png").toExternalForm());
            }
        }catch(SQLException ex) {}
        ImageView logo = new ImageView(image);
        logo.setFitHeight(24);
        logo.setFitWidth(24);
        name.setGraphic(logo);
        name.setContentDisplay(ContentDisplay.TOP);
        name.setGraphicTextGap(10);
        VBox title = new VBox(name,new Label(String.valueOf(list.get(3))), 
                new Label(String.valueOf(list.get(2))));
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-weight:bolder;");
        pane.setTop(title);
        BorderPane.setMargin(title, new Insets(20));
        
        Label id = new Label("Invoice No: "+GenerateId.get());
        id.setStyle("-fx-font-weight:bolder;");
        pane.setLeft(id);
        
        Label date = new Label("Date: "+LocalDate.now().toString());
        date.setStyle("-fx-font-weight:bolder;");
        HBox center = new HBox(id,date);
        center.setSpacing(100);
        center.setAlignment(Pos.CENTER);
        pane.setCenter(center);
        return pane;
    }
    public BorderPane bottom(){
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));
        Label save = new Label("Save");
        save.setTooltip(new Tooltip("Save and Close Invoice"));
        save.setGraphic(new ImageView(getClass().getResource("/juma/resources/save.png").toExternalForm()));
        save.setGraphicTextGap(5);
        save.setStyle("-fx-font-weight:bolder;-fx-cursor:hand;");
        save.setOnMouseClicked(e->{
            //after saving to database destroy session
            new Save();
        });
        Label print = new Label("Print");
        print.setTooltip(new Tooltip("Print Invoice Only"));
        print.setGraphic(new ImageView(getClass().getResource("/juma/resources/print.png").toExternalForm()));
        print.setGraphicTextGap(5);
        print.setStyle("-fx-font-weight:bolder;-fx-cursor:hand;");
        print.setOnMouseClicked(e->{
            try {
                Printer printer = new Printer((Cart)Session.get());
                printer.run();
            } catch (Exception ex) {
                new MessageBox().run("some unknown error occured");
            }
        });
        Label pdf = new Label("PDF");
        pdf.setTooltip(new Tooltip("Export Invoice To Pdf"));
        pdf.setGraphic(new ImageView(getClass().getResource("/juma/resources/file.png").toExternalForm()));
        pdf.setGraphicTextGap(5);
        pdf.setStyle("-fx-font-weight:bolder;-fx-cursor:hand;");
        pdf.setOnMouseClicked(e->{
            Pdf p =new Pdf((Cart)Session.get());
            p.create();
        });
        HBox b = new HBox(save,pdf,print);
        b.setSpacing(20);
        pane.setRight(b);
        return pane;
    }
    
    private Table getTable(){
        int totalItem = 0;double totalAmount=0;String products = "";
        int len = 100;
        String currency = String.valueOf(list.get(1));
        if(currency.endsWith("(N)")){
            currency = "N";
        }else if(currency.endsWith("($)")){
            currency = "$";
        }
        Table t = new Table();
        if(cart != null){
             t.setRowSize(cart.size());//row size excluding table column headers
        }else{
             t.setRowSize(1);//row size excluding table column headers
        }
       
        t.setColumns(new String[]{"#","Summary"});
        if(cart !=null){
            for(int i = 0; i < cart.size(); i++){//according to the row size
                Query query = cart.getItem(i);
                totalItem+=query.getQuantity();totalAmount+=query.getAmount();
                products+=query.getProduct()+" ";
                if(products.length() % len == (i * len) ){
                    products += "\n";
                }
            }
            t.AddRow(new Node[]{new Label("List Of Drugs"),new Label(products)});
            Label key = new Label("Total Item");
            key.setStyle("-fx-font-weight:bolder;");
            Label value = new Label(String.valueOf(totalItem));
            value.setStyle("-fx-font-weight:bolder;");
            t.AddFooterRows(new Node[]{key,value});
            
            key = new Label("Total Amount");
            key.setStyle("-fx-font-weight:bolder;");
            value = new Label(currency+String.valueOf(totalAmount));
            value.setStyle("-fx-font-weight:bolder;");
            t.AddFooterRows(new Node[]{key,value});
            
        }
        
        return t;
    }
    
}
