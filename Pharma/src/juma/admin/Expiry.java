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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import juma.api.Database;
import juma.cashier.Cashier;

public class Expiry extends Stage {
    public BorderPane pane = new BorderPane();
    long curPage,totalPage;int lower,upper;
    String query1,query2;
    Table table = new Table();
    Label show;
    String message = "";
    public Expiry(){
        pane.setId("root");
        Scene scene = new Scene(pane);
        scene.setFill(null);
        scene.getStylesheets().add("/juma/styles/expiry.css");
        this.setScene(scene);
        this.initStyle(StageStyle.TRANSPARENT);
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(App.stage);
        Rectangle2D rect = Screen.getPrimary().getVisualBounds();
        this.setX(rect.getMinX());
        this.setY(rect.getMinY());
        this.setWidth(rect.getWidth());
        this.setHeight(rect.getHeight());
    }
    public void run() {
        this.resetPageVariable(null,null);
        BorderPane top = new BorderPane();
        top.setId("top");
        Label close = new Label("x");Label title= new Label("List Of Out OF Date Products(Expiry)");
        close.setId("close");title.setId("title");
        close.setOnMouseClicked(e->{this.close();});
        top.setLeft(close);top.setRight(title);
        pane.setTop(top);
        VBox bottom = new VBox();
        bottom.setSpacing(20);
        bottom.setId("bottom");
        Button next = new Button("Next");
        show = new Label();
        bottom.getChildren().addAll(show,next);
        bottom.setAlignment(Pos.CENTER);
        table = this.getTable();
        pane.setCenter(table);
        next.setOnAction(e->{
            pane.getChildren().remove(table);
            table = this.getTable();
            pane.setCenter(table);
        });
        pane.setBottom(bottom);
        
        this.showAndWait();
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
        
        table.maxWidthProperty().bind(App.stage.widthProperty().divide(1.3));
        table.maxHeightProperty().bind(App.stage.heightProperty().divide(3));
        table.setColumns(new String[]{"Product Name","Generic Name","Purchased Date","Expiry Date","Shelve","Action"});
        for(int i = 0; i < rows; i++){ //according to the row size
            Label products = new Label(String.valueOf(list.get(count)));
            count++;
            String brand = String.valueOf(list.get(count));
            count++;
            Label generic = new Label(String.valueOf(list.get(count)));
            count++;
            Label arrival = new Label(String.valueOf(list.get(count)));
            count++;
            Label expire = new Label(String.valueOf(list.get(count)));
            count++;
            Label shelve = new Label(String.valueOf(list.get(count)));
            count++;
            int remaining = Integer.parseInt(String.valueOf(list.get(count)));
            count++;
            Label actions = new Label("remove");
            actions.setId("actions");
            ImageView delete = new ImageView(getClass().getResource("/juma/resources/deleteblack.png").toExternalForm());
            actions.setGraphic(delete);
            actions.setGraphicTextGap(5);
            actions.setOnMouseClicked(e->{
                this.close();
                this.delete(products.getText(), brand, remaining);
            });
            table.AddRow(new Node[]{products,generic,arrival
            ,expire,shelve,actions});
        }
        return table;
    }
    
    public void resetPageVariable(String q1, String q2){
        if(q2 != null && !q2.isEmpty()){
            query2 = q2;
        }else{
            query2 = "select count(*) from product where expiry <='"
                        +LocalDate.now().toString()+"'";
        }
        if(q1 != null && !q1.isEmpty()){
           query1 = q1;
        }else{
           query1 = "select * from product where expiry <='"
                        +LocalDate.now().toString()+"'";
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
                list.add(rs.getString("name"));
                 list.add(rs.getString("brand"));
                list.add(rs.getString("generic"));
                list.add(rs.getString("arrival"));
                list.add(rs.getString("expiry"));
                list.add(rs.getString("shelve"));
                list.add(rs.getInt("remaining"));
                
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
    public void delete(String name,String brand,int remaining){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                String sql = "delete from product where name='"+name+"' and expiry <='"+
                        LocalDate.now().toString()+"'";
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                 if(b.performBatchUpdate(new String[]{sql,"update brand set products=products-"+remaining+
                        " where name='"+brand+"'"}).length > 0){
                    message ="deleted successfully";
                }else{
                    message ="delete unsuccessful";
                }
                b.commit();
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run(message);
                     App.pane.getChildren().clear();
                     Wrapper wrapper = new Wrapper();
                     if(b.getRole().equalsIgnoreCase("admin") || b.getRole().equalsIgnoreCase("Manager")){
                         wrapper.setWrapper(new Admin());
                         App.pane.setCenter(wrapper);
                     }else if(b.getRole().equalsIgnoreCase("salesperson")){
                         wrapper.setWrapper(new Cashier());
                         App.pane.setCenter(wrapper);
                     }
                     
                });
                
            }catch(Exception e){
                e.printStackTrace();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("oops! an error occured");
                    b.rollBack();
                });
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
            
        }).start();
    }

}
