package com.emotion.player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emotion/player/camera.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Emotion-Based Music Player");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        
        CameraController controller = loader.getController();
        primaryStage.setOnHidden(e -> controller.setClosed());
        
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        nu.pattern.OpenCV.loadLocally();
        launch(args);
    }
}