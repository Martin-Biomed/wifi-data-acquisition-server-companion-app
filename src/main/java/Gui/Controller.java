package Gui;

import customExceptions.customException;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import Constants.Constants;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import javafx.scene.text.TextAlignment;
import org.json.JSONObject;
import outgoingApiCaller.outgoingApiCaller;
import outgoingApiCaller.bleConnection;
import outgoingApiCaller.bleConnection.*;
import outgoingApiCaller.wifiScan;
import Utils.stringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

// This file contains all the EventHandlers for the JavaFx Application
public class Controller {

    /*
    These fields were created and defined in the FXML file. To be able to refer to them in the code, we need to
    declare that they were created inside an FXML file.
    */

    @FXML
    private TabPane main_tab_pane;

    // These are objects available in the "BLE Connection Details" Tab
    @FXML
    private TextArea device_name_text_field, device_addr_text_field, device_ble_gatt_read_text_field, device_ble_gatt_write_text_field;
    @FXML
    private Text ble_conn_confirmation_label;
    @FXML
    private Tab ble_conn_tab;
    @FXML
    private Circle esp32_conn_status_circle;

    // These are objects available in the "Scanned Wi-Fi APs" Tab
    @FXML
    private Accordion wifi_scan_accordion;
    @FXML
    private Tab wifi_scan_tab;

    // We only ever use one instance of a bleConnection
    public static bleConnection applicationBleConnection;

    public static wifiScan applicationWifiScan;

    // This is a special function for the JavaFx Controller class that always executes at the start of the app
    public void initialize(){
        applicationBleConnection = new bleConnection();
        applicationWifiScan = new wifiScan();
    }

    // We set the BLE Connection static variables in our instance of bleConnection (shared between all BLE functions)
    public void click_configure_ble_button(MouseEvent mouseEvent) {

        // We only send the value if not empty to avoid Null Pointer Exception for a JavaFx Text Field
        applicationBleConnection.set_ble_device_name(device_name_text_field.getText());
        System.out.println("Adding Device Name: " + device_name_text_field.getText());

        applicationBleConnection.set_macAddress(device_addr_text_field.getText());
        System.out.println("Adding Device Address: " + device_addr_text_field.getText());

        applicationBleConnection.set_device_read_uuid(device_ble_gatt_read_text_field.getText());
        System.out.println("Adding Device GATT Read UUID: " + device_ble_gatt_read_text_field.getText());

        applicationBleConnection.set_device_write_uuid(device_ble_gatt_write_text_field.getText());
        System.out.println("Adding Device GATT Write UUID: " + device_ble_gatt_write_text_field.getText());

        int valid_status = applicationBleConnection.check_mandatory_ble_parameters();
        System.out.println("Valid Status Value: " + valid_status);

        switch(valid_status){
            case -1:
                System.out.println("BLE Device Name or MAC Address not provided.");
                ble_conn_confirmation_label.setText("BLE Device Name or MAC Address not provided.");
                break;
            case -2:
                System.out.println("BLE Device GATT Read UUID not provided.");
                ble_conn_confirmation_label.setText("BLE Device GATT Read UUID not provided.");
                break;
            case -3:
                System.out.println("BLE Device GATT Write UUID not provided.");
                ble_conn_confirmation_label.setText("BLE Device GATT Write UUID not provided.");
                break;
            default:
                System.out.println("BLE Connection Parameters Successfully Updated.");
                ble_conn_confirmation_label.setText("BLE Connection Parameters Successfully Updated.");
        }

        // This step ensures that we switch to the correct Tab everytime the button is clicked.
        main_tab_pane.getSelectionModel().select(ble_conn_tab);
    }

    public void click_scan_wifi_ap_button(MouseEvent mouseEvent) throws IOException, customException {

        int result = applicationWifiScan.execute_wifi_scan(applicationBleConnection);

        if (result != 0){
            // These error messages are displayed in the "BLE Connection Details" Tab
            esp32_conn_status_circle.setFill(Color.rgb(150, 10, 10));
            ble_conn_confirmation_label.setText("Error Detected: [" + applicationWifiScan.get_reply_str() + "]");
            System.out.println("Error Detected: [" + applicationWifiScan.get_reply_str() + "]");
            // This step ensures that we switch to the BLE Connection Tab which shows the error message from the API call
            main_tab_pane.getSelectionModel().select(ble_conn_tab);
        }
        else {
            // These error messages are displayed in the "BLE Connection Details" Tab
            esp32_conn_status_circle.setFill(Color.rgb(10, 120, 10));
            ble_conn_confirmation_label.setText("Successful ESP32 Connection");

            // This is the processing required to properly display the JSON data
            //String formatted_wifi_scan_str = wifiScan.format_json_str(applicationWifiScan.get_reply_str());
            String[] array_of_json_aps = stringUtils.split_json_1d_str(applicationWifiScan.get_reply_str());

            ObservableList<TitledPane> children = wifi_scan_accordion.getPanes();

            for (int i = 0; i<array_of_json_aps.length; i++){

                // We use the JSON properties of the string to extract the SSID of the AP [added as Text]
                if (!stringUtils.check_valid_json_str(array_of_json_aps[i])){
                    children.get(i).setContent(new Text("Error converting String to JSON Object"));
                }
                String formatted_wifi_scan_str = wifiScan.format_json_str(array_of_json_aps[i]);
                JSONObject json_ap = stringUtils.convert_string_to_json_obj(formatted_wifi_scan_str);
                children.get(i).setText(json_ap.get("SSID").toString());

                // We will automatically iterate over every available key in the JSON file
                ArrayList<String> available_json_keys =  stringUtils.return_available_json_keys(json_ap);

                StringBuilder formatted_ap_str = new StringBuilder();
                //final Text content = new Text();

                // We construct a formatted version of the JSON String
                for (String key : available_json_keys){
                    // We print out every key-value pair as a separate "Text" object in the current TitledPane
                    //children.get(i).setContent(new Text(key + ": " + json_ap.get(key).toString()));
                    formatted_ap_str.append(key).append(": ").append(json_ap.get(key).toString()).append("\n");
                }
                // We add the final string as a singular "Text" object
                final Text content = new Text(formatted_ap_str.toString());
                content.setTextAlignment(TextAlignment.LEFT);
                children.get(i).setContent(content);
            }
            // This step ensures that we switch to the correct Tab in case of a successful Wi-Fi scan
            main_tab_pane.getSelectionModel().select(wifi_scan_tab);
        }


    }

}
