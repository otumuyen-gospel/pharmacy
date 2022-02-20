/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import juma.admin.App;
import juma.admin.MessageBox;
import juma.admin.ProgressBox;
import juma.admin.Wrapper;

/**
 *
 * @author user1
 */
public class Camera extends Stage{
    
    private class WebCamInfo {

            private String webCamName;
            private int webCamIndex;

            public String getWebCamName() {
                    return webCamName;
            }

            public void setWebCamName(String webCamName) {
                    this.webCamName = webCamName;
            }

            public int getWebCamIndex() {
                    return webCamIndex;
            }

            public void setWebCamIndex(int webCamIndex) {
                    this.webCamIndex = webCamIndex;
            }

            @Override
            public String toString() {
                    return webCamName;
            }
    }

    private final FlowPane bottomCameraControlPane;
    private final HBox topPane;
    private final BorderPane root;
    private final String cameraListPromptText = "Choose Camera";
    private final ImageView imgWebCamCapturedImage;
    private Webcam webCam = null;
    private boolean stopCamera = false;
    private BufferedImage grabbedImage;
    private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    private final BorderPane webCamPane;
    private Button btnCamreaStop;
    private Button btnCamreaStart;
    private Button btnCameraSave;
    public static File capture;
    private String message;

    public Camera() {
            this.setTitle("Connecting Camera Device Using Webcam Capture API");
            root = new BorderPane();
            topPane = new HBox();
            topPane.setSpacing(20);
            topPane.setPadding(new Insets(5));
            BorderPane box = new BorderPane();
            Label title = new Label("X");
            title.setOnMouseClicked(e->{
                this.close();if(webCam != null)webCam.close();
            });
            title.setId("close");
            BorderPane.setAlignment(title, Pos.CENTER_RIGHT);
            box.setTop(title); box.setCenter(topPane);
            root.setTop(box);
            webCamPane = new BorderPane();
            webCamPane.setStyle("-fx-background-color: #ccc;");
            imgWebCamCapturedImage = new ImageView();
            webCamPane.setCenter(imgWebCamCapturedImage);
            root.setCenter(webCamPane);
            createTopPanel();
            bottomCameraControlPane = new FlowPane();
            bottomCameraControlPane.setOrientation(Orientation.HORIZONTAL);
            bottomCameraControlPane.setAlignment(Pos.CENTER);
            bottomCameraControlPane.setHgap(20);
            bottomCameraControlPane.setVgap(10);
            bottomCameraControlPane.setPrefHeight(40);
            bottomCameraControlPane.setDisable(true);
            createCameraControls();
            root.setBottom(bottomCameraControlPane);
            root.setId("root");
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/juma/styles/camera.css");
            this.setScene(scene);
            this.initStyle(StageStyle.UNDECORATED);
            this.initOwner(App.stage);
            this.initModality(Modality.WINDOW_MODAL);
            this.setResizable(false);
            this.setScene(scene);
            this.setHeight(600);
            this.setWidth(600);
            this.centerOnScreen();
            this.show();
            Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                            setImageViewSize();
                    }
            });

    }

    protected void setImageViewSize() {

            double height = webCamPane.getHeight();
            double width = webCamPane.getWidth();

            imgWebCamCapturedImage.setFitHeight(height);
            imgWebCamCapturedImage.setFitWidth(width);
            imgWebCamCapturedImage.prefHeight(height);
            imgWebCamCapturedImage.prefWidth(width);
            imgWebCamCapturedImage.setPreserveRatio(true);

    }

    private void createTopPanel() {

            int webCamCounter = 0;
            Label lbInfoLabel = new Label("Select Your WebCam Camera");
            ObservableList<WebCamInfo> options = FXCollections.observableArrayList();

            topPane.getChildren().add(lbInfoLabel);

            for (Webcam webcam : Webcam.getWebcams()) {
                    WebCamInfo webCamInfo = new WebCamInfo();
                    webCamInfo.setWebCamIndex(webCamCounter);
                    webCamInfo.setWebCamName(webcam.getName());
                    options.add(webCamInfo);
                    webCamCounter++;
            }

            ComboBox<WebCamInfo> cameraOptions = new ComboBox<>();
            cameraOptions.setItems(options);
            cameraOptions.setPromptText(cameraListPromptText);
            cameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {

                    @Override
                    public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
                            if (arg2 != null) {
                                    System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
                                    initializeWebCam(arg2.getWebCamIndex());
                            }
                    }
            });
            topPane.getChildren().add(cameraOptions);
    }

    protected void initializeWebCam(final int webCamIndex) {

            Task<Void> webCamTask = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                            if (webCam != null) {
                                    disposeWebCamCamera();
                            }
                            webCam = Webcam.getWebcams().get(webCamIndex);
                            webCam.open();

                            startWebCamStream();

                            return null;
                    }
            };

            Thread webCamThread = new Thread(webCamTask);
            webCamThread.setDaemon(true);
            webCamThread.start();

            bottomCameraControlPane.setDisable(false);
            btnCamreaStart.setDisable(true);
    }

    protected void startWebCamStream() {

            stopCamera = false;

            Task<Void> task = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                            final AtomicReference<WritableImage> ref = new AtomicReference<>();

                            while (!stopCamera) {
                                    try {
                                            if ((grabbedImage = webCam.getImage()) != null) {

                                                    ref.set(SwingFXUtils.toFXImage(grabbedImage, ref.get()));
                                                    
                                                    grabbedImage.flush();
                                                    
                                                    Platform.runLater(new Runnable() {

                                                            @Override
                                                            public void run() {
                                                                    imageProperty.set(ref.get());
                                                            }
                                                    });
                                            }
                                    } catch (Exception e) {
                                            e.printStackTrace();
                                    }
                            }

                            return null;
                    }
            };

            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            imgWebCamCapturedImage.imageProperty().bind(imageProperty);

    }

    private void createCameraControls() {

            btnCamreaStop = new Button();
            btnCamreaStop.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent arg0) {

                            stopWebCamCamera();
                    }
            });
            btnCamreaStop.setText("Stop Camera");
            btnCamreaStart = new Button();
            btnCamreaStart.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent arg0) {
                            startWebCamCamera();
                    }
            });
            btnCamreaStart.setText("Start Camera");
      
            btnCameraSave = new Button();
            btnCameraSave.setText("Save As Profile");
            btnCameraSave.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent arg0) {
                        setCapture();
                    }
            });
            bottomCameraControlPane.getChildren().add(btnCamreaStart);
            bottomCameraControlPane.getChildren().add(btnCamreaStop);
            bottomCameraControlPane.getChildren().add(btnCameraSave);
    }

    protected void disposeWebCamCamera() {
            stopCamera = true;
            webCam.close();
            btnCamreaStart.setDisable(true);
            btnCamreaStop.setDisable(true);
    }
    private void setCapture() {
        if(grabbedImage != null){
            try {
                capture = new File("capture.png");
                ImageIO.write(grabbedImage, "PNG", capture);
                webCam.close();
                stopCamera = true;
                this.close();
                this.uploadPhoto(capture);
            } catch (IOException ex) {
                
            }
        }

    }
    private void uploadPhoto(File file){
        ProgressBox progress = new ProgressBox();
        Database b = new Database();
        progress.run();
        new Thread(()->{
            try{
                b.getDataConnection();
                b.setAutoCommit();
                if(b.createBlob(file, "update profile set photo=? where username=?", b.getUser()) > 0){
                    message ="photo uploaded successfully";
                }else{
                    message ="photo upload unsuccessful";
                }
                b.commit();
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run(message);
                    App.pane.getChildren().clear();
                    Wrapper wrapper = new Wrapper();
                    if(b.getRole().equalsIgnoreCase("admin") || b.getRole().equalsIgnoreCase("Manager")){
                         wrapper.setWrapper(new juma.admin.Profile());
                         App.pane.setCenter(wrapper);
                     }else if(b.getRole().equalsIgnoreCase("salesperson")){
                         wrapper.setWrapper(new juma.cashier.Profile());
                         App.pane.setCenter(wrapper);
                     }
                    
                });
            }catch(Exception e){
                Platform.runLater(()->{
                    progress.close();
                    new MessageBox().run("error uploading update");
                });
                b.rollBack();
            }finally{
                b.closeConnection();
                webCam.close();
            }
        }).start();
    }
    protected void startWebCamCamera() {
            stopCamera = false;
            startWebCamStream();
            btnCamreaStop.setDisable(false);
            btnCamreaStart.setDisable(true);
    }

    protected void stopWebCamCamera() {
            stopCamera = true;
            btnCamreaStart.setDisable(false);
            btnCamreaStop.setDisable(true);
    }

    
}
