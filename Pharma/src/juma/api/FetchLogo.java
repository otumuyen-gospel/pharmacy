/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.sql.ResultSet;
import javafx.scene.image.Image;

/**
 *
 * @author user1
 */
public class FetchLogo {
    Database d = new Database();
    public Image blue,white;public String title;
    long blen,wlen=0;
    public void get(){
        try{
            d.getDataConnection();
            d.getStatement();
            ResultSet rs = d.getResultSet("select name,blue,white from settings");
            if(rs.next()){
                title = rs.getString(1);
                blen = rs.getBlob(2).length();
                blue = new Image(rs.getBlob(2).getBinaryStream());
                wlen = rs.getBlob(3).length();
                white = new Image(rs.getBlob(3).getBinaryStream());
            }
            if(blue == null || blen <= 0){
                blue = new Image(getClass().getResource("/juma/resources/logoblue.png").toExternalForm());
            }
            if(white == null|| wlen <= 0){
                white = new Image(getClass().getResource("/juma/resources/logowhite.png").toExternalForm());
            }
            if(title == null){
                title = "Pharma System";
            }
            rs.close();
        }catch(Exception e){
        }finally{
            d.closeResultSet();
            d.closeStatement();
            d.closeConnection();
        }
    }
}
