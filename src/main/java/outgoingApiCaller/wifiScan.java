package outgoingApiCaller;

import Constants.Constants;
import apiMsg.apiMsgConstructors;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import Utils.stringUtils;
import macManufacturerLookup.macManufacturerLookup;

import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import static Gui.Main.logger;

public class wifiScan {

    private static String reply_str;

    // This variable is kept static because we potentially append the OEM manufacturer if the correct option is selected
    private static String latest_formatted_string;

    // This function takes an existing instance of a BLE Connection as an input
    public int execute_wifi_scan(bleConnection currentBleConnection) throws IOException {

        // We cannot start a Wi-Fi AP scan until all required BLE connection fields have been filled with values
        int result = currentBleConnection.check_mandatory_ble_parameters();

        String response_str;

        // Create a properly formatted JSON string like the API serve expects for the "wifi_scan" command
        String message = apiMsgConstructors.create_wifi_scan_msg();

        // We only send a message if our BLE connection parameters have already been correctly set.
        // Note: Error -10 means there was an error when using the API calls
        if (result == 0){

            reply_str = currentBleConnection.send_new_ble_msg_to_esp32(message, currentBleConnection);

            // If the string is not JSON formatted, then it is likely an error message response from the BLE Client App
            if (!stringUtils.check_valid_json_str(reply_str)){
                reply_str = reply_str + " (After sending the message)";
                return -10;
            }

            // API Call has received a reply (not necessarily a success API Call)
            return 0;
        }

        else {
            return result;
        }
    }

    // This function takes in a raw JSON string and formats it in a way that produces the string to be displayed in
    // the Wi-Fi scan Tab
    public static String format_json_str(String input_str){

        JSONObject jsonObject = stringUtils.convert_string_to_json_obj(input_str);

        // We will replace the existing key-value pairs with the properly formatted key-value pairs

        if (jsonObject.keySet().contains("ssid")){
            jsonObject.put("SSID", jsonObject.get("ssid"));
            jsonObject.remove("ssid");
        }

        if (jsonObject.keySet().contains("mac")){
            jsonObject.put("MAC Address", jsonObject.get("mac"));
            jsonObject.remove("mac");
        }

        if (jsonObject.keySet().contains("rssi")){
            jsonObject.put("Received Signal Strength Indicator (RSSI)", jsonObject.get("rssi"));
            jsonObject.remove("rssi");
        }

        if (jsonObject.keySet().contains("channel")){
            jsonObject.put("Channel", jsonObject.get("channel"));
            jsonObject.remove("channel");
        }

        if (jsonObject.keySet().contains("auth_mode")){

            String auth_mode;

            // We replace the (int) that is returned from the ESP32 with a String (mapping).
            // The mapping that we are using is defined in the ESP32 app source code (wifi-data-acquisition-server)
            switch (Integer.valueOf(jsonObject.get("auth_mode").toString())) {
                case 0:
                    auth_mode = "Open Wi-Fi AP";
                    break;
                case 1:
                    auth_mode = "Wired Equivalent Privacy (WEP)";
                    break;
                case 2:
                    auth_mode = "Wi-Fi Protected Access Pre-Shared Key (WPA PSK)";
                    break;
                case 3:
                    auth_mode = "Wi-Fi Protected Access 2 Pre-Shared Key (WPA2 PSK)";
                    break;
                case 4:
                    auth_mode = "WPA/WPA2-PSK (TKIP/AES)";
                    break;
                case 5:
                    auth_mode = "Extensible Authentication Protocol (EAP) (Enterprise)";
                    break;
                case 6:
                    auth_mode = "Wi-Fi Protected Access 3 Pre-Shared Key (WPA3 PSK)";
                    break;
                case 7:
                    auth_mode = "WPA2/WPA3-PSK";
                    break;
                case 9:
                    auth_mode = "Opportunistic Wireless Encryption (OWE)";
                    break;
                case 10:
                    auth_mode = "WPA3-Enterprise 192-Bit";
                    break;
                default:
                    auth_mode = "Unknown Wi-Fi Authentication Method";
            }
            jsonObject.put("Authentication Mode", auth_mode);
            jsonObject.remove("auth_mode");
        }

        if (jsonObject.keySet().contains("group_cipher")){

            String group_cipher;

            // We replace the (int) that is returned from the ESP32 with a String (mapping).
            // The mapping that we are using is defined in the ESP32 app source code (wifi-data-acquisition-server)
            switch (Integer.valueOf(jsonObject.get("group_cipher").toString())) {
                case 0:
                    group_cipher = "No Cipher";
                    break;
                case 1:
                    group_cipher = "WEP40";
                    break;
                case 2:
                    group_cipher = "WEP104";
                    break;
                case 3:
                    group_cipher = "TKIP";
                    break;
                case 4:
                    group_cipher = "CCMP";
                    break;
                case 5:
                    group_cipher = "TKIP and CCMP";
                    break;
                case 6:
                    group_cipher = "AES-CMAC-128";
                    break;
                case 7:
                    group_cipher = "SMS4";
                    break;
                case 8:
                    group_cipher = "GCMP";
                    break;
                case 9:
                    group_cipher = "GCMP256";
                    break;
                default:
                    group_cipher = "Unknown Group Cipher";
            }
            jsonObject.put("Group Cipher", group_cipher);
            jsonObject.remove("group_cipher");
        }

        if (jsonObject.keySet().contains("pair_cipher")){

            String pair_cipher;

            // We replace the (int) that is returned from the ESP32 with a String (mapping).
            // The mapping that we are using is defined in the ESP32 app source code (wifi-data-acquisition-server)
            switch (Integer.valueOf(jsonObject.get("pair_cipher").toString())) {
                case 0:
                    pair_cipher = "No Cipher";
                    break;
                case 1:
                    pair_cipher = "WEP40";
                    break;
                case 2:
                    pair_cipher = "WEP104";
                    break;
                case 3:
                    pair_cipher = "TKIP";
                    break;
                case 4:
                    pair_cipher = "CCMP";
                    break;
                case 5:
                    pair_cipher = "TKIP and CCMP";
                    break;
                case 6:
                    pair_cipher = "AES-CMAC-128";
                    break;
                case 7:
                    pair_cipher = "SMS4";
                    break;
                case 8:
                    pair_cipher = "GCMP";
                    break;
                case 9:
                    pair_cipher = "GCMP256";
                    break;
                default:
                    pair_cipher = "Unknown Group Cipher";
            }
            jsonObject.put("Pairwise Cipher", pair_cipher);
            jsonObject.remove("pair_cipher");
        }

        latest_formatted_string = jsonObject.toString();
        return latest_formatted_string;
    }

    public String append_oem_to_json_string(String input_json_str) throws IOException {

        JSONObject jsonObject = stringUtils.convert_string_to_json_obj(input_json_str);

        // First, we attempt to find the manufacturer based on the manufacturer
        if (jsonObject.has("MAC Address")){
           String mac_address = jsonObject.get("MAC Address").toString();
           // We append the OEM as a new key-value pari in the JSON Object
           try{
               String oem_manufacturer = macManufacturerLookup.get_vendor_from_mac(mac_address);
               logger.info("Adding OEM to the JSON object: " + oem_manufacturer);

               // If the reply from the previous function is JSON-formatted, it is almost certainly an error message
               if (stringUtils.check_valid_json_str(oem_manufacturer)){
                   oem_manufacturer = "(Unable to Find Manufacturer)";
               }
               jsonObject.put("Wi-Fi AP Manufacturer", oem_manufacturer);

               // We sleep to avoid overloading the API server for MAC Address search (avoiding Error 429)
               Thread.sleep(1000);

               // We override the original value of (input_json_str) with the new JSON string that has the OEM as well
               input_json_str = jsonObject.toString();
               return input_json_str;
           }
           catch (IOException e){
               logger.error("Error Detected while trying to find OEM: " + e.toString());
               return e.toString();
           }
           catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
        }
        else{
            logger.error("JSON Object does not have key (MAC Address)");
            return "JSON Object does not have key (MAC Address)";
        }

    }

    public String append_device_type(String json_str, boolean deviceType){

        JSONObject jsonObject = stringUtils.convert_string_to_json_obj(json_str);
        String device_type_str;

        if (deviceType) {
            device_type_str = Constants.mobile_device_str;
        }
        else {
            device_type_str = Constants.regular_ap_router_str;
        }

        jsonObject.put(Constants.device_type_key, device_type_str);
        return jsonObject.toString();
    }

    public static String get_reply_str(){
        return reply_str;
    }

    public static void print_ap_data_to_textArea(ArrayList<String> available_json_keys, JSONObject json_ap, TitledPane ap_titledpane){

        StringBuilder formatted_ap_str = new StringBuilder();

        // We construct a formatted version of the JSON String
        for (String key : available_json_keys) {
            // We print out every key-value pair as a separate "Text" object in the current TitledPane
            formatted_ap_str.append(key).append(": ").append(json_ap.get(key).toString()).append("\n");
        }
        // We add the final string as a singular "Text" object
        final Text content = new Text(formatted_ap_str.toString());
        content.setTextAlignment(TextAlignment.LEFT);
        ap_titledpane.setContent(content);
    }


}
