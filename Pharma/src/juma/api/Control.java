/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import juma.admin.App;
import juma.admin.InputBox;
import juma.admin.MessageBox;

/**
 *
 * @author user1
 */
public class Control {
    public void run(){
        try{
            String ip = "";
            File file = new File("ip.txt");
            if(!file.exists()){
                file.createNewFile();
                InputBox input = new InputBox();
                input.set("localhost");
                input.run("Please Enter Server Ip Address Or Click Ok To Choose Localhost");
                ip = input.get();
                PrintWriter writer = new PrintWriter(file);
                writer.println(ip);
                writer.close();
            }else{
                Scanner scanner = new Scanner(file);
                while(scanner.hasNext()){
                   ip = scanner.next();
                }
                if(ip.isEmpty()){
                    InputBox input = new InputBox();
                    input.run("Please Enter Server Ip Address Or Click Ok To Choose Localhost");
                    ip = input.get();
                    PrintWriter writer = new PrintWriter(file);
                    writer.println(ip);
                    writer.close();
                }
            }
            
            Database b = new Database();
            b.setServer(ip);
            b.checkDatabase();
            
        }catch(Exception e){
            File file = new File("ip.txt");
            file.delete();
            App.stage.close();
            new MessageBox().run("oops! unknown server error occured");
        }
    }
   
}
