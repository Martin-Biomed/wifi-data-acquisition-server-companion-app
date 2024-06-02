package outgoingApiCaller;

import Constants.Constants;
import apiMsg.apiMsgConstructors;
import outgoingApiCaller.bleConnection;
import outgoingApiCaller.bleConnection.*;
import Utils.stringUtils;

import org.json.JSONObject;
import java.io.IOException;

public class wifiScan {

    private static String reply_str;

    // This function takes an existing instance of a BLE Connection as an input
    public static int execute_wifi_scan(bleConnection currentBleConnection) throws IOException {

        int result = currentBleConnection.check_mandatory_ble_parameters();

        String response_str;

        String message = apiMsgConstructors.create_wifi_scan_msg();

        // We only send a message if our BLE connection parameters have already been correctly set.
        // Note: Error -10 means there was an error when using the API calls
        if (result == 0){

            // These Device Name and MAC are only updated if the user has provided them (as only one of the two is required)
            if (!currentBleConnection.get_ble_device_name().isEmpty()){
                response_str = outgoingApiCaller.execute_api_call(
                        currentBleConnection.get_ble_device_name(), Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

                if (!response_str.contains("Value Updated")){
                    reply_str = response_str + " (While Updating Device Name)";
                    return -10;
                }
            }

            if(!currentBleConnection.get_macAddress().isEmpty()){
                response_str = outgoingApiCaller.execute_api_call(
                        currentBleConnection.get_macAddress(), Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

                if (!response_str.contains("Value Updated")){
                    reply_str = response_str + " (While Updating Device MAC Address)";
                    return -10;
                }
            }


            response_str = outgoingApiCaller.execute_api_call(
                    currentBleConnection.get_device_read_uuid(), Constants.gatt_read_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

            if (!response_str.contains("Value Updated")){
                reply_str = response_str + " (While Updating Read UUID)";
                return -10;
            }

            response_str = outgoingApiCaller.execute_api_call(
                    currentBleConnection.get_device_write_uuid(), Constants.gatt_write_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

            if (!response_str.contains("Value Updated")){
                reply_str = response_str + " (While Updating Write UUID)";
                return -10;
            }

            response_str = outgoingApiCaller.execute_api_call(
                    message, Constants.msg_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

            if (!response_str.contains("Value Updated")){
                reply_str = response_str + " (While Updating Message to be Sent)";
                return -10;
            }

            response_str = outgoingApiCaller.execute_api_call(
                    "", Constants.send_msg_topic, "POST", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

            // If the string is not JSON formatted, then it is likely an error message response from the App
            if (!stringUtils.check_valid_json_str(response_str)){
                reply_str = response_str + " (After sending the message)";
                return -10;
            }

            reply_str = response_str;

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

        return jsonObject.toString();

    }


    public static String get_reply_str(){
        return reply_str;
    }


}
