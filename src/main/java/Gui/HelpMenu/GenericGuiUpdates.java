package Gui.HelpMenu;

import Gui.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GenericGuiUpdates {

    public static void load_new_generic_scene(String fxml_file) throws IOException {

        // Load the new JavaFX Scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml_file));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(false);

            // We define this Stage as a Child Stage for the Main Application
            Main.addChildStage(stage);

            stage.show();
        }
        catch (IOException e) {
            System.out.println("Exception while loading a Generic Scene: " + e.getMessage());
            throw e;
        }

    }

}
