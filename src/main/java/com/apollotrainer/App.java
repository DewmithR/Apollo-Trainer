package com.apollotrainer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML for the initial Login screen
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = fxmlLoader.load();

        // 1. Create the scene with the root node
        Scene scene = new Scene(root); // Initialize without specific size

        stage.setTitle("Apollo Trainer - Login");
        stage.setScene(scene);

        // 2. Set the stage to maximize the window on startup
        stage.setMaximized(true);

        // OR: If you want true fullscreen (hiding taskbar/menu), use:
        // stage.setFullScreen(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}