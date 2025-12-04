package com.example.dms_project_final_phase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * December 3rd, 2025
 * MainApp.java
 * This application is the JavaFX entry point for the Runescape Item DMS.
 * It loads the FXML layout, Data Access Object design, and various services
 * for functionality, all to be injected into AppController.
 */

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("app.fxml"));
        Parent root = loader.load();

        AppController controller = loader.getController();

        // Set up database & services
        DatabaseManager dbManager = new DatabaseManager();
        ItemDao dao = new ItemDao(dbManager);
        BackpackService backpackService = new BackpackService(dao, 30.0); // 30kg cap

        controller.setDbManager(dbManager);
        controller.setItemDao(dao);
        controller.setBackpackService(backpackService);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApp.class.getResource("styles.css").toExternalForm());

        stage.setTitle("Runescape Item Database – Phase 4 (SQLite)");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
