/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.application.Platform;
import juma.admin.InputBox;
import juma.admin.MessageBox;
import juma.admin.ProgressBox;

/**
 *
 * @author user1
 */
public class Save {
   String  message = "";
    public Save(){
        InputBox input = new InputBox();
        input.run("Please enter customer's name");
        String cust = input.get().isEmpty()?"Anonymous":input.get();
        this.run(cust);
    }
    public Save(String cust,Cart oldCart,Cart newCart){
        this.update(cust,oldCart,newCart);
    }
    private void update(String cust,Cart oldCart,Cart newCart){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                if(newCart != null && newCart.size() > 0){
                    double amount = 0;
                    double profit = 0;
                    ArrayList<String>queryList = new ArrayList<>();
                    String sql;
                    for(int i = 0;i < newCart.size(); i++){
                        amount+=newCart.get(i).getAmount();
                        profit += newCart.get(i).getProfit();
                        sql = "insert into sales values(0,'"+newCart.get(i).getInvoice()+"','"+newCart.get(i).getProduct()
                                +"','"+newCart.get(i).getGeneric()+"','"+newCart.get(i).getBrand()+"','"+newCart.get(i).getDescription()+"',"+
                                newCart.get(i).getPrice()+","+newCart.get(i).getQuantity()+","+newCart.get(i).getAmount()+","+
                                newCart.get(i).getProfit()+")";
                        queryList.add(sql);
                        sql = "update product set remaining=remaining-"+newCart.get(i).getQuantity()+" where name='"+
                            newCart.get(i).getProduct()+"' and expiry > '"+LocalDate.now().toString()+"'";
                        queryList.add(sql);
                        sql = "update brand set products=products-"+newCart.get(i).getQuantity()+" where name='"+
                                newCart.get(i).getBrand()+"'";
                        queryList.add(sql);
                    }
                    sql = "delete from sales where invoice='"+newCart.get(0).getInvoice()+"'";
                    queryList.add(0,sql);
                    sql = "update invoice set amount="+amount+",profit="+profit+",customer='"+cust+
                            "' where invoice='"+newCart.get(0).getInvoice()+"'";
                    queryList.add(sql);
                    //this.updateNewProducts(newCart, b,queryList);
                    for(int i = 0;i < oldCart.size(); i++){
                        sql = "update brand set products=products+"+oldCart.get(i).getQuantity()+" where name='"+
                                oldCart.get(i).getBrand()+"'";
                        queryList.add(sql);
                        //error; resolve this statement; doesn't execute
                        sql = "update product set remaining=remaining+"+oldCart.get(i).getQuantity()+" where name='"+
                            oldCart.get(i).getProduct()+"' and expiry > '"+LocalDate.now().toString()+"' limit 1";
                        queryList.add(sql);
                    }
                    if(b.performBatchUpdate(queryList.toArray(new String[queryList.size()])).length > 0){
                        message ="saved successfully";
                        Session.destroy();
                        GenerateId.reset();
                    }else{
                        message ="save operation unsuccessful";
                    }
                }
                
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
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
    private void run(String cust){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                b.getStatement();
                Cart cart = (Cart)Session.get();
                if(cart != null && cart.size() > 0){
                    double amount = 0;
                    double profit = 0;
                    ArrayList<String>queryList = new ArrayList<>();
                    String sql;
                    for(int i = 0;i < cart.size(); i++){
                        amount+=cart.get(i).getAmount();
                        profit += cart.get(i).getProfit();
                        sql = "insert into sales values(0,'"+cart.get(i).getInvoice()+"','"+cart.get(i).getProduct()
                                +"','"+cart.get(i).getGeneric()+"','"+cart.get(i).getBrand()+"','"+cart.get(i).getDescription()+"',"+
                                cart.get(i).getPrice()+","+cart.get(i).getQuantity()+","+cart.get(i).getAmount()+","+
                                cart.get(i).getProfit()+")";
                        queryList.add(sql);
                        sql = "update brand set products=products-"+cart.get(i).getQuantity()+" where name='"+
                                cart.get(i).getBrand()+"'";
                        queryList.add(sql);
                    }
                    sql = "update profile set sales=sales+1 where username='"+b.getUser()+"'";
                    queryList.add(sql);
                    sql = "insert into invoice values(0,'"+GenerateId.get()+"',"+amount+","+profit+
                            ",'"+cust+"','"+b.getUser()+"','"+LocalDate.now().toString()+"')";
                    queryList.add(0, sql);
                    this.updateNewProducts(cart, b,queryList);
                    if(b.performBatchUpdate(queryList.toArray(new String[queryList.size()])).length > 0){
                        message ="saved successfully";
                        Session.destroy();
                        GenerateId.reset();
                    }else{
                        message ="save operation unsuccessful";
                    }
                }
                
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
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
    private void updateNewProducts(Cart cart,Database b,ArrayList<String> list) throws Exception{
        int remaining;//number of product record for a specific product
        String curProd;//current product working on
        int qty,id;
        for(int i = 0;i < cart.size();i++){
            curProd = cart.get(i).getProduct();
            qty = cart.get(i).getQuantity();
            ResultSet rs = b.getResultSet("select id,remaining from product where name='"+
                    curProd+"' and expiry >'"+LocalDate.now().toString()+"'");
            while(rs.next()){
                id = rs.getInt(1);
                remaining = rs.getInt(2);
                if(remaining >= qty){
                    remaining -=qty;
                    qty = 0;
                }else{
                    qty -=remaining;
                    remaining = 0;
                }
                list.add("update product set remaining="+remaining+" where id="+id);
                if(qty == 0){
                    break;
                }
            }
            rs.close();
            
        }
        
    }
    
}
