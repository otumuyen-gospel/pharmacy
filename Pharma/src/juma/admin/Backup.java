/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.admin;

import java.io.File;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import juma.admin.QuestionBox.Option;
import juma.api.Database;

/**
 *
 * @author user1
 */
public class Backup extends Admin{
    File file = null;
    String message = "";
    public Backup(){
        super();
    }
    @Override
    protected void Middle(){
        //current link
        backup.setId("backup2");
        backup = new Label("Home / System Backup");
        backup.setId("backup3");
        HBox classLink = new HBox(backup);
        classLink.setId("link");
        VBox center = new VBox(classLink,getBackups());
        center.setSpacing(50);
        center.setAlignment(Pos.TOP_CENTER);
         this.setCenter(center);
    }
    private VBox getBackups(){
        VBox box = new VBox();
        box.getStylesheets().add("/juma/styles/backup.css");
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(App.stage.getWidth()/1.1);
        box.setId("backup");
        Label caption = new Label("View Most Recent Backup");
        caption.setId("backupTitle");
          
        HBox bottom = new HBox();
        bottom.setSpacing(20);
        bottom.setId("bottom");
        Label run = new Label("click to backup database");
        ImageView node  = new ImageView(getClass().getResource("/juma/resources/backupblack.png").toExternalForm());
        node.setFitHeight(20);
        node.setFitWidth(20);
        run.setGraphic(node);
        run.setGraphicTextGap(10);
        run.setId("attach");
        run.setOnMouseClicked(e->{
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("choose backup location");
            file = chooser.showDialog(App.stage);
            if(file != null){
                this.backup();
            }
        });
        bottom.getChildren().addAll(run,this.attachment());
        bottom.setAlignment(Pos.CENTER);
        
        box.getChildren().addAll(caption,this.getTable(),bottom);
        return box;
    }
    private void backup(){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                if(b.backupDatabase(b.getServer(), "root", "", b.default_db_name,file)){
                    file = new File(b.path);
                    String sql = "insert into backup values(0,'"+file.getName()+"','"+file.getAbsolutePath().replace("\\", "+")+"','"+
                            b.getUser()+"','"+LocalDate.now()+"')";
                    b.getDataConnection();
                    b.getStatement();
                    if(b.performBatchUpdate(new String[]{sql}).length > 0){
                        message = "database backup successful";
                    }else{
                        message = "database backup unsuccessful";
                    }
                }else{
                    message = "database backup unsuccessful";
                }
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Backup());
                    App.pane.setCenter(wrapper);
                });
                
            }catch(Exception e){
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run("error processing backup");
                });
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
            
        }).start();
    }
    public void restore(File file){
        Database b = new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            if(new Database().restoreDatabase(b.getServer(), "root", "", b.default_db_name, file)){
                message = "database restore successful";
            }else{
                message = "database restore unsuccessful ensure backup file exist";
            }
            try{
                Platform.runLater(()->{
                  progress.close();
                  new MessageBox().run(message);
                });
            }catch(Exception e){
                Platform.runLater(()->{
                  progress.close();
                  new MessageBox().run("error restoring database");
                });
            }finally{
                b.closeConnection();
            }
            
        }).start();
    }
    
    private HBox attachment(){
        HBox box = new HBox();
        box.setPadding(new Insets(20));
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER_LEFT);
        Label caption = new Label("choose a database to restore");
        ImageView node  = new ImageView(getClass().getResource("/juma/resources/attachment.png").toExternalForm());
        node.setFitHeight(20);
        node.setFitWidth(20);
        caption.setGraphic(node);
        caption.setGraphicTextGap(10);
        caption.setId("attach");
        caption.setOnMouseClicked(e->{
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Backup Files", "*.sql","*.bak"));
            chooser.setTitle("choose database file");
            file = chooser.showOpenDialog(App.stage);
            if(file != null){
                caption.setText(file.getName());
                this.restore(file);
            }else{
                caption.setText("No File Selected");
            }
        });
        box.getChildren().addAll(caption);
        return box;
        
    }
    
    public ArrayList fetchBackup(){
        ArrayList list = new ArrayList();
        Database b = new Database();
        try{
            b.getDataConnection();
            b.getStatement();
            ResultSet rs = b.getResultSet("select * from backup order by id desc limit 5");
            while(rs.next()){
                list.add(rs.getInt(1));
                list.add(rs.getString(2));
                list.add(rs.getString(3).replace("+", "\\"));
                list.add(rs.getString(4));
                list.add(rs.getString(5));
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
    public void delete(int id){
        Database b= new Database();
        ProgressBox progress = new ProgressBox();
        progress.run();
        new Thread(()->{
            try{
                String sql = "delete from backup where id="+id;
                b.getDataConnection();
                b.getStatement();
                if(file.exists()){
                    file.delete();
                }
                if(b.performBatchUpdate(new String[]{sql}).length > 0){
                    message = "backup history removed successfully";
                }else{
                    message = "unable to remove backup history";
                }
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    wrapper.setWrapper(new Backup());
                    App.pane.setCenter(wrapper);
                });
                
            }catch(Exception e){
                Platform.runLater(()->{
                     progress.close();
                     new MessageBox().run("error deleting backup history");
                });
            }finally{
                b.closeStatement();
                b.closeConnection();
            }
            
        }).start();
    }
    private Table getTable(){
        ArrayList list = this.fetchBackup();
        int rows = 0; 
        int count = 0;
        Table table = new Table();
        if(list != null && list.size() > 0){
            rows = list.size()/5; // number of fetch item from table divided by number of table column
        }
        table.setRowSize(rows);//row size excluding table column headers(this has to be set before table column headers
        table.setColumns(new String[]{"#","File Name","Saving Date","Stored By","Action"});
        for(int i = 0; i < rows; i++){ //according to the row size
            Label delete = new Label();
            delete.setGraphic(new ImageView(getClass().getResource("/juma/resources/deleteblack.png").toExternalForm()));
            delete.setGraphicTextGap(5);
            delete.setStyle("-fx-cursor:hand;");
            delete.setTooltip(new Tooltip("delete backup"));
            Label restore = new Label();
            restore.setGraphic(new ImageView(getClass().getResource("/juma/resources/attachment.png").toExternalForm()));
            restore.setGraphicTextGap(5);
            restore.setStyle("-fx-cursor:hand;");
            restore.setTooltip(new Tooltip("restore backup"));
            Label id = new Label(String.valueOf(list.get(count)));
            count++;
            Label filename = new Label(String.valueOf(list.get(count)));
            count++;
            Label path = new Label(String.valueOf(list.get(count)));
            restore.setOnMouseClicked(e->{
                file = new File(path.getText());
                this.restore(file);
            });
            delete.setOnMouseClicked(e->{
                QuestionBox q = new QuestionBox();
                Option option = q.run("about to delete this backup history ?");
                if(option == Option.OK){
                    file = new File(path.getText());
                    this.delete(Integer.parseInt(id.getText()));
                }
            });
            count++;
            Label user = new Label(String.valueOf(list.get(count)));
            count++;
            Label date = new Label(String.valueOf(list.get(count)));
            count++;
            table.AddRow(new Node[]{id,filename,date, user,new HBox(delete,restore)});
            
        }
        return table;
    }
}
