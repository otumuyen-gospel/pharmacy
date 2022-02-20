
package juma.admin;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import juma.api.Database;

public class Product extends Admin{
    boolean isShowing = false;
    GridPane pane;
    VBox center;
    String message = "";
    long curPage,totalPage;int lower,upper;
    String query1,query2;
    Table table = new Table();
    Label show;
    static TextField net,selling,profit;
    public Product(){
        super();
    }
    @Override
    protected void Middle(){
        //current link
        product.setId("product2");
        product = new Label("Home / New Products");
        product.setId("product3");
        HBox classLink = new HBox(product);
        classLink.setId("link");
         pane = this.AddProduct();
        center = new VBox(classLink,this.products());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
    }
    
    public VBox products(){
        this.resetPageVariable(null,null);
        VBox box = new VBox();
        box.getStylesheets().add("/juma/styles/products.css");
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(1000);
        box.setId("products");
        Label caption = new Label("View Products");
        caption.setId("productsTitle");
        
        Button add = new Button("Add New Product");
        HBox addBox = new HBox(add);
        addBox.setAlignment(Pos.CENTER_RIGHT);
        
        HBox search = new HBox();
        search.setId("search");
        TextField field = new TextField();
        Label find = new Label();
        find.setOnMouseClicked(e->{
            if(!field.getText().isEmpty()){
                box.getChildren().remove(3);
                this.resetPageVariable("select * from product where name like '"+field.getText()+
                        "%' or brand like'"+field.getText()+"%' or generic like'"+field.getText()+
                        "%' or supplier like'"+field.getText()+"%' or shelve like'"+field.getText()+"%' or"+
                        " info like'"+field.getText()+"%' order by id desc",
                        "select count(*) from product where name like '"+field.getText()+
                        "%' or brand like'"+field.getText()+"%' or generic like'"+field.getText()+
                        "%' or supplier like'"+field.getText()+"%' or shelve like'"+field.getText()+"%' or"+
                        " info like'"+field.getText()+"%' order by id desc");
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
               add.setText("Add New Product");
            }else{
                center.getChildren().add(1,pane);
                isShowing = true;
                add.setText("Hide Form");
            }
           
        });
        return box;
    }
    private Table getTable(){
        String currency = Product.fetchCurrency();
        Table table = new Table();
        ArrayList list = this.paginate();
        int rows = 0; 
        int count = 0;
        if(list != null && list.size() > 0){
            rows = list.size()/14; // number of fetch item from table divided by number of table column
        }
        table.setRowSize(rows);//row size excluding table column headers(this has to be set before table column headers
        
        table.setColumns(new String[]{"#","Product Name","Expiry Date",
            "Selling Price","Info","Qty","Left","Action"});
        for(int i = 0; i < rows; i++){//according to the row size
            ProductUpdate runup = new ProductUpdate();
            runup.setId(Integer.parseInt(String.valueOf(list.get(count))));
            Label id = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setProduct(String.valueOf(list.get(count)));
            Label products = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setBrand(String.valueOf(list.get(count)));
            count++;
            runup.setGeneric(String.valueOf(list.get(count)));
            count++;
            runup.setSupplier(String.valueOf(list.get(count)));
            count++;
            runup.setArrived(String.valueOf(list.get(count)));
            count++;
            runup.setExpiry(String.valueOf(list.get(count)));
            Label expiry = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setNet(String.valueOf(list.get(count)));
            count++;
            runup.setSelling(String.valueOf(list.get(count)));
            Label selling = new Label(currency+String.valueOf(list.get(count)));
            count++;
            runup.setProfit(String.valueOf(list.get(count)));
            count++;
            Label info = new Label();
            info.setTooltip(new Tooltip("drug information"));
            info.setGraphic(new ImageView(getClass().getResource("/juma/resources/info.png").toExternalForm()));
            info.setGraphicTextGap(5);
            info.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
            info.setUserData(String.valueOf(list.get(count)));
            info.setOnMouseClicked(e->{
                new MessageBox().run(String.valueOf(info.getUserData()));
            });
            runup.setInfo(String.valueOf(list.get(count)));
            count++;
            runup.setQuantity(String.valueOf(list.get(count)));
            Label quantity = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setLeft(String.valueOf(list.get(count)));
            Label left = new Label(String.valueOf(list.get(count)));
            count++;
            runup.setRack(String.valueOf(list.get(count)));
            count++;
            
            Label delete = new Label();
            delete.setTooltip(new Tooltip("delete"));
            delete.setGraphic(new ImageView(getClass().getResource("/juma/resources/deleteblack.png").toExternalForm()));
            delete.setGraphicTextGap(5);
            delete.setStyle("-fx-text-fill:red;-fx-font-weight:bolder;-fx-cursor:hand;");
            delete.setOnMouseClicked(e->{
                QuestionBox q = new QuestionBox();
                QuestionBox.Option option = q.run("about to delete this product ?");
                if(option == QuestionBox.Option.OK){
                   this.delete(runup.getId(), runup.getSupplier(), 
                    runup.getBrand(), Integer.parseInt(runup.getLeft()));
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
            
            table.AddRow(new Node[]{id,products,expiry,selling,info,quantity,left,new HBox(delete,update)});
        }
        return table;
    }
    public GridPane AddProduct(){
        GridPane p = new GridPane();
        p.setId("products");
        p.setMaxWidth(1000);
        p.getStylesheets().add("/juma/styles/products.css");
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        TextField products = new TextField();
        TextField generic = new TextField();
        ChoiceBox suppliers= new ChoiceBox(Product.fetchSuppliers());
        DatePicker arrived = new DatePicker();
        DatePicker expiry = new DatePicker();
        net = new TextField();
        selling = new TextField();
        profit = new TextField();
        profit.setEditable(false);
        selling.textProperty().addListener(Product::changed);
        net.textProperty().addListener(Product::changed);
         
        TextArea info = new TextArea();
        TextField quantity = new TextField();
        Button submit = new Button("Submit");
        Label caption = new Label("Add New Products");
        caption.setId("addTitle");
        ChoiceBox brands= new ChoiceBox(Product.fetchBrands());
        TextField rack = new TextField();
        p.add(caption, 0, 0, 2, 1);
        p.add(new VBox(new Label("Brand Name"),brands), 0, 1);
        p.add(new VBox(new Label("Product Name"),products), 1, 1);
        p.add(new VBox(new Label("Generic Name"),generic), 0, 2);
        p.add(new VBox(new Label("Supplier"),suppliers), 1, 2);
        p.add(new VBox(new Label("Date Arrived"),arrived), 0, 3);
        p.add(new VBox(new Label("Expiry Date"),expiry), 1, 3);
        p.add(new VBox(new Label("Original Price"),net), 0, 4);
        p.add(new VBox(new Label("Selling Price"),selling), 1, 4);
        p.add(new VBox(new Label("Profit"),profit), 0, 5);
        p.add(new VBox(new Label("Drug Information"),info), 1, 5);
        p.add(new VBox(new Label("Quantity"),quantity), 0, 6);
        p.add(new VBox(new Label("Shelve Or Rack No"),rack), 1, 6);
        p.add(submit, 0, 7);
        submit.setOnAction(e->{
            if(brands.getValue() != null){
                if(!products.getText().isEmpty()){
                    if(!generic.getText().isEmpty()){
                        if(suppliers.getValue() != null){
                            if(arrived.getValue() != null){
                                if(expiry.getValue() != null){
                                    if(!net.getText().isEmpty() && net.getText().trim().matches("[0-9]*")){
                                       if(!selling.getText().isEmpty() && selling.getText().trim().matches("[0-9]*")){
                                         if(!info.getText().isEmpty()){
                                             if(!quantity.getText().isEmpty() && quantity.getText().trim().matches("[0-9]*")){
                                                  if(!rack.getText().isEmpty()){
                                                      String sql = "insert into product values(0,'"+products.getText()+"','"+
                                                              brands.getValue()+"','"+generic.getText()+"','"+
                                                              suppliers.getValue()+"','"+arrived.getValue()+"','"+
                                                              expiry.getValue()+"',"+net.getText()+","+selling.getText()+","+
                                                              profit.getText()+",'"+info.getText()+"',"+quantity.getText()+","+
                                                              quantity.getText()+",'"+rack.getText()+"')";
                                                      String sql2 ="select count(*) from product where name='"
                                                              +products.getText().trim()+"'";
                                                      this.createNewProduct(sql,sql2, suppliers.getValue().toString(), 
                                                              brands.getValue().toString(), 
                                                              Integer.parseInt(quantity.getText()));
                                                  }else{
                                                      new MessageBox().run("Please enter shelve or rack no");
                                                  }
                                             }else{
                                                 new MessageBox().run("quantity must be a number");
                                             }
                                         }else{
                                             new MessageBox().run("please enter drugs description");
                                         }
                                        }else{
                                           new MessageBox().run("please enter Selling Price(must be a number)");
                                        }
                                    }else{
                                        new MessageBox().run("please enter Original Cost Price (must be a number)");
                                    }
                                }else{
                                    new MessageBox().run("please enter the correct expiry Date");
                                }
                            }else{
                                new MessageBox().run("please enter arrival Date");
                            }
                        }else{
                            new MessageBox().run("please choose supplier name");
                        }
                    }else{
                        new MessageBox().run("please enter generic name");
                    }
                }else{
                     new MessageBox().run("please enter product name");
                }
            }else{
                new MessageBox().run("please choose brand name");
            }
        });
        return p;
    }
    public static void changed(ObservableValue<? extends String> prop,String oldValue,String newValue) {
           String original = net.getText().isEmpty()?"0":net.getText();
            String price = selling.getText().isEmpty()?"0":selling.getText();
            if(!original.trim().isEmpty() && original.trim().matches("[0-9]*")){
                if(!price.trim().isEmpty() && price.trim().matches("[0-9]*")){
                    double pro = Double.parseDouble(price)-Double.parseDouble(original);
                      profit.setText(String.valueOf(pro));
                }
            }
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
    public static ObservableList<String> fetchBrands(){
        ArrayList<String> list = new ArrayList<>();
         Database b = new Database();
         ResultSet rs;
        try{
            b.getDataConnection();
            b.getStatement();
            rs = b.getResultSet("select name from brand");
            while(rs.next()){
                list.add(rs.getString(1));
            }
            rs.close();
        }catch(Exception e){
            new MessageBox().run("unable to fetch product brands");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
        }
        return FXCollections.observableList(list);
    }
    public static ObservableList<String> fetchSuppliers(){
        ArrayList<String> list = new ArrayList<>();
         Database b = new Database();
         ResultSet rs;
        try{
            b.getDataConnection();
            b.getStatement();
            rs = b.getResultSet("select name from supplier");
            while(rs.next()){
                list.add(rs.getString(1));
            }
            rs.close();
        }catch(Exception e){
            new MessageBox().run("unable to fetch suppliers");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
        }
        return FXCollections.observableList(list);
    }
    int count = 0;
    public void createNewProduct(String sql,String sql2,String supplier,String brand,int qty){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                ResultSet rs = b.getResultSet(sql2);
                count = rs.next()?rs.getInt(1):0;
                if(count <= 0){
                    if(b.performBatchUpdate(new String[]{sql,
                        "update brand set products=products+"+qty+" where name='"+brand+"'",
                        "update supplier set items=items+"+qty+" where name='"+supplier+"'"}).length > 0){
                        message ="added successfully";
                    }else{
                        message ="operation failed";
                    }
                }else{
                    message = "this product already exist";
                }
                
                b.commit();
                rs.close();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Product());
                    App.pane.setCenter(wrapper);
                });
            }catch(Exception e){
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
    public void resetPageVariable(String q1, String q2){
        if(q2 != null && !q2.isEmpty()){
            query2 = q2;
        }else{
            query2 = "select count(*) from product";
        }
        if(q1 != null && !q1.isEmpty()){
           query1 = q1;
        }else{
           query1 = "select * from product order by id desc";
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
                list.add(rs.getString("brand"));
                list.add(rs.getString("generic"));
                list.add(rs.getString("supplier"));
                list.add(rs.getString("arrival"));
                list.add(rs.getString("expiry"));
                list.add(rs.getDouble("original"));
                list.add(rs.getDouble("selling"));
                list.add(rs.getDouble("profit"));
                list.add(rs.getString("info"));
                list.add(rs.getLong("quantity"));
                list.add(rs.getLong("remaining"));
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
            new MessageBox().run("oops! an error occured");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
            System.gc();
        }
        return list;
    }
    public void delete(int id,String supplier,String brand,int remaining){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                String sql = "delete from product where id="+id;
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                 if(b.performBatchUpdate(new String[]{sql,"update brand set products=products-"+remaining+
                        " where name='"+brand+"'","update supplier set items=items-"+remaining+
                        " where name='"+supplier+"'"}).length > 0){
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
                     wrapper.setWrapper(new Product());
                     App.pane.setCenter(wrapper);
                     
                });
                
            }catch(Exception e){
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


class ProductUpdate extends Stage{
    String brand,product,generic,supplier,arrived,expiry,net,selling,profit,info,quantity,rack,left;int id;
    static TextField nets,sellings,profits;
    String message = "";int remaining = 0;
    BorderPane pane = new BorderPane();
    public ProductUpdate(){
        Scene scene = new Scene(pane);
        pane.setId("root");
        scene.getStylesheets().add("/juma/styles/products.css");
        this.setScene(scene);
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.setTitle("Update Product");
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(App.stage);
        this.setY(0);
        this.setX((App.stage.getWidth() - App.stage.getWidth()/1.04));
        this.setWidth(App.stage.getWidth()/1.1);
        this.setHeight(520);
    }
    public void run(){
        BorderPane top = new BorderPane();
        top.setId("top");
        Label close = new Label("x");Label title= new Label("Update Product");
        close.setId("close");title.setId("title");
        close.setOnMouseClicked(e->{this.close();});
        top.setLeft(close);top.setRight(title);
        pane.setTop(top);
        pane.setCenter(this.updateProduct());
        this.showAndWait();
    }
   public GridPane updateProduct(){
        GridPane p = new GridPane();
        p.setId("products");
        p.getStylesheets().add("/juma/styles/products.css");
        p.setVgap(10);
        p.setHgap(50);
        p.setAlignment(Pos.CENTER);
        ChoiceBox brands= new ChoiceBox();
        brands.setValue(this.getBrand());
        brands.getItems().addAll(Product.fetchBrands());
        TextField products = new TextField();
        products.setText(this.getProduct());
        TextField generic = new TextField();
        generic.setText(this.getGeneric());
        ChoiceBox suppliers= new ChoiceBox();
        suppliers.setValue(this.getSupplier());
        suppliers.getItems().addAll(Product.fetchSuppliers());
        DatePicker arriveds = new DatePicker();
        LocalDate date =LocalDate.parse(this.getArrived());
        arriveds.setValue(date);
        DatePicker expirys = new DatePicker();
        date =LocalDate.parse(this.getExpiry());
        expirys.setValue(date);
        nets = new TextField();
        nets.setText(this.getNet().split("[.]")[0]);
        sellings = new TextField();
        sellings.setText(this.getSelling().split("[.]")[0]);
        profits = new TextField(this.getProfit());
        profits.setEditable(false);
        profits.setText(this.getProfit());
        sellings.textProperty().addListener(ProductUpdate::changed);
        nets.textProperty().addListener(ProductUpdate::changed);
        TextArea info = new TextArea();
        info.setText(this.getInfo());
        TextField quantity = new TextField();
        TextField rack = new TextField();
        rack.setText(this.getRack());
        Button submit = new Button("Save Update");
        submit.setOnAction(e->{this.close();});
        Label caption = new Label("Update Products");
        caption.setId("addTitle");
        p.add(caption, 0, 0, 2, 1);
        p.add(new VBox(new Label("Brand Name"),brands), 0, 1);
        p.add(new VBox(new Label("Product Name"),products), 1, 1);
        p.add(new VBox(new Label("Generic Name"),generic), 2, 1);
        p.add(new VBox(new Label("Supplier"),suppliers), 0, 2);
        p.add(new VBox(new Label("Date Arrived"),arriveds), 1, 2);
        p.add(new VBox(new Label("Expiry Date"),expirys), 2, 2);
        p.add(new VBox(new Label("Original Price"),nets), 0, 3);
        p.add(new VBox(new Label("Selling Price"),sellings), 1, 3);
        p.add(new VBox(new Label("Profit"),profits), 2, 3);
        p.add(new VBox(new Label("Drug Information"),info), 0, 4);
        p.add(new VBox(new Label("Quantity"),quantity), 1, 4);
        p.add(new VBox(new Label("Shelve Or Rack No"),rack), 2, 4);
        p.add(submit, 0, 5);
        submit.setOnAction(e->{
            if(brands.getValue() != null){
                if(!products.getText().isEmpty()){
                    if(!generic.getText().isEmpty()){
                        if(suppliers.getValue() != null){
                            if(arriveds.getValue() != null){
                                if(expirys.getValue() != null){
                                    if(!nets.getText().isEmpty() && nets.getText().trim().matches("[0-9]*")){
                                       if(!sellings.getText().isEmpty() && sellings.getText().trim().matches("[0-9]*")){
                                         if(!info.getText().isEmpty()){
                                             if(!quantity.getText().isEmpty() && quantity.getText().trim().matches("[0-9]*")){
                                                  if(!rack.getText().isEmpty()){
                                                      String sql = "update product set name='"+products.getText()+"',brand='"+
                                                              brands.getValue()+"',generic='"+generic.getText()+"',supplier='"+
                                                              suppliers.getValue()+"',arrival='"+arriveds.getValue()+"',expiry='"+
                                                              expirys.getValue()+"',original="+nets.getText()+",selling="+
                                                              sellings.getText()+",profit="+profits.getText()+",info='"+
                                                              info.getText()+"',quantity=remaining+"+quantity.getText()+
                                                              ",remaining=remaining+"+quantity.getText()+",shelve='"+rack.getText()+"' "+
                                                              "where id="+this.getId();
                                                      String sql2 ="select remaining from product where name='"
                                                              +products.getText().trim()+"'";
                                                      this.close();
                                                      this.updateOldProduct(sql,sql2, suppliers.getValue().toString(), 
                                                              brands.getValue().toString(), 
                                                              Integer.parseInt(quantity.getText()));
                                                  }else{
                                                      new MessageBox().run("Please enter shelve or rack no");
                                                  }
                                             }else{
                                                 new MessageBox().run("quantity must be a number");
                                             }
                                         }else{
                                             new MessageBox().run("please enter drugs description");
                                         }
                                        }else{
                                           new MessageBox().run("please enter Selling Price(must be a number)");
                                        }
                                    }else{
                                        new MessageBox().run("please enter Original Cost Price (must be a number)");
                                    }
                                }else{
                                    new MessageBox().run("please enter the correct expiry Date");
                                }
                            }else{
                                new MessageBox().run("please enter arrival Date");
                            }
                        }else{
                            new MessageBox().run("please choose supplier name");
                        }
                    }else{
                        new MessageBox().run("please enter generic name");
                    }
                }else{
                     new MessageBox().run("please enter product name");
                }
            }else{
                new MessageBox().run("please choose brand name");
            }
        });
        return p;
    }
   public static void changed(ObservableValue<? extends String> prop,String oldValue,String newValue) {
           String original = nets.getText().isEmpty()?"0":nets.getText();
            String price = sellings.getText().isEmpty()?"0":sellings.getText();
            if(!original.trim().isEmpty() && original.trim().matches("[0-9]*")){
                if(!price.trim().isEmpty() && price.trim().matches("[0-9]*")){
                    double pro = Double.parseDouble(price)-Double.parseDouble(original);
                      profits.setText(String.valueOf(pro));
                }
            }
    }
   public void updateOldProduct(String sql,String sql2,String supplier,String brand,int qty){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                ResultSet rs = b.getResultSet(sql2);
                remaining = rs.next()?rs.getInt(1):0;
                if(b.performBatchUpdate(new String[]{sql,"update brand set products=products-"+this.getQuantity()+
                        " where name='"+this.getBrand()+"'","update brand set products=products+"+
                                (remaining+qty)+" where name='"+brand+"'",
                        "update supplier set items=items-"+this.getQuantity()+
                        " where name='"+this.getSupplier()+"'","update supplier set items=items+"+(remaining+qty)+
                        " where name='"+supplier+"'"}).length > 0){
                    message ="updated successfully";
                }else{
                    message ="update unsuccessful";
                }
                b.commit();
                rs.close();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Product());
                    App.pane.setCenter(wrapper);
                });
            }catch(Exception e){
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("oops! an error occured ");
                    b.rollBack();
                });
            }finally{
                b.closeResultSet();
                b.closeStatement();
                b.closeConnection();
            }
        }).start();
    }
    public int getId(){
       return this.id;
    }
    public void setId(int value){
         this.id = value;
    }
    public String getBrand(){
       return this.brand;
    }
    public void setBrand(String value){
         this.brand = value;
    }
    public String getProduct(){
       return this.product;
    }
    public void setProduct(String value){
         this.product = value;
    }
    public String getGeneric(){
       return this.generic;
    }
    public void setGeneric(String value){
         this.generic = value;
    }
    public String getSupplier(){
       return this.supplier;
    }
    public void setSupplier(String value){
         this.supplier = value;
    }
    public String getArrived(){
       return this.arrived;
    }
    public void setArrived(String value){
         this.arrived = value;
    }
    public String getExpiry(){
       return this.expiry;
    }
    public void setExpiry(String value){
         this.expiry = value;
    }
    public String getNet(){
       return this.net;
    }
    public void setNet(String value){
         this.net = value;
    }
    public String getSelling(){
       return this.selling;
    }
    public void setSelling(String value){
         this.selling = value;
    }
    public String getProfit(){
       return this.profit;
    }
    public void setProfit(String value){
         this.profit = value;
    }
    public String getInfo(){
       return this.info;
    }
    public void setInfo(String value){
         this.info = value;
    }
    public String getQuantity(){
       return this.quantity;
    }
    public void setQuantity(String value){
         this.quantity = value;
    }
    public String getRack(){
       return this.rack;
    }
    public void setRack(String value){
         this.rack = value;
    }
    public String getLeft(){
       return this.left;
    }
    public void setLeft(String value){
         this.left = value;
    }
}
