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
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
public class Brand extends Admin{
    boolean isShowing = false;
    VBox pane;
    VBox center;
    String message = "";
    long curPage,totalPage;int lower,upper;
    String query1,query2;
    Table table = new Table();
    Label show;
    public Brand(){
        super();
    }
    @Override
    protected void Middle(){
        //current link
        brand.setId("brand2");
        brand = new Label("Home / Brand");
        brand.setId("brand3");
        HBox classLink = new HBox(brand);
        classLink.setId("link");
        pane = this.addBrand();
        center = new VBox(classLink,this.brand());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
        this.setCenter(center);
    }
    public VBox brand(){
        this.resetPageVariable(null,null);
        VBox box = new VBox();
        box.getStylesheets().add("/juma/styles/brand.css");
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(App.stage.getWidth()/1.1);
        box.setId("brand");
        Label caption = new Label("View Brand");
        caption.setId("brandTitle");
        
        Button add = new Button("Add New Brand");
        HBox addBox = new HBox(add);
        addBox.setAlignment(Pos.CENTER_RIGHT);
        
        HBox search = new HBox();
        search.setId("search");
        TextField field = new TextField();
        Label find = new Label();
        find.setOnMouseClicked(e->{
            if(!field.getText().isEmpty()){
                box.getChildren().remove(3);
                this.resetPageVariable("select * from brand where name like '"+field.getText()+
                        "%' order by id desc",
                        "select count(*) from brand where name like '"+field.getText()+
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
               add.setText("Add New Brand");
            }else{
                center.getChildren().add(1,pane);
                isShowing = true;
                add.setText("Hide Form");
            }
           
        });
        return box;
    }
    private VBox addBrand(){
        VBox panes = new VBox();
        panes.setId("brand");
        panes.getStylesheets().add("/juma/styles/brand.css");
        TextField addField = new TextField();
        Label caption = new Label("Add New Brand");
        caption.setId("addTitle");
        Button submit = new Button("+ Add New Brand");
        submit.setOnAction(e->{
            if(!addField.getText().isEmpty()){
                this.createNewBrand("insert into brand values(0,'"+addField.getText()+"',0)"
                        , addField.getText());
            }else{
                new MessageBox().run("please enter brand name");
            }
        });
        panes.setSpacing(20);
        panes.getChildren().addAll(caption,addField,submit);
        
        return panes;
    }
    private Table getTable(){
        Table table = new Table();
        ArrayList list = this.paginate();
        int rows = 0; 
        int count = 0;
        if(list != null && list.size() > 0){
            rows = list.size()/3; // number of fetch item from table divided by number of table column
        }
        table.setRowSize(rows);//row size excluding table column headers(this has to be set before table column headers
        
        table.setColumns(new String[]{"#","Brand Name","Num Of Product","Action"});
        for(int i = 0; i < rows; i++){//according to the row size
            Label id = new Label(String.valueOf(list.get(count)));
            count++;
            Label name = new Label(String.valueOf(list.get(count)));
            count++;
            Label number = new Label(String.valueOf(list.get(count)));
            count++;
            BrandUpdate bp = new BrandUpdate();
            bp.setName(name.getText());
            bp.setId(Integer.parseInt(id.getText()));
            Label delete = new Label();
            delete.setTooltip(new Tooltip("delete"));
            delete.setGraphic(new ImageView(getClass().getResource("/juma/resources/deleteblack.png").toExternalForm()));
            delete.setGraphicTextGap(5);
            delete.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
            delete.setOnMouseClicked(e->{
                QuestionBox q = new QuestionBox();
                QuestionBox.Option option = q.run("about to delete this brand ?");
                if(option == QuestionBox.Option.OK){
                   this.delete(bp.getId());
                }
            });
            Label update = new Label();
            update.setTooltip(new Tooltip("update"));
            update.setGraphic(new ImageView(getClass().getResource("/juma/resources/updateblack.png").toExternalForm()));
            update.setGraphicTextGap(5);
            update.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
            update.setOnMouseClicked(e->{
               bp.run();
            });
            
            table.AddRow(new Node[]{id,name,number,new HBox(delete,update)});
        }
        return table;
    }
    public void resetPageVariable(String q1, String q2){
        if(q2 != null && !q2.isEmpty()){
            query2 = q2;
        }else{
            query2 = "select count(*) from brand";
        }
        if(q1 != null && !q1.isEmpty()){
           query1 = q1;
        }else{
           query1 = "select * from brand order by id desc";
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
                list.add(rs.getLong("products"));
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
                String sql = "delete from brand where id="+id;
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
                     wrapper.setWrapper(new Brand());
                     App.pane.setCenter(wrapper);
                     
                });
                
            }catch(Exception e){
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run("Error trying to delete brand item");
                });
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
            
        }).start();
    }
    public void createNewBrand(String sql,String brand){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
               ResultSet rs=  b.getResultSet("select * from brand where name ='"+brand+"'");
               if(rs.next()){
                   message = "sorry brand already exist";
               }else{
                   if(b.performBatchUpdate(new String[]{sql}).length > 0){
                        message = "brand added successfully";
                    }else{
                        message = "error trying to add new brand";
                    }
               }
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Brand());
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



class BrandUpdate extends Stage{
    String name;int id;String message;
    BorderPane pane = new BorderPane();
    public BrandUpdate(){
        Scene scene = new Scene(pane);
        pane.setId("root");
        scene.getStylesheets().add("/juma/styles/brand.css");
        this.setScene(scene);
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.setTitle("Update Brand");
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
        Label close = new Label("x");Label title= new Label("Update Brand");
        close.setId("close");title.setId("title");
        close.setOnMouseClicked(e->{this.close();});
        top.setLeft(close);top.setRight(title);
        pane.setTop(top);
        pane.setCenter(this.brandUpdate());
        this.showAndWait();
    }
    public void update(String value){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                String sql1 = "update brand set name='"+value+"' where name = '"+this.getName()+"'";
                String sql2 = "update product set brand='"+value+"' where brand = '"+this.getName()+"'";
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
                     wrapper.setWrapper(new Brand());
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
   
    private VBox brandUpdate(){
        VBox panes = new VBox();
        panes.setId("brand");
        panes.getStylesheets().add("/juma/styles/brand.css");
        TextField addField = new TextField();
        addField.setText(this.getName());
        Label caption = new Label("Update Brand");
        caption.setId("addTitle");
        Button submit = new Button("Save Update");
        submit.setOnAction(e->{
            if(!addField.getText().isEmpty()){
              this.close();this.update(addField.getText());
            }else{
                new MessageBox().run("please enter brand name");
            }
            
        });
        panes.setSpacing(20);
        panes.getChildren().addAll(caption,addField,submit);
        
        return panes;
    }
    
    public String getName(){
       return this.name;
    }
    public void setName(String value){
         this.name = value;
    }
    public int getId(){
       return this.id;
    }
    public void setId(int value){
         this.id = value;
    }
}


