/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.cashier;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.application.Platform;
import juma.admin.App;
import juma.admin.Table;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import juma.admin.MessageBox;
import juma.admin.ProgressBox;
import juma.admin.QuestionBox;
import juma.admin.UpdateSales;
import juma.admin.Wrapper;
import juma.api.Database;

/**
 *
 * @author user1
 */
public class SalesQuery extends Cashier{
   String message = "";
    long curPage,totalPage;int lower,upper;
    String query1,query2;
    Table table = new Table();
    Label show;
    int qty = 0;
    public SalesQuery(){
        super();
    }
    @Override
    protected void Middle(){
        //current link
        salesQuery.setId("salesQuery2");
        salesQuery = new Label("Home / Sales Query");
        salesQuery.setId("salesQuery3");
        HBox classLink = new HBox(salesQuery);
        classLink.setId("link");
        VBox center = new VBox(classLink,this.query());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
    }
    
    public VBox query(){
        this.resetPageVariable(null,null);
        VBox box = new VBox();
        box.getStylesheets().add("/juma/styles/invoice.css");
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(App.stage.getWidth()/1.1);
        box.setId("invoice");
        Label caption = new Label("Invoice Query");
        caption.setId("invoiceTitle");
        
        HBox search = new HBox();
        search.setId("search");
        TextField field = new TextField();
        Label find = new Label();
        find.setOnMouseClicked(e->{
            if(!field.getText().isEmpty()){
                box.getChildren().remove(2);
                this.resetPageVariable("select * from invoice where invoice like '"+field.getText()+
                        "%' or customer like'"+field.getText()+"%' and seller like'"+new Database().getUser()+
                        "%' or date like'"+field.getText()+"%' order by id desc",
                        "select count(*) from invoice where invoice like '"+field.getText()+
                        "%' or customer like'"+field.getText()+"%' and seller like'"+new Database().getUser()+
                        "%' or date like'"+field.getText()+"%' order by id desc");
                table = this.getTable();
                box.getChildren().add(2, table);
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
        box.getChildren().addAll(caption,search,table,bottom);
        next.setOnAction(e->{
            box.getChildren().remove(2);
            table = this.getTable();
            box.getChildren().add(2, table);
        });
        return box;
    }
    
    private Table getTable(){
        String currency = SalesQuery.fetchCurrency();
        Table table = new Table();
        ArrayList list = this.paginate();
        int rows = 0; 
        int count = 0; double totalProfit = 0,totalAmount = 0;
        if(list != null && list.size() > 0){
            rows = list.size()/7; // number of fetch item from table divided by number of table column
        }
        table.setRowSize(rows);//row size excluding table column headers(this has to be set before table column headers
        
        table.setColumns(new String[]{"#","Invoice","Price","Profit","Customer","Sold By","Date","Action"});
        for(int i = 0; i < rows; i++){ //according to the row size
            Label id = new Label(String.valueOf(list.get(count)));
            count++;
            Label invoice = new Label(String.valueOf(list.get(count)));
            Label delete = new Label();
            delete.setTooltip(new Tooltip("delete"));
            delete.setGraphic(new ImageView(getClass().getResource("/juma/resources/deleteblack.png").toExternalForm()));
            delete.setGraphicTextGap(5);
            delete.setStyle("-fx-font-weight:bolder;-fx-cursor:hand;");
            delete.setOnMouseClicked(e->{
                QuestionBox q = new QuestionBox();
                QuestionBox.Option option = q.run("about to undo/delete this invoice?");
                if(option == QuestionBox.Option.OK){
                   this.delete(invoice.getText());
                }
            });
            count++;
            Label amount = new Label(SalesQuery.fetchCurrency()+String.valueOf(list.get(count)));
            totalAmount +=Double.parseDouble(String.valueOf(list.get(count)));
            count++;
            Label profit = new Label(SalesQuery.fetchCurrency()+String.valueOf(list.get(count)));
            totalProfit +=Double.parseDouble(String.valueOf(list.get(count)));
            count++;
            Label customer = new Label(String.valueOf(list.get(count)));
            Label update = new Label();
            update.setTooltip(new Tooltip("update"));
            update.setGraphic(new ImageView(getClass().getResource("/juma/resources/updateblack.png").toExternalForm()));
            update.setGraphicTextGap(5);
            update.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
            update.setOnMouseClicked(e->{
                //grab all sales tied to the invoice number and push to new sales for editing
                UpdateSales sales = new UpdateSales(invoice.getText(),customer.getText());
                sales.showAndWait();
            });
            count++;
            Label seller = new Label(String.valueOf(list.get(count)));
            count++;
            Label date = new Label(String.valueOf(list.get(count)));
            count++;
            
            table.AddRow(new Node[]{id,invoice,amount,profit,customer,seller,date,new HBox(delete,update)});
            
        }
        Label key = new Label("Total Price");
        key.setStyle("-fx-font-weight:bolder;");
        Label value = new Label(SalesQuery.fetchCurrency()+totalAmount);
        value.setStyle("-fx-font-weight:bolder;");
        table.AddFooterRows(new Node[]{key,value});
        key = new Label("Total Profit");
        key.setStyle("-fx-font-weight:bolder;");
        value = new Label(SalesQuery.fetchCurrency()+totalProfit);
        value.setStyle("-fx-font-weight:bolder;");
        table.AddFooterRows(new Node[]{key,value});
        return table;
    }
    public static String fetchCurrency(){
        String currency = "";
         Database b = new Database();
         ResultSet rs;
        try{
            b.getDataConnection();
            b.getStatement();
            rs = b.getResultSet("select currency from settings");
            if(rs.next()){
                if(rs.getString(1).endsWith("(N)")){
                    currency = "N";
                }else if(rs.getString(1).endsWith("($)")){
                    currency = "$";
                }
            }
            rs.close();
        }catch(Exception e){
            new MessageBox().run("unable to fetch currency Settings");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
        }
        return currency;
    }
    public void resetPageVariable(String q1, String q2){
        if(q2 != null && !q2.isEmpty()){
            query2 = q2;
        }else{
            query2 = "select count(*) from invoice where seller='"+new Database().getUser()+"'";
        }
        if(q1 != null && !q1.isEmpty()){
           query1 = q1;
        }else{
           query1 = "select * from invoice where seller='"+new Database().getUser()+"' order by id desc";
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
                list.add(rs.getString("invoice"));
                list.add(rs.getDouble("amount"));
                list.add(rs.getDouble("profit"));
                list.add(rs.getString("customer"));
                list.add(rs.getString("seller"));
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
    public void delete(String invoice){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                ArrayList<String>queryList = new ArrayList<>();
                String sql = "select name,brand,quantity from sales where invoice='"+invoice+"'";
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                ResultSet rs = b.getResultSet(sql);
                while(rs.next()){
                    queryList.add("update product set remaining=remaining+"+rs.getInt(3)+" where name='"+
                            rs.getString(1)+"' and expiry > '"+LocalDate.now().toString()+"' limit 1");
                    queryList.add("update brand set products=products+"+rs.getInt(3)+" where name='"+
                            rs.getString(2)+"'");
                }
                rs.close();
                queryList.add(0, "delete from invoice where invoice='"+invoice+"'");
                queryList.add(1, "delete from sales where invoice='"+invoice+"'");
                 if(b.performBatchUpdate(queryList.toArray(new String[queryList.size()])).length > 0){
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
                     wrapper.setWrapper(new SalesQuery());
                     App.pane.setCenter(wrapper);
                     
                });
                
            }catch(Exception e){
                e.printStackTrace();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("oops! an error occured");
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
