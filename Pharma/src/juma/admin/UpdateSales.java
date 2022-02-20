package juma.admin;


import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import juma.api.Cart;
import juma.api.Database;
import juma.api.Pdf;
import juma.api.Printer;
import juma.api.Query;
import juma.api.Save;

public class UpdateSales extends Stage{
    Cart oldCart = new Cart();
    Cart newCart = new Cart();
    String invoice,cust;
    Table table = new Table();
    VBox box;
    BorderPane pane = new BorderPane();
    public UpdateSales(String invoice,String cust){
        this.invoice = invoice;
        this.cust = cust;
        Scene scene = new Scene(pane);
        pane.setId("root");
        scene.getStylesheets().add("/juma/styles/update.css");
        this.setScene(scene);
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.setY(0);
        this.setX((App.stage.getWidth() - App.stage.getWidth()/1.04));
        this.setWidth(App.stage.getWidth()/1.1);
        this.setHeight(500);
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(App.stage);
        Wrapper wrapper = new Wrapper();
        wrapper.setWrapper(this.update());
        this.setTitle("Update Sales");
        BorderPane top = new BorderPane();
        top.setId("top");
        Label close = new Label("x");Label title= new Label("Update Sales");
        close.setId("close");title.setId("title");
        close.setOnMouseClicked(e->{this.close();});
        top.setLeft(close);top.setRight(title);
        pane.setTop(top);
        pane.setCenter(wrapper);
        pane.setBottom(this.bottom());
    }
    public VBox update(){
        this.setCart();
        box = new VBox();
        box.getStylesheets().add("/juma/styles/update.css");
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);
        box.setId("sales");
        Label caption = new Label("Update Sales");
        caption.setId("salesTitle");
        caption.setPrefWidth(App.stage.getWidth()/1.1);
        GridPane p = new GridPane();
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        TextField quantity = new TextField("1");
        ComboBox product= new ComboBox(Sales.fetchProducts());
        product.setValue("choose product");
        Button add = new Button("Add to cart");
        add.setGraphic(new ImageView(getClass().getResource("/juma/resources/saleswhite.png").toExternalForm()));
        add.setGraphicTextGap(5);
        add.setOnAction(e->{
            if(product.getValue() != null && !product.getValue().toString().equals("choose product")){
                if(!quantity.getText().trim().isEmpty() && quantity.getText().matches("[0-9]*")){
                    if(!this.ResolveDuplicateItems(product.getValue().toString())){
                        Query q = new Query();
                        q.setProduct(product.getValue().toString());
                        if(Integer.parseInt(quantity.getText()) <= 0){
                            quantity.setText("1");
                            q.setQuantity(1);
                        }else{
                            q.setQuantity(Integer.parseInt(quantity.getText()));
                        }
                        if(q.check(oldCart)){
                            q.setInvoice(invoice);
                            newCart.addItem(q);
                            //reset table;
                            box.getChildren().remove(2);
                            table = this.getTable();
                            box.getChildren().add(2,table);
                        }else{
                            new MessageBox().run("quantity specified is higher than available stock");
                        }
                    }else{
                         new MessageBox().run("this product is already in your cart");
                    }
                    
                }else{
                    new MessageBox().run("please enter quantity");
                }
            }else{
                new MessageBox().run("please choose a product");
            }
            
            
        });
        p.add(new VBox(new Label("Choose Product"),product), 0, 0);
        p.add(new VBox(new Label("Quantity"),quantity), 1, 0);
        p.add(add, 2, 0);
        table = this.getTable();
        box.getChildren().addAll(caption,p,table);
        return box;
    }
    private void setCart(){
        Database b = new Database();
        try{
            b.getDataConnection();
            b.getStatement();
            ResultSet rs = b.getResultSet("select * from sales where invoice='"+invoice+"'");
            while(rs.next()){
                Query q = new Query();
                q.setInvoice(rs.getString("invoice"));
                q.setProduct(rs.getString("name"));
                q.setGeneric(rs.getString("generic"));
                q.setBrand(rs.getString("brand"));
                q.setDescription(rs.getString("info"));
                q.setPrice(rs.getDouble("price"));
                q.setProfit(rs.getDouble("profit"));
                q.setAmount(rs.getDouble("amount"));
                q.setQuantity(rs.getInt("quantity"));
                oldCart.add(q);
                
            }
            rs.close();
        }catch(Exception e){
            new MessageBox().run("unable to fetch sales list for this invoice");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
        }
        //set new Cart
        newCart.addAll(oldCart);
    }
    public boolean ResolveDuplicateItems(String name){
        boolean isDuplicate = false;
        for(Query q : newCart){
            if(q.getProduct().equals(name)){
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }
    private void deleteFromCart(Query item){
        newCart.removeItem(item);
        //reset table;
        box.getChildren().remove(2);
        table = this.getTable();
        box.getChildren().add(2,table);
        
    }
   private Table getTable(){
        int totalItem = 0;double totalAmount=0;double totalProfit = 0;
        Table t = new Table();
        if(newCart != null){
             t.setRowSize(newCart.size());//row size excluding table column headers
        }else{
             t.setRowSize(1);//row size excluding table column headers
        }
       
        t.setColumns(new String[]{"#","Product Name","Generic Name",
            "Description","Price","Qauntity","Amount","Profit","Action"});
        if(newCart !=null){
            for(int i = 0; i < newCart.size(); i++){//according to the row size
                Query query = newCart.getItem(i);
                totalItem++;totalAmount+=query.getAmount();totalProfit+=query.getProfit();
                Label info = new Label();
                info.setTooltip(new Tooltip("drug information"));
                info.setGraphic(new ImageView(getClass().getResource("/juma/resources/info.png").toExternalForm()));
                info.setGraphicTextGap(5);
                info.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
                info.setOnMouseClicked(e->{new MessageBox().run(query.getDescription());});

                Label delete = new Label();
                delete.setTooltip(new Tooltip("remove from cart"));
                delete.setGraphic(new ImageView(getClass().getResource("/juma/resources/deleteblack.png").toExternalForm()));
                delete.setGraphicTextGap(5);
                delete.setStyle("-fx-font-weight:bolder;-fx-cursor:hand;");
                delete.setOnMouseClicked(e->{
                    this.deleteFromCart(query);
                });

                t.AddRow(new Node[]{new Label(String.valueOf((i+1))),new Label(query.getProduct())
                        ,new Label(query.getGeneric()),info,new Label(UpdateSales.fetchCurrency()+query.getPrice())
                        ,new Label(String.valueOf(query.getQuantity())),new Label(UpdateSales.fetchCurrency()+query.getAmount())
                        ,new Label(UpdateSales.fetchCurrency()+query.getProfit()),new HBox(delete)});
            }
            Label key = new Label("Total Item");
            key.setStyle("-fx-font-weight:bolder;");
            Label value = new Label(String.valueOf(totalItem));
            value.setStyle("-fx-font-weight:bolder;");
            t.AddFooterRows(new Node[]{key,value});
            
            key = new Label("Total Amount");
            key.setStyle("-fx-font-weight:bolder;");
            value = new Label(UpdateSales.fetchCurrency()+String.valueOf(totalAmount));
            value.setStyle("-fx-font-weight:bolder;");
            t.AddFooterRows(new Node[]{key,value});
            
            key = new Label("Total Profit");
            key.setStyle("-fx-font-weight:bolder;");
            value = new Label(UpdateSales.fetchCurrency()+String.valueOf(totalProfit));
            value.setStyle("-fx-font-weight:bolder;");
            t.AddFooterRows(new Node[]{key,value});
            
        }
        
        return t;
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
    public static ObservableList<String> fetchProducts(){
        ArrayList<String> list = new ArrayList<>();
         Database b = new Database();
            try{
                b.getDataConnection();
                b.getStatement();
                ResultSet rs = b.getResultSet("select distinct name from product");
                while(rs.next()){
                    list.add(rs.getString(1));
                }
                rs.close();
            }catch(Exception e){
                new MessageBox().run("unable to fetch product list");
            }finally{
                b.closeResultSet();
                b.closeStatement();
                b.closeConnection();
            }
        return FXCollections.observableList(list);
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
            if(newCart != null  && newCart.size() > 0){
                InputBox input = new InputBox();
                input.set(cust);
                input.run("Customer Name Update");
                this.close();
                Save s = new Save(input.get().isEmpty()?"Anonymous":input.get(),oldCart,newCart);
            }else{
                new MessageBox().run("you have not entered any item to cart");
            }
        });
        Label print = new Label("Print");
        print.setTooltip(new Tooltip("Print Invoice Only"));
        print.setGraphic(new ImageView(getClass().getResource("/juma/resources/print.png").toExternalForm()));
        print.setGraphicTextGap(5);
        print.setStyle("-fx-font-weight:bolder;-fx-cursor:hand;");
        print.setOnMouseClicked(e->{
            try {
                Printer printer = new Printer(newCart);
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
             Pdf p = new Pdf(newCart);
             p.create();
        });
        HBox b = new HBox(save,pdf,print);
        b.setSpacing(20);
        pane.setRight(b);
        return pane;
    }
    
   
}