/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import juma.admin.App;
import static juma.admin.App.pane;
import juma.admin.Login;
import juma.admin.MessageBox;
import juma.admin.ProgressBox;

/**
 *
 * @author user1
 */
public class Database {
    public String default_db_name = "pharma";
    public String path ;
    String driver = "com.mysql.jdbc.Driver";
    static String server = "localhost";
    String port = "3306";
    static String password = "";
    static String user = "root";
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    static String url = "";
    boolean isExist = false;
    static String username,pass,role;
    public Database(){
        
    }
    public String getServer(){
        return Database.server;
    }
    public void setServer(String server){
        if((server != null) || (!server.isEmpty())){
            Database.server =server;
        }
        
    }
    public String getUser(){
        return username;
    }
    public void setUser(String value){
        username = value;
    }
    public String getRole(){
        return role;
    }
    public void setRole(String value){
        role = value;
    }
    public String getPassword(){
        return pass;
    }
    public void setPassword(String value){
           pass= value;
        
    }
    public void getDataLessConnection() throws Exception{
        url = "jdbc:mysql://"+server+":"+port+"/?user="+user+"&password="+password;
        Class.forName(driver);
        connection = DriverManager.getConnection(url);
    }
    public void getDataConnection() throws SQLException, ClassNotFoundException{
        url = "jdbc:mysql://"+server+":"+port+"/"+default_db_name+"?user="+user+"&password="+password;
        Class.forName(driver);
        connection = DriverManager.getConnection(url);
    }
    public void closeConnection(){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException ex) {
                
            }
        }
    }
    
    public void getStatement() throws SQLException{
        stmt = connection.createStatement();
    }
    public void closeStatement(){
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException ex) {
                
            }
        }
    }
    public ResultSet getResultSet(String sql) throws SQLException{
        rs = stmt.executeQuery(sql);
        return rs;
    }
    public void closeResultSet(){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException ex) {
                
            }
        }
    }
    public void rollBack(){
        try{
             connection.rollback();
        }catch(SQLException e){
        }
       
    }
    public void setAutoCommit() throws SQLException{
        connection.setAutoCommit(false);//commit update later
    }
    public void commit() throws SQLException{
        connection.commit();//commit update now
    }
    public boolean performSingleUpdate(String sql) throws SQLException{
        boolean update =stmt.execute(sql);
        return update;
    }
    public  int[] performBatchUpdate(String[]sqls) throws SQLException{
        for(String sql:sqls){
            stmt.addBatch(sql);
        }
        return stmt.executeBatch();
    }
    public synchronized void checkDatabase() throws Exception{
        this.getDataLessConnection();
        this.getStatement();
        rs = connection.getMetaData().getCatalogs();
        ProgressBox progress = new ProgressBox();
        while(rs.next()){
            if(rs.getString(1).equals(this.default_db_name)){
                isExist =  true;
                App.stage.show();
                pane.setCenter(new Login());
                break;
            }
        }
        if(!isExist){
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Backup Files", "*.sql","*.bak"));
            chooser.setTitle("Choose Database File To Restore");
            File file = chooser.showOpenDialog(App.stage);
            if(file != null){
                progress.run();
                new Thread(() -> {
                    try{
                        this.performSingleUpdate("create database "+this.default_db_name);
                        isExist = this.restoreDatabase(server, user, password, this.default_db_name, 
                            file);
                    }catch(Exception e){
                        Platform.runLater(() -> {
                            progress.close();
                            new MessageBox().run("unknown server error occured");
                         });
                    }
                    Platform.runLater(() -> {
                        progress.close();
                        if(isExist){
                            App.stage.show();
                            pane.setCenter(new Login());
                        }
                     });
                    
                }).start();
                
            }else{
                new MessageBox().run("No File Choosen");
                App.stage.close();
            }
            
        }
        
    }
    public boolean backupDatabase(String host,String user,String pass,String database,File file){
        boolean status = false;
    
        try {
            path = file.getAbsolutePath()+"/db_backup_by_"+username+"_"+LocalDate.now().toString()+".sql";
            String cmd = "c:/xampp/mysql/bin/mysqldump --host=" + host + " --user=" + user + " --password=" + 
                pass + " " + database + " -r " + path;
            Process p = null;
            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(cmd);
            int execute = p.waitFor();
            if(execute == 0){
                status = true;
            }
        } catch (IOException | InterruptedException ex) {
            status = false;
        }
        return status;
        
    }
    public boolean restoreDatabase(String host,String user,String pass,String database,File bak){
        boolean status = false;
        try {
            String []cmd = new String[]{"c:/xampp/mysql/bin/mysql.exe",database,"--user="+user,"--password="+pass,
                "-e"," source " + bak.getAbsolutePath()};
            Process p = null;
            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(cmd);
            int execute = p.waitFor();
            if(execute == 0){
                status = true;
            }
        } catch (IOException | InterruptedException ex) {
            status = false;
        }
        return status;
    }
    public int createBlob(File file,String sql,Object value) throws Exception{
        Blob blob = connection.createBlob();
        int startPos = 1;
        OutputStream out = blob.setBinaryStream(startPos);
        FileInputStream in = new FileInputStream(file.getAbsolutePath());
        int b = -1;
        while((b = in.read())!= -1){
            out.write(b);
        }
         in.close();
         out.close();
         //String sql = "update profile set photo=? where username=?" (? == value);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setBlob(1, blob);
        if(value !=null){
            pstmt.setObject(2, value);
        }
        
        int i = pstmt.executeUpdate();
        pstmt.close();
        
        return i;
       
    }
    public Image fetchBlob(String sql,Object value) throws SQLException{
        //String sql = "select photo from profile where username=?";(? == value)
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setObject(1, value);
        rs = pstmt.executeQuery();
        Blob blob = null;Image image = null;
        if(rs.next()){
            blob = rs.getBlob(1);
        }
        
        if(blob !=null){
            image = new Image(blob.getBinaryStream());
        }
        pstmt.close();
        return image;
    }
    public boolean convertBlobToFile(Blob blob, File file) throws Exception{
        // Read picture data and save it to a file
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
        InputStream in = blob.getBinaryStream();
        int b = -1;
        while((b = in.read()) != -1) {
         fos.write((byte)b);
        }
        fos.close();
        return true;
    }
    
}
