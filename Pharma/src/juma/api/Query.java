/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.sql.ResultSet;
import java.time.LocalDate;
import juma.admin.MessageBox;

/**
 *
 * @author user1
 */
public class Query {
    int quantity;String invoice,product,brand,generic,description;double price,amount,profit;
    boolean checked = false;
    public void setQuantity(int value){
        this.quantity = value;
    }
    public int getQuantity(){
        return this.quantity;
    }
    public boolean check(Cart oldSale){
        //query database(product table) and set other items here
        Database b = new Database();
        try{
            b.getDataConnection();
            b.getStatement();
            ResultSet rs = b.getResultSet("select sum(remaining),brand,generic,info,selling,profit"
                    + " from product where name='"+this.getProduct()+"' and expiry > '"
                    +LocalDate.now().toString()+"'");
            if(rs.next()){
                int oldQty = 0;
                //resolves old Sales and new Sales quantity  where they are identical products
                for(Query q : oldSale){
                    if(q.getProduct().equals(this.getProduct())){
                        //get old sales quantity
                        oldQty = q.getQuantity();
                        break;
                    }
                }
                if((rs.getInt(1)+ oldQty) >= this.getQuantity()){
                    brand = rs.getString(2);
                    generic = rs.getString(3);
                    description = rs.getString(4);
                    //selling price here
                    price = rs.getDouble(5);
                    //(selling price - net price)*quantity
                    profit = rs.getDouble(6)* quantity;
                    //price * quantity
                    amount = price * quantity;
                    checked = true;
                }
            }

            rs.close();

        }catch(Exception e){
            new MessageBox().run("error adding specified product to cart. please try again");
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
            System.gc();
        }
        return checked;
    }
    
    public void setProduct(String value){
        this.product = value;
    }
    public String getProduct(){
        return this.product;
    }
    public String getGeneric(){
        return this.generic;
    }
    public void setGeneric(String value){
        this.generic = value;
    }
    public String getBrand(){
        return this.brand;
    }
    public void setBrand(String value){
        this.brand = value;
    }
    public void setInvoice(String value){
        this.invoice = value;
    }
    public String getInvoice(){
        return this.invoice;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String value){
        this.description = value;
    }
    public double getAmount(){
        return this.amount;
    }
    public void setAmount(double value){
        this.amount = value;
    }
    public double getPrice(){
        return this.price;
    }
    public void setPrice(double value){
        this.price = value;
    }
    public double getProfit(){
        return this.profit;
    }
    public void setProfit(double value){
        this.profit = value;
    }
}
