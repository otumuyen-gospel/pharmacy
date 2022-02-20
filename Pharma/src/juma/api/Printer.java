/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import juma.admin.App;
import juma.admin.ListBox;
import juma.admin.MessageBox;
import juma.admin.ProgressBox;

/**
 *
 * @author user1
 */
public class Printer {
    Cart cart;
    public Printer(Cart cart){
        this.cart = cart;
    }
    public void run() throws Exception{
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("choose location to save print out");
        File file = chooser.showDialog(App.stage); 
        Database b = new Database();
        ProgressBox progress = new ProgressBox();
        if(file != null){
            if(cart !=null && cart.size() > 0){
                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
                PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
                patts.add(Sides.DUPLEX);
                PrintService [] ps = PrintServiceLookup.lookupPrintServices(flavor, patts);
                if(ps.length == 0){
                    new MessageBox().run("No Printer Found");
                }else{
                    // select your printer,show selectBox
                    ListBox list = new ListBox();
                    ArrayList<String>names = new ArrayList();
                    for (PrintService p : ps) {
                        names.add(p.getName());
                    }
                    list.run("choose a printer from the list", names);
                    String choice = list.get();
                    if(choice == null){
                        new MessageBox().run("No Printer Selected");
                        return;
                    }
                    PrintService myService = null;
                    for(PrintService p:ps){
                        if(p.getName().equals(choice)){
                            myService = p;
                            break;
                        }
                    }

                    if(myService == null){
                        new MessageBox().run("Printer Not Found");
                        return;
                    }
                    progress.run();
                    DocPrintJob job = myService.createPrintJob();
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
                            FileInputStream pstr = new FileInputStream(g.getFile());
                            Doc doc = new SimpleDoc(pstr,DocFlavor.INPUT_STREAM.PDF,null);
                            job.print(doc, new HashPrintRequestAttributeSet());
                            pstr.close();
                            Platform.runLater(()->{
                                progress.close();
                                new MessageBox().run("Operation Successful");
                            });
                        }catch(Exception e){
                            Platform.runLater(()->{
                                progress.close();
                                new MessageBox().run("oops! an error occured, ensure printer can print pdf");
                            });
                        }finally{
                            b.closeResultSet();
                            b.closeStatement();
                            b.closeConnection();
                            System.gc();
                        }
                     }).start();
                    
                }
               
            }else{
                new MessageBox().run("Sorry cart session already saved & closed print invoice from Sales Query");
            }
            
        }else{
            new MessageBox().run("No Location Choosen");
        }
    }
    private void print(File file) throws Exception{
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
        PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
        patts.add(Sides.DUPLEX);
        PrintService [] ps = PrintServiceLookup.lookupPrintServices(flavor, patts);
        if(ps.length == 0){
            new MessageBox().run("No Printer Found");
        }else{
            // select your printer,show selectBox
            ListBox list = new ListBox();
            ArrayList<String>names = new ArrayList();
            for (PrintService p : ps) {
                names.add(p.getName());
            }
            list.run("choose a printer from the list", names);
            String choice = list.get();
            PrintService myService = null;
            for(PrintService p:ps){
                if(p.getName().equals(choice)){
                    myService = p;
                    break;
                }
            }
            
            if(myService == null){
                new MessageBox().run("Printer Not Found");
                return;
            }
            FileInputStream pstr = new FileInputStream(file.getAbsolutePath());
            Doc doc = new SimpleDoc(pstr,DocFlavor.INPUT_STREAM.AUTOSENSE,null);
            DocPrintJob job = myService.createPrintJob();
            job.print(doc, new HashPrintRequestAttributeSet());
            pstr.close();
        }
    }
    
}
