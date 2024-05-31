package outgoingApiCaller;

import Constants.Constants;
import apiMsg.apiMsgConstructors;
import outgoingApiCaller.bleConnection;
import outgoingApiCaller.bleConnection.*;

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
            response_str = outgoingApiCaller.execute_api_call(
                    currentBleConnection.get_ble_device_name(), Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

            if (!response_str.contains("Value Updated")){
                reply_str = response_str + " (While Updating Device Name)";
                return -10;
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
                reply_str = response_str + " (While Updating Message)";
                return -10;
            }

            response_str = outgoingApiCaller.execute_api_call(
                    "", Constants.send_msg_topic, "POST", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

            reply_str = response_str;

            return 0;
        }

        else {
            return result;
        }
    }

    public static String get_reply_str(){
        return reply_str;
    }


}
