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
import javafx.scene.control.Label;

/**
 *
 * @author user1
 */
public class Notification {
    private static int stock = 0;
    private static int expire = 0;
    private int check = 0;
    int sum = 0;
    Sound s = new Sound();
    public void outOfStock(Label label){
        Database b= new Database();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.getStatement();
                 for(String product:fetchProducts()){
                       ResultSet  rs = b.getResultSet("select sum(remaining) from product where name='"+
                                product+"' and expiry >'"+LocalDate.now().toString()+"'");
                        while(rs.next()){
                            sum=rs.getInt(1);
                        }
                        //found one out of stock product
                        if(sum <= 0 ){
                           check++; sum = 0;
                        }
                    }
                 
                 Platform.runLater(()->{
                     if(check > stock){
                            stock = check;
                            s.play();
                     }else if(check < stock){
                         stock = check;
                     }
                     if(stock > 0){
                        label.setText(String.valueOf(stock));
                        if(!label.isVisible()){
                            label.setVisible(true);
                        }
                    }else{
                        label.setVisible(false);
                    }
                 });
                
            }catch(Exception e){ 
                e.printStackTrace();
            }finally{
                b.closeResultSet();
                b.closeStatement();
                b.closeConnection();
                System.gc();
            }
        }).start();
        
    }
    public void expiry(Label label){
        Database b= new Database();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.getStatement();
                ResultSet  rs = b.getResultSet("select count(*) from product where expiry <='"
                        +LocalDate.now().toString()+"'");
                if(rs.next()){
                    check = rs.getInt(1);
                }
                Platform.runLater(()->{
                     if(check > expire){
                            expire = check;
                            s.play();
                     }else if(check < expire){
                         expire = check;
                     }
                     if(expire > 0){
                        label.setText(String.valueOf(expire));
                        if(!label.isVisible()){
                            label.setVisible(true);
                        }
                    }else{
                        label.setVisible(false);
                    }
                 });
                
            }catch(Exception e){ 
                e.printStackTrace();
            }finally{
                b.closeResultSet();
                b.closeStatement();
                b.closeConnection();
                System.gc();
            }
        }).start();
    }
    private static ArrayList<String> fetchProducts(){
        ArrayList<String> list = new ArrayList<>();
         Database b = new Database();
            try{
                b.getDataConnection();
                b.getStatement();
                ResultSet rs = b.getResultSet("select distinct name from product where expiry >'"+
                        LocalDate.now().toString()+"'");
                while(rs.next()){
                    list.add(rs.getString(1));
                }
                rs.close();
            }catch(Exception e){
                
            }finally{
                b.closeResultSet();
                b.closeStatement();
                b.closeConnection();
            }
        return list;
    }
    
}
