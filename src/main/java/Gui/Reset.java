package Gui;

import Constants.SceneDefaultValues;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import outgoingApiCaller.esp32WifiConnection;
import outgoingApiCaller.gpsCaller;
import outgoingApiCaller.wifiScan;
import outgoingApiCaller.pingHost;

/** This class can be used to access functions to reset different JavaFx Scene Objects to a known State */

public class Reset {

    public static wifiScan reset_wifi_scan_tab(wifiScan currentWifiScan, Accordion wifi_scan_accordion){

        System.out.println("Resetting the Wi-Fi Scan Tab");

        // We re-initialise the instance of the class (and all its variables)
        currentWifiScan = new wifiScan();

        // We reset the Tab TitledPanes to their default state
        // Every Pane in the "accordion" is a child that will be populated with Text
        ObservableList<TitledPane> children = wifi_scan_accordion.getPanes();

        for (TitledPane pane : children){
            pane.setText(SceneDefaultValues.default_scanned_ap_title_text);
            // Tab Panes should be blank to start off
            pane.setContent(new Text(""));
        }
        return currentWifiScan;
    }

    public static esp32WifiConnection reset_wifi_conn_tab(esp32WifiConnection currentWifiConnection, Text ip_addr_field, Text subnet_mask_field){

        System.out.println("Resetting the Wi-Fi Connection Tab");

        // We re-initialise the instance of the class (and all its variables)
        currentWifiConnection = new esp32WifiConnection();

        ip_addr_field.setText(SceneDefaultValues.default_esp32_ip_addr_text);
        subnet_mask_field.setText(SceneDefaultValues.default_esp32_subnet_mask_text);

        return currentWifiConnection;
    }

    public static gpsCaller reset_esp_gps_tab(gpsCaller currentGpsCaller, AnchorPane section_AnchorPane){

        System.out.println("Resetting the ESP32 GPS Tab");

        // We re-initialise the instance of the class (and all its variables)
        currentGpsCaller = new gpsCaller();

        // We re-initialise the contents of the AnchorPane related to the raw ESP32 GPS location
        section_AnchorPane.getChildren().clear();

        return currentGpsCaller;
    }

    public static pingHost reset_ping_tab(pingHost currentPingHostObj,
                                          Text host_ip_text, Text ttl_text, Text seq_num_text, Text time_text){

        System.out.println("Resetting the Ping Host Tab");

        host_ip_text.setText(SceneDefaultValues.default_host_ip_result_text);
        ttl_text.setText(SceneDefaultValues.default_ping_ttl_text);
        seq_num_text.setText(SceneDefaultValues.default_seq_num_text);
        time_text.setText(SceneDefaultValues.default_ping_time_text);

        currentPingHostObj = new pingHost();

        return currentPingHostObj;
    }

    public static void reset_geoapify_section(AnchorPane section_AnchorPane){

        System.out.println("Resetting the GPS Geoapify Section");

        // We re-initialise the contents of the AnchorPane related to the result of the API call parsing
        section_AnchorPane.getChildren().clear();
    }

}
