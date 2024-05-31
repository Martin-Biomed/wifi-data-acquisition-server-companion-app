// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

import Constants.Constants;
import customExceptions.customException;
import macManufacturerLookup.*;
import Utils.stringUtils;

import org.json.JSONObject;
import outgoingApiCaller.*;
import apiMsg.*;

import java.io.IOException;

public class manualCmds {
    public static void main(String[] args) throws IOException, customException {

        //String macAddress = "A4:91:B1:CE:C4:59";
        //String macAddress = "FA:8F:CA:70:F1:5C";
        //String macAddress = "C8:3A:35:27:88:23";
        String macAddress = "D8:47:32:31:9B:34";
        String mac_vendor = macManufacturerLookup.get_vendor_from_mac(macAddress);
        System.out.println("Vendor for MAC Address " + macAddress + " is " + mac_vendor);

        String ble_device_name = "ESP32-BLE-Server";
        String device_read_uuid = "0000fef4-0000-1000-8000-00805f9b34fb";
        String device_write_uuid = "0000dead-0000-1000-8000-00805f9b34fb";
        String message = apiMsgConstructors.create_wifi_scan_msg();

        String response_str;
        //String ble_device_name = "";
        response_str = outgoingApiCaller.execute_api_call(
                ble_device_name, Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

        response_str = outgoingApiCaller.execute_api_call(
                device_read_uuid, Constants.gatt_read_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

        response_str = outgoingApiCaller.execute_api_call(
                device_write_uuid, Constants.gatt_write_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

        response_str = outgoingApiCaller.execute_api_call(
                message, Constants.msg_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

        response_str = outgoingApiCaller.execute_api_call(
                "", Constants.send_msg_topic, "POST", Constants.BLE_Client_hostname, Constants.BLE_Client_port);


        System.out.println("Received Response Str: " + response_str);

        boolean valid_json = stringUtils.check_valid_json_str(response_str);
        System.out.println("Is the String JSON Formatted? " + valid_json);

        JSONObject[] wifi_aps = apiMsgParser.parse_wifi_scan_response(response_str);

        //JSONObject jsonObject = stringUtils.convert_string_to_json_obj(response_str);
        //ArrayList<String> list_of_keys = stringUtils.return_available_json_keys(jsonObject);

    }
}