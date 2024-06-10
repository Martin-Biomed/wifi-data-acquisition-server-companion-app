package Gui;

import Constants.SceneDefaultValues;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import outgoingApiCaller.esp32WifiConnection;
import outgoingApiCaller.wifiScan;

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


}
