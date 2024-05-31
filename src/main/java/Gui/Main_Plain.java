package Gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main_Plain extends Application {

    public static void main(String[] args) {
        // Starts JavaFX application
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Get Java version
        String javaVersion = System.getProperty("java.version");

        // Get JavaFX version
        String javafxVersion = System.getProperty("javafx.version");

        Label label = new Label("Hello, JavaFX " + javafxVersion
                + ", running on Java " + javaVersion + ".");

        // Label -> StackPane
        StackPane root = new StackPane(label);

        // StackPane -> Scene
        Scene scene = new Scene(root, 640, 480);

        // Scene -> primaryStage
        primaryStage.setScene(scene);

        primaryStage.setTitle("Hello World JavaFX");

        primaryStage.show();

    }
}