package com.chalwk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/main.fxml");
        if (fxmlLocation == null) {
            throw new RuntimeException("FXML file not found: /fxml/main.fxml. Make sure it exists in resources/fxml/");
        }

        Parent root = FXMLLoader.load(fxmlLocation);

        primaryStage.setTitle("Map Cycle Builder v1.4.1");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}