package Gui;

import Utils.stringUtils;
import customExceptions.customException;
import distanceCalcs.distanceCalculator;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;
import outgoingApiCaller.wifiScan;
import Constants.Constants;
import customExceptions.customException;

import java.io.IOException;
import java.util.ArrayList;

public class distanceCalcGuiUpdates {

    private static double latest_low_att_distance;
    private static double latest_medium_att_distance;
    private static double latest_high_att_distance;
    private static String latest_ssid;

    public static void set_latest_low_att_distance(double distance){
        latest_low_att_distance = distance;
    }

    public static void set_latest_medium_att_distance(double distance){
        latest_medium_att_distance = distance;
    }

    public static void set_latest_high_att_distance(double distance){
        latest_high_att_distance = distance;
    }
    public static void set_latest_ssid(String ssid){ latest_ssid = ssid; }

    public static double get_latest_low_att_distance(){ return latest_low_att_distance; }
    public static double get_latest_medium_att_distance(){ return latest_medium_att_distance; }
    public static double get_latest_high_att_distance(){ return latest_high_att_distance; }
    public static String get_latest_ssid(){ return latest_ssid; }


    /** This function is used explicitly to update a specific JavaFx TitledPane with the Device Type when the user
     * presses a specific button (Either the Mobile Device or Regular Router Button for this AP) */
    public static void update_wifi_scan_titledpane_with_device(Accordion input_accordion, wifiScan inputWifiScan,
                                                        String[] scanned_wifi_aps, int ap_num, boolean type) throws customException {

        // Every Pane in the "accordion" is a child that will be populated with Text
        ObservableList<TitledPane> children = input_accordion.getPanes();

        String og_wifi_ap_str = inputWifiScan.format_json_str(scanned_wifi_aps[ap_num]);
        String modified_wifi_ap_str = inputWifiScan.append_device_type(og_wifi_ap_str, type);

        // We will use a JSON Object when updating the GUI (for convenience)
        JSONObject json_ap = stringUtils.convert_string_to_json_obj(modified_wifi_ap_str);
        ArrayList<String> available_json_keys = stringUtils.return_available_json_keys(json_ap);

        // We will automatically iterate over every available key in the JSON object
        wifiScan.print_ap_data_to_textArea(available_json_keys, json_ap, children.get(ap_num));
    }

    public boolean extract_device_type_from_titledpane(Accordion input_accordion, int ap_num) throws customException {

        try {
            // Every Pane in the "accordion" is a child that will be populated with Text
            ObservableList<TitledPane> children = input_accordion.getPanes();

            // We only need to refer to a single TitledPane at a time
            TitledPane current_titled_pane = children.get(ap_num);
            Text titled_pane_text = (Text) current_titled_pane.getContent();
            String titled_pane_str = titled_pane_text.getText();
            //System.out.println("Extracted the following String from TitledPane: " + titled_pane_str);

            // It is easier to use key-value parsing when working directly with the JSON Object
            JSONObject json_ap = stringUtils.convert_multiline_string_to_json_obj(titled_pane_str);

            String value;
            if (json_ap.has(Constants.device_type_key)) {
                value = json_ap.get(Constants.device_type_key).toString();
                System.out.println("Retrieved the Device Type for AP " + (ap_num + 1) + ": " + value);
            } else {
                System.out.println("AP " + (ap_num + 1) + " was not explicitly configured with a Device Type, assuming Regular Router.");
                value = Constants.regular_ap_router_str;
            }

            // Other functions expect the Device Type (Mobile or Regular Router) to be determined by a true/false value
            if (value.matches(Constants.mobile_device_str)) {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception e){
            System.out.println("Exception while extracting Device Type for AP " + (ap_num + 1) + ": " + e.getMessage());
            throw new customException("Exception while extracting Device Type for AP " + (ap_num + 1) + ": " + e.getMessage());
        }
    }

    public int extract_rssi_from_titledpane(Accordion input_accordion, int ap_num) throws customException {

        // Every Pane in the "accordion" is a child that will be populated with Text
        ObservableList<TitledPane> children = input_accordion.getPanes();

        // We only need to refer to a single TitledPane at a time
        TitledPane current_titled_pane = children.get(ap_num);
        Text titled_pane_text = (Text) current_titled_pane.getContent();
        String titled_pane_str = titled_pane_text.getText();
        //System.out.println("Extracted the following String from TitledPane: " + titled_pane_str);

        // It is easier to use key-value parsing when working directly with the JSON Object
        JSONObject json_ap = stringUtils.convert_multiline_string_to_json_obj(titled_pane_str);
        int value;

        if (json_ap.has("Received Signal Strength Indicator (RSSI)")){
            value = Integer.parseInt(json_ap.get("Received Signal Strength Indicator (RSSI)").toString());
            System.out.println("Retrieved the RSSI for AP " + (ap_num+1) + ": " + value);
        }
        else {
            System.out.println("AP " + ap_num + " does not have a value for RSSI");
            throw new customException("AP " + (ap_num+1) + " does not have a value for RSSI");
        }
        return value;
    }

    public int extract_channel_from_titledpane(Accordion input_accordion, int ap_num) throws customException {

        // Every Pane in the "accordion" is a child that will be populated with Text
        ObservableList<TitledPane> children = input_accordion.getPanes();

        // We only need to refer to a single TitledPane at a time
        TitledPane current_titled_pane = children.get(ap_num);
        Text titled_pane_text = (Text) current_titled_pane.getContent();
        String titled_pane_str = titled_pane_text.getText();
        //System.out.println("Extracted the following String from TitledPane: " + titled_pane_str);

        // It is easier to use key-value parsing when working directly with the JSON Object
        JSONObject json_ap = stringUtils.convert_multiline_string_to_json_obj(titled_pane_str);
        int value;

        if (json_ap.has("Channel")){
            value = Integer.parseInt(json_ap.get("Channel").toString());
            System.out.println("Retrieved the Wi-Fi 2.4GHz Channel for AP " + (ap_num+1) + ": " + value);
        }
        else {
            System.out.println("AP " + ap_num + " does not have a value for Channel");
            throw new customException("AP " + (ap_num+1) + " does not have a value for Channel");
        }
        return value;
    }

    public String extract_SSID_from_titledpane(Accordion input_accordion, int ap_num) throws customException {

        // Every Pane in the "accordion" is a child that will be populated with Text
        ObservableList<TitledPane> children = input_accordion.getPanes();

        // We only need to refer to a single TitledPane at a time
        TitledPane current_titled_pane = children.get(ap_num);
        // The SSID is the Text that is immediately accessible (not in a TextArea) for each TitledPane
        String titled_pane_title = current_titled_pane.getText();

        System.out.println("Extracted the following String from TitledPane: " + titled_pane_title);

        if (titled_pane_title.isEmpty()){
            return "[Unknown AP SSID]";
        }

        return titled_pane_title;
    }

    /** This function calculates three different distances based on provided data for a Wi-Fi AP in the list */
    public void calculate_distances_for_selected_ap(Accordion input_accordion, int ap_num) throws customException, IOException {

        boolean device_type = extract_device_type_from_titledpane(input_accordion, ap_num);
        int rssi = extract_rssi_from_titledpane(input_accordion, ap_num);
        int channel = extract_channel_from_titledpane(input_accordion, ap_num);

        double low_att_distance = distanceCalculator.estimateDistanceFromWifiAP(channel, device_type, rssi, Constants.light_attenuation_db);
        double medium_att_distance = distanceCalculator.estimateDistanceFromWifiAP(channel, device_type, rssi, Constants.medium_attenuation_db);
        double high_att_distance = distanceCalculator.estimateDistanceFromWifiAP(channel, device_type, rssi, Constants.high_attenuation_db);

        set_latest_low_att_distance(low_att_distance);
        set_latest_medium_att_distance(medium_att_distance);
        set_latest_high_att_distance(high_att_distance);

        System.out.println("Set the Distances for this AP: " + get_latest_low_att_distance() + ", " + get_latest_medium_att_distance());

        String ssid = extract_SSID_from_titledpane(input_accordion, ap_num);
        set_latest_ssid(ssid);

        // Load the new JavaFX Scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("distanceCalculatorPopUp.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(false);

            // We define this Stage as a Child Stage for the Main Application
            Main.addChildStage(stage);

            stage.show();
        }
        catch (IOException e) {
            System.out.println("Exception while loading AP Distance Calculator Scene: " + e.getMessage());
            throw e;
        }

    }

}
