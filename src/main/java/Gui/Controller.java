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
import javafx.util.Duration;
import org.json.JSONObject;
import outgoingApiCaller.outgoingApiCaller;
import outgoingApiCaller.bleConnection;
import outgoingApiCaller.wifiScan;
import outgoingApiCaller.esp32WifiConnection;

import Utils.stringUtils;
import Gui.checkDesktopInternetConnectionService;

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

    // Available GUI Tabs
    @FXML
    private Tab wifi_scan_tab, wifi_conn_tab, ping_details_tab;

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

    // These Objects are related to showing the Internet Connection Status of the Desktop
    @FXML
    private Circle desktop_wifi_status_circle;
    @FXML
    private Text desktop_wifi_status_text;

    // These are the different buttons that are available in the GUI
    @FXML
    private Button wifi_scan_option_button, wifi_conn_execute_button, ping_host_button, wifi_router_oem_button, gps_location_button;

    // These are Objects associated with connecting the ESP32 to a Wi-Fi AP
    @FXML
    private TextField wifi_ssid_text_field, wifi_pwd_text_field;
    @FXML
    private Circle esp32_wifi_status_circle;
    @FXML
    private Text wifi_ap_status_text, esp32_ip_addr_text, esp32_subnet_mask_text;

    // Object Instances that are only ever initialised once in the lifecycle of the JAR application
    public static bleConnection applicationBleConnection;
    public static wifiScan applicationWifiScan;
    public static esp32WifiConnection applicationEsp32WifiConn;
    public static checkDesktopInternetConnectionService internetConnectionMonitorService;

    // This variable is used when determining whether we can lookup Wi-Fi AP manufacturers (only if a scan has occurred)
    private static boolean scanned_wifi_aps_available = false;
    private static String[] latest_set_of_scanned_wifi_aps;

    // This is a special function for the JavaFx Controller class that always executes at the start of the app
    public void initialize(){
        applicationBleConnection = new bleConnection();
        applicationWifiScan = new wifiScan();
        applicationEsp32WifiConn = new esp32WifiConnection();

        // Start the JavaFx task for monitoring Internet Connectivity
        internetConnectionMonitorService = new checkDesktopInternetConnectionService(
                desktop_wifi_status_circle, desktop_wifi_status_text);

        internetConnectionMonitorService.setPeriod(Duration.seconds(3));
        internetConnectionMonitorService.start();

        // We disable some buttons that have to be enabled by specific conditions
        wifi_scan_option_button.setDisable(true);
        wifi_conn_execute_button.setDisable(true);
        ping_host_button.setDisable(true);
        wifi_router_oem_button.setDisable(true);
        gps_location_button.setDisable(true);

    }

    // We set the BLE Connection static variables in our instance of bleConnection (shared between all BLE functions)
    public void click_configure_ble_button(MouseEvent mouseEvent) {

        // We always start by disabling all dependent Buttons in case we fail to configure the ESP32 BLE GATT connection
        wifi_scan_option_button.setDisable(true);
        wifi_conn_execute_button.setDisable(true);
        ping_host_button.setDisable(true);
        gps_location_button.setDisable(true);

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
                // We re-enable the Buttons which are dependent on successfully setting up the BLE Connection first
                wifi_scan_option_button.setDisable(false);
                wifi_conn_execute_button.setDisable(false);
                gps_location_button.setDisable(false);
                ping_host_button.setDisable(true); // Ping cannot be used unless ESP32 is connected to a Wi-Fi AP
        }

        // This step ensures that we switch to the correct Tab everytime the button is clicked.
        main_tab_pane.getSelectionModel().select(ble_conn_tab);
    }

    public void click_scan_wifi_ap_button(MouseEvent mouseEvent) throws IOException, customException {

        applicationWifiScan = Reset.reset_wifi_scan_tab(applicationWifiScan, wifi_scan_accordion);

        int result = applicationWifiScan.execute_wifi_scan(applicationBleConnection);

        if (result != 0){
            // These error messages are displayed in the "BLE Connection Details" Tab
            esp32_conn_status_circle.setFill(Color.rgb(180, 10, 10));
            ble_conn_confirmation_label.setText("Error Detected: [" + applicationWifiScan.get_reply_str() + "]");
            System.out.println("Error Detected: [" + applicationWifiScan.get_reply_str() + "]");
            // This step ensures that we switch to the BLE Connection Tab which shows the error message from the API call
            main_tab_pane.getSelectionModel().select(ble_conn_tab);
        }
        else {
            // These error messages are displayed in the "BLE Connection Details" Tab
            esp32_conn_status_circle.setFill(Color.rgb(10, 150, 10));
            ble_conn_confirmation_label.setText("Successful ESP32 Connection");

            // This is the processing required to properly display the JSON data
            //String formatted_wifi_scan_str = wifiScan.format_json_str(applicationWifiScan.get_reply_str());
            String[] array_of_json_aps = stringUtils.split_json_1d_str(applicationWifiScan.get_reply_str());

            latest_set_of_scanned_wifi_aps = array_of_json_aps;

            // Every Pane in the "accordion" is a child that will be populated with Text
            ObservableList<TitledPane> children = wifi_scan_accordion.getPanes();

            for (int i = 0; i < array_of_json_aps.length; i++) {

                // We use the JSON properties of the string to extract the SSID of the AP [added as Text]
                if (!stringUtils.check_valid_json_str(array_of_json_aps[i])) {
                    children.get(i).setContent(new Text("Error converting String to JSON Object"));
                }
                // We convert the JSON string from the ESP32 to a more human-readable JSON string
                String formatted_wifi_scan_str = wifiScan.format_json_str(array_of_json_aps[i]);
                JSONObject json_ap = stringUtils.convert_string_to_json_obj(formatted_wifi_scan_str);
                children.get(i).setText(json_ap.get("SSID").toString());

                // We will automatically iterate over every available key in the JSON file
                ArrayList<String> available_json_keys = stringUtils.return_available_json_keys(json_ap);

                StringBuilder formatted_ap_str = new StringBuilder();

                // We construct a formatted version of the JSON String
                for (String key : available_json_keys) {
                    // We print out every key-value pair as a separate "Text" object in the current TitledPane
                    formatted_ap_str.append(key).append(": ").append(json_ap.get(key).toString()).append("\n");
                }
                // We add the final string as a singular "Text" object
                final Text content = new Text(formatted_ap_str.toString());
                content.setTextAlignment(TextAlignment.LEFT);
                children.get(i).setContent(content);
            }
            // This step ensures that we switch to the correct Tab in case of a successful Wi-Fi scan
            main_tab_pane.getSelectionModel().select(wifi_scan_tab);

            // We enable the OEM Lookup Button if our scan succeeds and if we have an internet connection
            if (internetConnectionMonitorService.internet_conn == true){
                wifi_router_oem_button.setDisable(false);
            }

            // If this has succeeded then we have Wi-Fi APs available for additional functions
            scanned_wifi_aps_available = true;
        }
    }

    public void click_wifi_ap_oem_button(MouseEvent mouseEvent) throws IOException, customException {

        // We iterate over the latest available set of scanned Wi-Fi APs (very similar to "click_scan_wifi_ap_button")
        for (int i=0; i<latest_set_of_scanned_wifi_aps.length; i++){

            // Every Pane in the "accordion" is a child that will be populated with Text
            ObservableList<TitledPane> children = wifi_scan_accordion.getPanes();

            // We individually check each String in the array to see if its proper JSON formatted
            if (stringUtils.check_valid_json_str(latest_set_of_scanned_wifi_aps[i])){

                // We create a separate string that will be modified to include an additional JSON key-value pair
                // We do not need to use any specific instance of the wifiScan class for these functions.
                // We choose to use the same instance of wifiScan as the rest of this file for consistency
                String modified_wifi_ap_str = applicationWifiScan.format_json_str(latest_set_of_scanned_wifi_aps[i]);
                modified_wifi_ap_str = applicationWifiScan.append_oem_to_json_string(modified_wifi_ap_str);
                System.out.println("JSON string has been modified to: " + modified_wifi_ap_str);

                // We use the JSON properties of the string to extract the SSID of the AP [added as Text]
                String formatted_wifi_scan_str = wifiScan.format_json_str(modified_wifi_ap_str);
                JSONObject json_ap = stringUtils.convert_string_to_json_obj(formatted_wifi_scan_str);
                children.get(i).setText(json_ap.get("SSID").toString());

                // We will automatically iterate over every available key in the JSON file
                ArrayList<String> available_json_keys = stringUtils.return_available_json_keys(json_ap);

                StringBuilder formatted_ap_str = new StringBuilder();

                // We construct a formatted version of the JSON String
                for (String key : available_json_keys) {
                    // We print out every key-value pair as a separate "Text" object in the current TitledPane
                    formatted_ap_str.append(key).append(": ").append(json_ap.get(key).toString()).append("\n");
                }
                // We add the final string as a singular "Text" object
                final Text content = new Text(formatted_ap_str.toString());
                content.setTextAlignment(TextAlignment.LEFT);
                children.get(i).setContent(content);
            }
            else {
                System.out.println("Failed to add OEM for Wi-Fi AP: " + latest_set_of_scanned_wifi_aps[i]);
            }
            // This step ensures that we switch to the correct Tab in case of a successful Wi-Fi scan
            main_tab_pane.getSelectionModel().select(wifi_scan_tab);
        }
    }

    public void click_connect_to_ap_button(MouseEvent event) throws IOException {

        applicationEsp32WifiConn = Reset.reset_wifi_conn_tab(applicationEsp32WifiConn, esp32_ip_addr_text, esp32_subnet_mask_text);

        String SSID = wifi_ssid_text_field.getText().toString();
        String PWD = wifi_pwd_text_field.getText().toString();

        int result;

        // If the user has tried to execute a Wi-Fi AP connection without entering the SSID or PWD fields, its an error
        if (SSID.isEmpty() || PWD.isEmpty()){
            esp32_wifi_status_circle.setFill(Color.rgb(180, 10, 10));
            wifi_ap_status_text.setText("Error: SSID and/or PWD have not been provided.");
            System.out.println("Error: SSID and/or PWD have not been provided.");
            main_tab_pane.getSelectionModel().select(wifi_conn_tab);
            result = -20;
        }
        else {
            result = applicationEsp32WifiConn.execute_wifi_connection(applicationBleConnection, SSID, PWD);
        }

        if (result != 0 && result != -20) {
            // These error messages are displayed in the "Wi-Fi AP Connection Details" Tab
            esp32_wifi_status_circle.setFill(Color.rgb(180, 10, 10));
            wifi_ap_status_text.setText("Error Detected: [" + applicationEsp32WifiConn.get_reply_str() + "]");
            System.out.println("Error Detected: [" + applicationEsp32WifiConn.get_reply_str() + "]");
            // Ensures that we switch to the Wi-Fi Connection Details Tab which shows the error message from the API call
            main_tab_pane.getSelectionModel().select(wifi_conn_tab);
        }
        else {
            // In case of successful Wi-Fi AP connection, we update the GUI to reflect this success
            String wifi_conn_str = applicationEsp32WifiConn.get_reply_str();

            // We update additional information fields on a successful Wi-Fi Connection
            if (stringUtils.check_valid_json_str(wifi_conn_str)){

                // We update the JavaFx objects based on the outcome from the Wi-Fi AP connection
                JSONObject json_wifi_conn = stringUtils.convert_string_to_json_obj(wifi_conn_str);

                if (json_wifi_conn.keySet().contains("esp32_ip_addr")){
                    esp32_ip_addr_text.setText(json_wifi_conn.get("esp32_ip_addr").toString());
                }

                if (json_wifi_conn.keySet().contains("esp32_netmask")){
                    esp32_subnet_mask_text.setText(json_wifi_conn.get("esp32_netmask").toString());
                }

                if (json_wifi_conn.keySet().contains("wifi_conn_success")){
                    wifi_ap_status_text.setText("ESP32 can connect to Wi-Fi AP: " + SSID + " [AP Credentials Saved]");
                    esp32_wifi_status_circle.setFill(Color.rgb(10, 150, 10));
                    ping_host_button.setDisable(false);
                }
                main_tab_pane.getSelectionModel().select(wifi_conn_tab);
            }
            else {
                esp32_wifi_status_circle.setFill(Color.rgb(180, 10, 10));
                wifi_ap_status_text.setText("Problem with parsing response from: " + SSID + "(Not JSON)");
            }
        }
    }

}
