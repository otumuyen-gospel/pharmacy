package juma.cashier;


import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import juma.admin.App;
import juma.admin.MessageBox;
import juma.admin.Table;
import juma.admin.Wrapper;
import juma.api.Cart;
import juma.api.Database;
import juma.api.GenerateId;
import juma.api.Query;
import juma.api.Session;

/**
 *
 * @author user1
 */
public class Sales extends Cashier{
    Cart cart;
    Table table = new Table();
    VBox box;
    public Sales(){
        super();
    }
    @Override
    protected void Middle(){
        //current link
        sales.setId("sales2");
        sales = new Label("Home / New Sales");
        sales.setId("sales3");
        HBox classLink = new HBox(sales);
        classLink.setId("link");
        VBox center = new VBox(classLink,this.newSales());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
    }
    
    public VBox newSales(){
        box = new VBox();
        box.getStylesheets().add("/juma/styles/sales.css");
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(App.stage.getWidth()/1.1);
        box.setId("sales");
        Label caption = new Label("New Sales");
        caption.setId("salesTitle");
        
        GridPane p = new GridPane();
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        TextField quantity = new TextField("1");
        ComboBox products= new ComboBox(Sales.fetchProducts());
        products.setValue("choose product");
        Button add = new Button("Add to cart");
        add.setGraphic(new ImageView(getClass().getResource("/juma/resources/saleswhite.png").toExternalForm()));
        add.setGraphicTextGap(5);
        
        if(Session.get() != null){
            //retrive cart sessions
            cart = (Cart)Session.get();
        }else{
            //create new cart
            cart = new Cart();
            //set a one time invoice id here;
            GenerateId.set();
            //set Session
            Session.set(cart);
        }
        add.setOnAction(e->{
            if(products.getValue() != null && !products.getValue().toString().equals("choose product")){
                if(!quantity.getText().trim().isEmpty() && quantity.getText().matches("[0-9]*")){
                    if(!this.ResolveDuplicateItems(products.getValue().toString())){
                        Query q = new Query();
                        q.setProduct(products.getValue().toString());
                        if(Integer.parseInt(quantity.getText()) <= 0){
                            quantity.setText("1");
                            q.setQuantity(1);
                        }else{
                            q.setQuantity(Integer.parseInt(quantity.getText()));
                        }
                        if(q.check(cart)){
                            q.setInvoice(GenerateId.get());
                            cart.addItem(q);
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
        p.add(new VBox(new Label("Choose Product"),products), 0, 0);
        p.add(new VBox(new Label("Quantity"),quantity), 1, 0);
        p.add(add, 2, 0);
        
        VBox bottom = new VBox();
        bottom.setSpacing(20);
        bottom.setId("bottom");
        Button check = new Button("Check Out");
        check.setOnAction(e->{
            if(cart !=null && cart.size() > 0){
                App.pane.getChildren().clear();
                Wrapper wrapper = new Wrapper();
                wrapper.setWrapper(new Checkout());
                App.pane.setCenter(wrapper);
            }else{
                new MessageBox().run("You Have Not Added Any Item To Cart");
            }
        });
        bottom.getChildren().addAll(check);
        bottom.setAlignment(Pos.CENTER_LEFT);
        table = this.getTable();
        box.getChildren().addAll(caption,p,table,bottom);
        return box;
    }
    public boolean ResolveDuplicateItems(String name){
        boolean isDuplicate = false;
        for(Query q : cart){
            if(q.getProduct().equals(name)){
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }
    private void deleteFromCart(Query item){
        cart.removeItem(item);
        //reset table;
        box.getChildren().remove(2);
        table = this.getTable();
        box.getChildren().add(2,table);
        
    }
    private Table getTable(){
        int totalItem = 0;double totalAmount=0;double totalProfit = 0;
        Table t = new Table();
        if(cart != null){
             t.setRowSize(cart.size());//row size excluding table column headers
        }else{
             t.setRowSize(1);//row size excluding table column headers
        }
       
        t.setColumns(new String[]{"#","Product Name","Generic Name",
            "Description","Price","Qauntity","Amount","Profit","Action"});
        if(cart !=null){
            for(int i = 0; i < cart.size(); i++){//according to the row size
                Query query = cart.getItem(i);
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
                delete.setOnMouseClicked(e->{this.deleteFromCart(query);});

                t.AddRow(new Node[]{new Label(String.valueOf((i+1))),new Label(query.getProduct())
                        ,new Label(query.getGeneric()),info,new Label(Sales.fetchCurrency()+query.getPrice())
                        ,new Label(String.valueOf(query.getQuantity())),new Label(Sales.fetchCurrency()+query.getAmount())
                        ,new Label(Sales.fetchCurrency()+query.getProfit()),new HBox(delete)});
            }
            Label key = new Label("Total Item");
            key.setStyle("-fx-font-weight:bolder;");
            Label value = new Label(String.valueOf(totalItem));
            value.setStyle("-fx-font-weight:bolder;");
            t.AddFooterRows(new Node[]{key,value});
            
            key = new Label("Total Amount");
            key.setStyle("-fx-font-weight:bolder;");
            value = new Label(Sales.fetchCurrency()+String.valueOf(totalAmount));
            value.setStyle("-fx-font-weight:bolder;");
            t.AddFooterRows(new Node[]{key,value});
            
            key = new Label("Total Profit");
            key.setStyle("-fx-font-weight:bolder;");
            value = new Label(Sales.fetchCurrency()+String.valueOf(totalProfit));
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
}
