package com.handen.lab.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String APP_TITLE = "Ivan Pletinski variant 19";

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("primary.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 300, 275);

        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}