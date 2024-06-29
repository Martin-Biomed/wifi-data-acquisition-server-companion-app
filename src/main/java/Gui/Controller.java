package Gui;

import customExceptions.customException;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import javafx.util.Duration;

import org.json.JSONObject;
import outgoingApiCaller.bleConnection;
import outgoingApiCaller.wifiScan;
import outgoingApiCaller.esp32WifiConnection;
import outgoingApiCaller.gpsCaller;
import gpsProcessing.gpsGeolocator;
import gpsProcessing.gpsPCLocator;

import Utils.stringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

// This file contains all the EventHandlers for the Main JavaFx Application
public class Controller {

    /**
    These fields were created and defined in the FXML file. To be able to refer to them in the code, we need to
    declare that they were created inside an FXML file.
    */

    @FXML
    private TabPane main_tab_pane;

    // Available GUI Tabs
    @FXML
    private Tab ble_conn_tab, wifi_scan_tab, wifi_conn_tab, ping_details_tab, esp32_gps_tab;

    // These are objects available in the "BLE Connection Details" Tab
    @FXML
    private TextArea device_name_text_field, device_addr_text_field, device_ble_gatt_read_text_field, device_ble_gatt_write_text_field;
    @FXML
    private Text ble_conn_confirmation_label;
    @FXML
    private Circle esp32_conn_status_circle;

    // These are objects available in the "Scanned Wi-Fi APs" Tab
    @FXML
    private Accordion wifi_scan_accordion;
    @FXML
    private Button wifi_ap_mobile_button_1, wifi_ap_mobile_button_2, wifi_ap_mobile_button_3, wifi_ap_mobile_button_4,
            wifi_regular_router_button_1, wifi_regular_router_button_2, wifi_regular_router_button_3, wifi_regular_router_button_4,
            wifi_ap_distance_button_1, wifi_ap_distance_button_2;

    // These Objects are related to showing the Internet Connection Status of the Desktop
    @FXML
    private Circle desktop_wifi_status_circle;
    @FXML
    private Text desktop_wifi_status_text;

    // These are the different buttons that are available in the GUI
    @FXML
    private Button wifi_scan_option_button, wifi_conn_execute_button, ping_host_button, wifi_router_oem_button,
            esp_gps_location_button, esp32_geoapify_button;

    // These are Objects associated with connecting the ESP32 to a Wi-Fi AP
    @FXML
    private TextField wifi_ssid_text_field, wifi_pwd_text_field;
    @FXML
    private Circle esp32_wifi_status_circle;
    @FXML
    private Text wifi_ap_status_text, esp32_ip_addr_text, esp32_subnet_mask_text;

    // These are Object associated with the GPS functionality of the ESP32 and Java App
    @FXML
    private TitledPane esp32_gprmc_data_titledpane;
    @FXML
    private SplitPane esp32_gps_splitpane;
    @FXML
    private AnchorPane offline_gps_pane, online_gps_pane, geoapify_result_anchorpane, gps_offline_scrollpane_anchorpane;
    @FXML
    private TextField esp32_geoapify_textfield;
    @FXML
    private ScrollPane gps_offline_scrollpane;
    @FXML
    private TextArea pc_coordinates_textarea;

    // Object Instances that are only ever initialised once in the lifecycle of the JAR application
    public static bleConnection applicationBleConnection;
    public static wifiScan applicationWifiScan;
    public static esp32WifiConnection applicationEsp32WifiConn;
    public static checkDesktopInternetConnectionService internetConnectionMonitorService;
    public static gpsCaller esp32GpsCaller;

    // This variable is used when determining whether we can lookup Wi-Fi AP manufacturers (only if a scan has occurred)
    private static boolean scanned_wifi_aps_available = false;
    private static String[] latest_set_of_scanned_wifi_aps;

    // This is a special function for the JavaFx Controller class that always executes at the start of the app
    public void initialize(){
        applicationBleConnection = new bleConnection();
        applicationWifiScan = new wifiScan();
        applicationEsp32WifiConn = new esp32WifiConnection();
        esp32GpsCaller = new gpsCaller();

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
        esp_gps_location_button.setDisable(true);
        esp32_geoapify_button.setDisable(true);

    }

    // We set the BLE Connection static variables in our instance of bleConnection (shared between all BLE functions)
    public void click_configure_ble_button(MouseEvent mouseEvent) {

        // We always start by disabling all dependent Buttons in case we fail to configure the ESP32 BLE GATT connection
        wifi_scan_option_button.setDisable(true);
        wifi_conn_execute_button.setDisable(true);
        ping_host_button.setDisable(true);
        esp_gps_location_button.setDisable(true);
        esp32_geoapify_button.setDisable(true);

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
                esp_gps_location_button.setDisable(false);
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

                wifiScan.print_ap_data_to_textArea(available_json_keys, json_ap, children.get(i));
            }
            // This step ensures that we switch to the correct Tab in case of a successful Wi-Fi scan
            main_tab_pane.getSelectionModel().select(wifi_scan_tab);

            // We enable the OEM Lookup Button if our scan succeeds and if we have an internet connection
            if (internetConnectionMonitorService.internet_conn == true){
                wifi_router_oem_button.setDisable(false);
                wifi_ap_mobile_button_1.setDisable(false);
                wifi_regular_router_button_1.setDisable(false);
                wifi_ap_mobile_button_2.setDisable(false);
                wifi_regular_router_button_2.setDisable(false);
                wifi_ap_mobile_button_3.setDisable(false);
                wifi_regular_router_button_3.setDisable(false);
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

                // We will automatically iterate over every available key in the JSON object
                ArrayList<String> available_json_keys = stringUtils.return_available_json_keys(json_ap);

                wifiScan.print_ap_data_to_textArea(available_json_keys, json_ap, children.get(i));
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

    public void click_esp_gps_button(MouseEvent event) throws IOException, customException {

        esp32GpsCaller = Reset.reset_esp_gps_tab(esp32GpsCaller);

        String gprmc_str = esp32GpsCaller.request_gps_gprmc_data_value(applicationBleConnection);
        JSONObject gprmc_obj = esp32GpsCaller.create_gprmc_json_obj(gprmc_str);

        System.out.println("Parsed GPS Object: " + gprmc_obj.toString());
        String formatted_gprmc_str = "Raw GPRMC Sentence: \n " + gprmc_str + "\n\n";

        // We will automatically iterate over every available key in the JSON object
        ArrayList<String> available_json_keys = stringUtils.return_available_json_keys(gprmc_obj);

        StringBuilder printable_gprmc_str = new StringBuilder();

        // We construct a printable version of each JSON key-value pair
        for (String key : available_json_keys) {
            // We print out every key-value pair as a separate "Text" object in the current TitledPane
            printable_gprmc_str.append(key).append(": ").append(gprmc_obj.get(key).toString()).append("\n");
        }

        // We add the final string as a singular "TextArea" object
        final TextArea content = new TextArea(formatted_gprmc_str + printable_gprmc_str.toString());
        content.setEditable(false);

        // To ensure that the TextArea fills the AnchorPane, you must anchor the content to all four sides of the AnchorPane
        gps_offline_scrollpane_anchorpane.getChildren().add(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);

        main_tab_pane.getSelectionModel().select(esp32_gps_tab);

        // We enable the Address Lookup button if our GPS request succeeds and if we have an internet connection
        if (internetConnectionMonitorService.internet_conn == true) {
            esp32_geoapify_button.setDisable(false);
        }
    }

    public void click_esp_geoapify_button(MouseEvent event) throws IOException {

        String longitude = esp32GpsCaller.get_longitude();
        String latitude = esp32GpsCaller.get_latitude();

        String api_key = esp32_geoapify_textfield.getText();

        String api_response = gpsGeolocator.send_geoapify_api_request(latitude, longitude, api_key);

        String address = gpsGeolocator.parse_geoapify_api_response(api_response);
        System.out.println("Coordinates point to the following address: " + address);

        // We add the final string as a singular "TextArea" object
        final TextArea content = new TextArea("Found Address: " + address);
        content.setEditable(false);

        geoapify_result_anchorpane.getChildren().add(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
    }

    public void click_pc_coordinates_button(MouseEvent event) throws MalformedURLException {

        String message;

        if (internetConnectionMonitorService.internet_conn == true){
            JSONObject pc_coordinates = gpsPCLocator.getComputerCoordinates();
            float latitude = (float) pc_coordinates.getDouble("lat");
            float longitude = (float) pc_coordinates.getDouble("lon");

            System.out.println("Retrieved the following coordinates: lat=" + latitude + ", lon=" + longitude);

            message = "The following coordinates were found based on the IP Address of your LAN: \n" +
                    "Latitude=" + latitude + ", Longitude=" + longitude;
        }
        else {
            message = "The PC is not connected to the internet";
        }

        // We add the final string as a singular "TextArea" object
        pc_coordinates_textarea.setText(message);
        pc_coordinates_textarea.setEditable(false);
    }

    public void click_wifi_mobile_1_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 0, true);
    }

    public void click_wifi_regular_router_1_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 0, false);
    }

    public void click_estimate_wifi_ap1_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 0);
    }

    public void click_wifi_mobile_2_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 1, true);
    }

    public void click_wifi_regular_router_2_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 1, false);
    }

    public void click_estimate_wifi_ap2_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 1);
    }

    public void click_wifi_mobile_3_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 2, true);
    }

    public void click_wifi_regular_router_3_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 2, false);
    }

    public void click_estimate_wifi_ap3_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 2);
    }

    public void click_wifi_mobile_4_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 3, true);
    }

    public void click_wifi_regular_router_4_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 3, false);
    }

    public void click_estimate_wifi_ap4_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 3);
    }

    public void click_wifi_mobile_5_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 4, true);
    }

    public void click_wifi_regular_router_5_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 4, false);
    }

    public void click_estimate_wifi_ap5_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 4);
    }
    public void click_wifi_mobile_6_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 5, true);
    }

    public void click_wifi_regular_router_6_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 5, false);
    }

    public void click_estimate_wifi_ap6_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 5);
    }

    public void click_wifi_mobile_7_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 6, true);
    }

    public void click_wifi_regular_router_7_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 6, false);
    }

    public void click_estimate_wifi_ap7_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 6);
    }

    public void click_wifi_mobile_8_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 7, true);
    }

    public void click_wifi_regular_router_8_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 7, false);
    }

    public void click_estimate_wifi_ap8_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 7);
    }
    public void click_wifi_mobile_9_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 8, true);
    }

    public void click_wifi_regular_router_9_button(MouseEvent event) throws customException {
        distanceCalcGuiUpdates.update_wifi_scan_titledpane_with_device(wifi_scan_accordion, applicationWifiScan,
                latest_set_of_scanned_wifi_aps, 8, false);
    }

    public void click_estimate_wifi_ap9_dist_button(MouseEvent event) throws customException, IOException {
        distanceCalcGuiUpdates new_set_of_updates = new distanceCalcGuiUpdates();
        new_set_of_updates.calculate_distances_for_selected_ap(wifi_scan_accordion, 8);
    }


}
