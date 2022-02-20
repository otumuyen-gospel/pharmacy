/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.io.File;
import java.sql.Blob;
import java.sql.ResultSet;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import juma.admin.App;
import juma.admin.MessageBox;
import juma.admin.ProgressBox;

/**
 *
 * @author user1
 */
public class Pdf {
    Cart cart;
    public Pdf(Cart cart){
        this.cart = cart;
    }
    public void create(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("choose location to save pdf");
        File file = chooser.showDialog(App.stage); 
        Database b = new Database();
        ProgressBox progress = new ProgressBox();
        if(file != null){
            if(cart !=null && cart.size() > 0){
                progress.run();
                 new Thread(()->{
                    try{
                        Generator g = new Generator(cart,file);
                        b.getDataConnection();
                        b.getStatement();
                        ResultSet rs = b.getResultSet("select name,currency,email,telephone,blue from settings");
                        if(rs.next()){
                            Blob blob  = rs.getBlob(5);
                            long length = blob.length();
                            g.header(rs.getString(1), rs.getString(2),rs.getString(3), rs.getString(4),blob.getBytes(1, (int)length));
                        }
                        g.body();
                        g.footer();
                        g.closeFile();
                        rs.close();
                        Platform.runLater(()->{
                            progress.close();
                            new MessageBox().run("Operation Successful");
                        });
                    }catch(Exception e){
                        Platform.runLater(()->{
                            progress.close();
                            new MessageBox().run("oops! an error occured");
                        });
                    }finally{
                        b.closeResultSet();
                        b.closeStatement();
                        b.closeConnection();
                        System.gc();
                    }
                 }).start();
           
            }else{
                new MessageBox().run("Sorry cart session already saved & closed get invoice from Sales Query");
            }
            
        }else{
            new MessageBox().run("No Location Choosen");
        }
        
    }
    
}