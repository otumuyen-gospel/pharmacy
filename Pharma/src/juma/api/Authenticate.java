/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.sql.ResultSet;
import java.time.LocalDate;
import juma.admin.Admin;
import juma.admin.App;
import juma.admin.MessageBox;
import juma.admin.Wrapper;
import juma.cashier.Cashier;

/**
 *
 * @author user1
 */
public class Authenticate {
    public boolean run(String user,String password){
        boolean run = false;
        Database b = new Database();
        if(!user.isEmpty()){
            if(!password.isEmpty() && password.length() == 8){
                try{
                    b.getDataConnection();
                    b.setAutoCommit();
                    b.getStatement();
                    /*
                      check if any user has been created before, if none create first user and
                      assign admin role to user, if yes then attempt to log user in using the entered
                      credentials
                    */
                    ResultSet rs = b.getResultSet("select * from profile");
                    if(rs.next()){
                        //try to log user in 
                        this.login(rs, user, password,b);
                        
                    }else{
                        //create first user and make him the admin and log user in
                        String sql = "insert into profile values(0,'"+user+"','"+password+"','','','Admin','','"+
                                LocalDate.now().toString()+"',0,'')";
                         b.performSingleUpdate(sql);
                        this.login(rs, user, password, b);
                    }
                    b.commit();
                }catch(Exception e){
                    new MessageBox().run("unknown Server Error");
                    b.rollBack();
                }finally{
                    b.closeResultSet();
                    b.closeStatement();
                    b.closeConnection();
                }
                
            }else{
                new MessageBox().run("password must be 8 characters long no more no less");
            }
        }else{
            new MessageBox().run("please enter username");
        }
        return true;
    }
    public void login(ResultSet rs,String user, String password,Database b) throws Exception{
        rs = b.getResultSet("select role from profile where username='"+user+"' and password='"+password+"'");
        if(rs.next()){
            b.setUser(user);
            b.setPassword(password);
            b.setRole(rs.getString(1));
            if(rs.getString(1).equalsIgnoreCase("Admin") || rs.getString(1).equalsIgnoreCase("Manager")){
                App.pane.getChildren().clear();
                Wrapper wrapper = new Wrapper();
                wrapper.setWrapper(new Admin());
                App.pane.setCenter(wrapper);
                
                
            }else if(rs.getString(1).equalsIgnoreCase("SalesPerson")){
                App.pane.getChildren().clear();
                Wrapper wrapper = new Wrapper();
                wrapper.setWrapper(new Cashier());
                App.pane.setCenter(wrapper);
            }
            
        }else{
            new MessageBox().run("sorry you don't have access");
        }
        
    }
    
}
