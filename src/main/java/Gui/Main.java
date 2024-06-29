package Gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {

    private static ArrayList<Stage> childStages = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainAppScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setScene(scene);

        // Add a close request handler to close all child stages when the parent stage closes
        // Each Child Stage has to be explicitly added to the ArrayList.
        stage.setOnCloseRequest(event -> {
            if (!getChildStages().isEmpty()){
                for (Stage childStage : getChildStages()){
                    childStage.close();
                }
            }
        });
        stage.show();
    }

    // We have to add each Child Stage to the array once its created (so we can close them all at once)
    public static void addChildStage(Stage new_stage){
        childStages.add(new_stage);
    }

    public static ArrayList<Stage> getChildStages(){
        return childStages;
    }

    public static void main(String[] args){
        launch();
    }
}
