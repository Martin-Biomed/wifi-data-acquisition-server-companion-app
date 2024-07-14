package Gui;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import static Gui.Main.logger;

// This is a parallel JavaFx Task that executes in parallel with the main GUI Task
// It periodically checks if this JAR Application has access to the internet

public class checkDesktopInternetConnectionService extends ScheduledService<Void> {

    // This func requires a JavaFx circle shape to be passed as an input parameter to its constructor
    // The Fill of this shape can then be updated based on this parallel thread to the main JavaFx Scene
    private static Circle connected_circle;

    private static Text connected_text;

    public static boolean internet_conn;

    public checkDesktopInternetConnectionService(Circle connected_circle, Text connected_text){
        this.connected_circle = connected_circle;
        this.connected_text = connected_text;
    }

    // The service will execute one repeated task
    @Override
    protected Task<Void> createTask(){

        // This task checks if the PC is connected to the internet, and then updates connected JavaFx shapes in the GUI
        // to reflect the connection status.
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try{
                    boolean wifi_conn_status = checking_internet_connectivity();
                    update_javafx_shapes(wifi_conn_status);
                }
                catch (Exception e){
                    logger.error(e.getMessage());
                }
                return null;
            }
        };
    }

    // Our entire check for internet connectivity relies on being able to connect to (google.com)
    public static boolean checking_internet_connectivity() throws Exception{

        Process process = java.lang.Runtime.getRuntime().exec("ping www.google.com");
        int x = process.waitFor();
        if (x == 0) {
            System.out.println("Connected to Internet (able to ping google.com)");
            internet_conn = true;
            return true;
        }
        else {
            logger.error("Internet Not Connected (unable to ping google.com)");
            internet_conn = false;
            return false;
        }
    }

    public static void update_javafx_shapes(boolean result){
        if (result){
            connected_text.setText("Connected to Internet (able to ping google.com)");
            connected_circle.setFill(Color.rgb(10, 150, 10));
        }
        else {
            connected_text.setText("Internet Not Connected (unable to ping google.com)");
            connected_circle.setFill(Color.rgb(180, 10, 10));;
        }
    }

}
