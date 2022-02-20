package juma.admin;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import juma.api.Database;

public class OutOfStock extends Stage {
    public BorderPane pane = new BorderPane();
    long curPage,totalPage;int lower,upper;
    String query1,query2;
    Table table = new Table();
    Label show = new Label();
    public OutOfStock(){
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
        Label close = new Label("x");Label title= new Label("List Of Out OF Stock Products");
        close.setId("close");title.setId("title");
        close.setOnMouseClicked(e->{this.close();});
        top.setLeft(close);top.setRight(title);
        pane.setTop(top);
        pane.setCenter(this.getTable());
        VBox bottom = new VBox();
        bottom.setSpacing(20);
        bottom.setId("bottom");
        Button next = new Button("Next");
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
    public void resetPageVariable(String q1, String q2){
        if(q2 != null && !q2.isEmpty()){
            query2 = q2;
        }else{
            query2 = "select count(*) from product where expiry >'"
                        +LocalDate.now().toString()+"' and remaining < 1";
        }
        if(q1 != null && !q1.isEmpty()){
           query1 = q1;
        }else{
           query1 = "select * from product where expiry >'"
                        +LocalDate.now().toString()+"' and remaining < 1";
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
                list.add(rs.getString("generic"));
                list.add(rs.getInt("quantity"));
                list.add(rs.getInt("remaining"));
                list.add(rs.getString("shelve"));
                
            }
            show.setText("showing "+curPage+" of "+totalPage);
            //reset page
            if(curPage >= totalPage){
                lower = 0;curPage = 0;
            }else{
                lower +=upper;
            }
            
        }catch(Exception e){
            e.printStackTrace();
            new MessageBox().run("oops! an error occured");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
            System.gc();
        }
        return list;
    }
    
    private Table getTable(){
        Table table = new Table();
        ArrayList list = this.paginate();
        int rows = 0; 
        int count = 0;
        if(list != null && list.size() > 0){
            rows = list.size()/5; // number of fetch item from table divided by number of table column
        }
        table.setRowSize(rows);//row size excluding table column headers(this has to be set before table column headers
        
        table.maxWidthProperty().bind(this.widthProperty().divide(1.5));
        table.maxHeightProperty().bind(this.heightProperty().divide(2.7));
        table.setColumns(new String[]{"Product Name","Generic Name","Qty","available","Shelve"});
        for(int i = 0; i < rows; i++){ //according to the row size
            Label products = new Label(String.valueOf(list.get(count)));
            count++;
            Label generic = new Label(String.valueOf(list.get(count)));
            count++;
            Label quantity = new Label(String.valueOf(list.get(count)));
            count++;
            Label remaining = new Label(String.valueOf(list.get(count)));
            count++;
            Label shelve = new Label(String.valueOf(list.get(count)));
            count++;
            table.AddRow(new Node[]{products,generic,quantity,remaining,shelve}); 
        }
        return table;
    }
    
}
