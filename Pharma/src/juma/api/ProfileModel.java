/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.sql.Blob;
import java.sql.ResultSet;
import juma.admin.MessageBox;

/**
 *
 * @author user1
 */
public class ProfileModel {
    String username,password,fullname,email,role,contact,joined;long sales;Blob photo;
    public void setUserName(String value){
        this.username = value;
    }
    public String getUserName(){
        return this.username;
    }
    public void setPassword(String value){
        this.password = value;
    }
    public String getPassword(){
        return this.password;
    }
    public void setFullName(String value){
        this.fullname = value;
    }
    public String getFullName(){
        return this.fullname;
    }
    public void setEmail(String value){
        this.email = value;
    }
    public String getEmail(){
        return this.email;
    }
    public void setRole(String value){
        this.role = value;
    }
    public String getRole(){
        return this.role;
    }
    public void setContact(String value){
        this.contact = value;
    }
    public String getContact(){
        return this.contact;
    }
    public void setJoined(String value){
        this.joined = value;
    }
    public String getJoined(){
        return this.joined;
    }
    public void setSales(long value){
        this.sales = value;
    }
    public long getSales(){
        return this.sales;
    }
    public void setPhoto(Blob value){
        this.photo = value;
    }
    public Blob getPhoto(){
        return this.photo;
    }
    public static ProfileModel fetch(){
        ProfileModel profile = new ProfileModel();
        Database b = new Database();
        String user = b.getUser(); String pass=b.getPassword();
        try{
            b.getDataConnection();
            b.getStatement();
            ResultSet rs = b.getResultSet("select * from profile where username='"+user+"' and password='"+pass+"'");
            if(rs.next()){
                profile.setUserName(rs.getString("username"));
                profile.setFullName(rs.getString("fullname"));
                profile.setPassword(rs.getString("password")); 
                profile.setEmail(rs.getString("email"));
                profile.setRole(rs.getString("role"));
                profile.setContact(rs.getString("contact"));
                profile.setJoined(rs.getString("joined"));
                profile.setSales(rs.getLong("sales"));
                profile.setPhoto(rs.getBlob("photo"));
            }
            
        }catch(Exception e){
        }finally{
            b.closeResultSet();
            b.closeStatement();
            b.closeConnection();
        }
        
        return profile;
    }
}
