package outgoingApiCaller;

import Utils.stringUtils;
import apiMsg.apiMsgConstructors;
import org.json.JSONObject;

import java.io.IOException;

public class esp32WifiConnection {

    private static String reply_str;

    public static int execute_wifi_connection(bleConnection currentBleConnection, String SSID, String PWD) throws IOException {

        // We cannot start a Wi-Fi AP scan until all required BLE connection fields have been filled with values
        int result = currentBleConnection.check_mandatory_ble_parameters();

        String response_str;

        String message = apiMsgConstructors.create_wifi_connect_msg(SSID, PWD);

        // We only send a message if our BLE connection parameters have already been correctly set.
        // Note: Error -10 means there was an error when using the API calls
        if (result == 0){

            // Create a properly formatted JSON string like the API serve expects for the "wifi_conn" command
            reply_str = currentBleConnection.send_new_ble_msg_to_esp32(message, currentBleConnection);

            // If the string is not JSON formatted, then it is likely an error message response from the App
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

    public String get_reply_str(){
        return reply_str;
    }


}
