/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.admin;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import juma.api.Database;

/**
 *
 * @author user1
 */
public class Supplier extends Admin{
    boolean isShowing = false;
    GridPane pane;
    VBox center;
    String message = "";
    long curPage,totalPage;int lower,upper;
    String query1,query2;
    Table table = new Table();
    Label show;
    public Supplier(){
        super();
    }
    @Override
    protected void Middle(){
        //current link
        supplier.setId("supplier2");
        supplier = new Label("Home / New Supplier");
        supplier.setId("supplier3");
        HBox classLink = new HBox(supplier);
        classLink.setId("link");
        pane  = this.AddSupplier();
        center = new VBox(classLink,this.suppliers());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
    }
    
public VBox suppliers(){
        this.resetPageVariable(null,null);
        VBox box = new VBox();
        box.getStylesheets().add("/juma/styles/supplier.css");
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(App.stage.getWidth()/1.1);
        box.setId("supplier");
        Label caption = new Label("View Suppliers");
        caption.setId("supplierTitle");
        
        Button add = new Button("Add New Supplier");
        HBox addBox = new HBox(add);
        addBox.setAlignment(Pos.CENTER_RIGHT);
        
        HBox search = new HBox();
        search.setId("search");
        TextField field = new TextField();
        Label find = new Label();
        find.setOnMouseClicked(e->{
            if(!field.getText().isEmpty()){
                box.getChildren().remove(3);
                this.resetPageVariable("select * from supplier where name like '"+field.getText()+
                        "%' order by id desc",
                        "select count(*) from supplier where name like '"+field.getText()+
                        "%' order by id desc");
                table = this.getTable();
                box.getChildren().add(3, table);
            }else{
                new MessageBox().run("please enter search word in the box");
            }
            
        });
        find.setGraphic(new ImageView(getClass().getResource("/juma/resources/searchwhite.png").toExternalForm()));
        search.getChildren().addAll(field,find);
        search.setAlignment(Pos.CENTER);
        
        VBox bottom = new VBox();
        bottom.setSpacing(20);
        bottom.setId("bottom");
        Button next = new Button("Next");
        show = new Label();
        bottom.getChildren().addAll(show,next);
        bottom.setAlignment(Pos.CENTER);
        table = this.getTable();
        box.getChildren().addAll(addBox,caption,search,table,bottom);
        next.setOnAction(e->{
            box.getChildren().remove(3);
            table = this.getTable();
            box.getChildren().add(3, table);
        });
        add.setOnAction(e->{
            if(isShowing){
                center.getChildren().remove(1);
                isShowing = false;
               add.setText("Add New Supplier");
            }else{
                center.getChildren().add(1,pane);
                isShowing = true;
                add.setText("Hide Form");
            }
           
        });
        return box;
    }
    private Table getTable(){
        Table table = new Table();
        ArrayList list = this.paginate();
        int rows = 0; 
        int count = 0;
        if(list != null && list.size() > 0){
            rows = list.size()/7; // number of fetch item from table divided by number of table column
        }
        table.setRowSize(rows);//row size excluding table column headers(this has to be set before table column headers
        
        table.setColumns(new String[]{"#","Supplier Name","Referrer","Contact No","Address","Items","Date","Action"});
        for(int i = 0; i < rows; i++){//according to the row size
            SupplierUpdate runup = new SupplierUpdate();
            runup.setId(Integer.parseInt(String.valueOf(list.get(count))));
            Label id = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setName(String.valueOf(list.get(count)));
            Label name = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setReference(String.valueOf(list.get(count)));
            Label reference = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setContact(String.valueOf(list.get(count)));
            Label contact = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setAddress(String.valueOf(list.get(count)));
            Label address = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setItems(String.valueOf(list.get(count)));
            Label items = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setDate(String.valueOf(list.get(count)));
            Label date = new Label(String.valueOf(list.get(count)));
            count++;
            Label delete = new Label();
            delete.setTooltip(new Tooltip("delete"));
            delete.setGraphic(new ImageView(getClass().getResource("/juma/resources/deleteblack.png").toExternalForm()));
            delete.setGraphicTextGap(5);
            delete.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
            delete.setOnMouseClicked(e->{
                QuestionBox q = new QuestionBox();
                QuestionBox.Option option = q.run("about to delete this supplier Information ?");
                if(option == QuestionBox.Option.OK){
                   this.delete(Integer.parseInt(id.getText()));
                }
            });
            Label update = new Label();
            update.setTooltip(new Tooltip("update"));
            update.setGraphic(new ImageView(getClass().getResource("/juma/resources/updateblack.png").toExternalForm()));
            update.setGraphicTextGap(5);
            update.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
            update.setOnMouseClicked(e->{
               runup.run();
            });
            
            table.AddRow(new Node[]{id,name,reference,contact,address,items,date,new HBox(delete,update)});
        }
        return table;
    }
    public GridPane AddSupplier(){
        GridPane p = new GridPane();
        p.setId("supplier");
        p.getStylesheets().add("/juma/styles/supplier.css");
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        TextField name = new TextField();
        TextField reference = new TextField();
        DatePicker date = new DatePicker();
        
        TextArea address = new TextArea();
        TextField contact = new TextField();
        Button submit = new Button("Submit");
        submit.setOnAction(e->{
            if(!name.getText().isEmpty()){
                if(!reference.getText().isEmpty()){
                    if(date.getValue() != null){
                        if(!address.getText().isEmpty()){
                            if(!contact.getText().isEmpty()){
                                String sql = "insert into supplier values(0,'"+name.getText()+"','"+
                                        reference.getText()+"','"+contact.getText()+"','"+
                                        address.getText()+"',0,'"+date.getValue()+"')";
                                this.createNewSupplier(sql, name.getText());
                            }else{
                                new MessageBox().run("please enter supplier contact no");
                            }
                        }else{
                            new MessageBox().run("please enter supplier address");
                        }
                    }else{
                        new MessageBox().run("please enter joined date");
                    }
                }else{
                     new MessageBox().run("please enter supplier reference");
                }
            }else{
                new MessageBox().run("please enter supplier name");
            }
        });
        Label caption = new Label("Add New Supplier");
        caption.setId("addTitle");
        p.add(caption, 0, 0, 2, 1);
        p.add(new VBox(new Label("Supplier Name"),name), 0, 1);
        p.add(new VBox(new Label("Reference By"),reference), 1, 1);
        p.add(new VBox(new Label("Contact No"),contact), 0, 2);
        p.add(new VBox(new Label("Address"),address), 1, 2);
        p.add(new VBox(new Label("joined Date"),date), 0, 3);
        p.add(submit, 1, 4);
        return p;
    }
    public void resetPageVariable(String q1, String q2){
        if(q2 != null && !q2.isEmpty()){
            query2 = q2;
        }else{
            query2 = "select count(*) from supplier";
        }
        if(q1 != null && !q1.isEmpty()){
           query1 = q1;
        }else{
           query1 = "select * from supplier order by id desc";
        }
        lower = 0;upper = 5;curPage = 0;totalPage = 0;
    }
    public ArrayList paginate(){
        ArrayList list = new ArrayList();
        Database b= new Database();
        try{
            b.getDataConnection();
            b.getStatement();
            ResultSet rs = b.getResultSet(query2);
            if(rs.next()){
                 curPage++;totalPage = 1;
                 long count = rs.getInt(1);
                if(count > upper){
                    //if database value is even divide but if database value is odd divide and approx by 1
                    totalPage = count % upper == 0 ? count / upper : count/upper + 1;
                }
                if(count == 0){
                    curPage = 0;totalPage = 0;
                }
            }
            rs = b.getResultSet(query1+" limit "+lower+","+upper);
            while(rs.next()){
                list.add(rs.getInt("id"));
                list.add(rs.getString("name"));
                list.add(rs.getString("reference"));
                list.add(rs.getString("contact"));
                list.add(rs.getString("address"));
                list.add(rs.getLong("items"));
                list.add(rs.getString("date"));
            }
            show.setText("showing "+curPage+" of "+totalPage);
            //reset page
            if(curPage >= totalPage){
                lower = 0;curPage = 0;
            }else{
                lower +=upper;
            }
            
        }catch(Exception e){
            new MessageBox().run("oops! an error occured");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
            System.gc();
        }
        return list;
    }
    public void delete(int id){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                String sql = "delete from supplier where id="+id;
                b.getDataConnection();
                b.getStatement();
                if(b.performBatchUpdate(new String[]{sql}).length > 0){
                    message = "deleted successfully";
                }else{
                    message = "delete operation unsuccessful";
                }
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run(message);
                     App.pane.getChildren().clear();
                     Wrapper wrapper = new Wrapper();
                     wrapper.setWrapper(new Supplier());
                     App.pane.setCenter(wrapper);
                     
                });
                
            }catch(Exception e){
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run("Error trying to delete supplier information ");
                });
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
            
        }).start();
    }
    public void createNewSupplier(String sql,String name){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
               ResultSet rs=  b.getResultSet("select * from supplier where name ='"+name+"'");
               if(rs.next()){
                   message = "sorry supplier already exist";
               }else{
                   if(b.performBatchUpdate(new String[]{sql}).length > 0){
                        message = "supplier added successfully";
                    }else{
                        message = "error trying to add new supplier";
                    }
               }
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Supplier());
                    App.pane.setCenter(wrapper);
                });
            }catch(Exception e){
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("unknown server error ");
                    b.rollBack();
                });
            }finally{
                b.closeResultSet();
                b.closeStatement();
                b.closeConnection();
            }
        }).start();
    }
        
}


class SupplierUpdate extends Stage{
    String name,reference,contact,address,items,date;int id;
    String message = "";
    BorderPane pane = new BorderPane();
    public SupplierUpdate(){
        Scene scene = new Scene(pane);
        pane.setId("root");
        scene.getStylesheets().add("/juma/styles/supplier.css");
        this.setScene(scene);
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.setTitle("Update Supplier");
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(App.stage);
        Rectangle2D rect = Screen.getPrimary().getVisualBounds();
        this.setX(rect.getMaxX()/6);
        this.setY(0);
        this.setWidth(rect.getWidth()/1.5);
    }
    public void run(){
        BorderPane top = new BorderPane();
        top.setId("top");
        Label close = new Label("x");Label title= new Label("Update Supplier");
        close.setId("close");title.setId("title");
        close.setOnMouseClicked(e->{this.close();});
        top.setLeft(close);top.setRight(title);
        pane.setTop(top);
        pane.setCenter(this.updateSupplier());
        this.showAndWait();
    }
    public void update(String sql1,String sql2){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                if(b.performBatchUpdate(new String[]{sql1,sql2}).length > 0){
                    message ="updated successfully";
                }else{
                    message ="update unsuccessful";
                }
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run(message);
                     App.pane.getChildren().clear();
                     Wrapper wrapper = new Wrapper();
                     wrapper.setWrapper(new Supplier());
                     App.pane.setCenter(wrapper);
                     
                });
                b.commit();
            }catch(Exception e){
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run("server update error");
                     b.rollBack();
                });
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
            
        }).start();
    }
   public GridPane updateSupplier(){
       GridPane p = new GridPane();
        p.setId("supplier");
        p.getStylesheets().add("/juma/styles/supplier.css");
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        TextField names = new TextField();
        names.setText(this.getName());
        TextField references = new TextField();
        references.setText(this.getReference());
        DatePicker dates = new DatePicker();
        LocalDate local =LocalDate.parse(this.getDate());
        dates.setValue(local);
        
        TextArea addresss = new TextArea();
        addresss.setText(this.getAddress());
        TextField contacts = new TextField();
        contacts.setText(this.getContact());
        Button submit = new Button("Save Update");
        submit.setOnAction(e->{
            if(!names.getText().isEmpty()){
                if(!references.getText().isEmpty()){
                    if(dates.getValue() != null){
                        if(!addresss.getText().isEmpty()){
                            if(!contacts.getText().isEmpty()){
                                this.close();
                                String sql1 = "update supplier set name='"+names.getText()+"',reference='"+
                                        references.getText()+"',contact='"+contacts.getText()+"',address='"+
                                        addresss.getText()+"',date='"+dates.getValue()+"' where name='"+
                                        this.getName()+"'";
                                String sql2 = "update product set supplier='"+names.getText()+"' where supplier='"+
                                        this.getName()+"'";
                                this.update(sql1, sql2);
                                
                            }else{
                                new MessageBox().run("please enter supplier contact no");
                            }
                        }else{
                            new MessageBox().run("please enter supplier address");
                        }
                    }else{
                        new MessageBox().run("please enter joined date");
                    }
                }else{
                     new MessageBox().run("please enter supplier reference");
                }
            }else{
                new MessageBox().run("please enter supplier name");
            }
        });
        Label caption = new Label("Update Supplier");
        caption.setId("addTitle");
        p.add(caption, 0, 0, 2, 1);
        p.add(new VBox(new Label("Supplier Name"),names), 0, 1);
        p.add(new VBox(new Label("Reference By"),references), 1, 1);
        p.add(new VBox(new Label("Contact No"),contacts), 0, 2);
        p.add(new VBox(new Label("Address"),addresss), 1, 2);
        p.add(new VBox(new Label("joined Date"),dates), 0, 3);
        p.add(submit, 1, 4);
        return p;
    }
    public int getId(){
       return this.id;
    }
    public void setId(int value){
         this.id = value;
    }
    public String getName(){
       return this.name;
    }
    public void setName(String value){
         this.name = value;
    }
    public String getReference(){
       return this.reference;
    }
    public void setReference(String value){
         this.reference = value;
    }
    public String getContact(){
       return this.contact;
    }
    public void setContact(String value){
         this.contact = value;
    }
    public String getAddress(){
       return this.address;
    }
    public void setAddress(String value){
         this.address = value;
    }
    public String getItems(){
       return this.items;
    }
    public void setItems(String value){
         this.items = value;
    }
    public String getDate(){
       return this.date;
    }
    public void setDate(String value){
         this.date = value;
    }
    
}

