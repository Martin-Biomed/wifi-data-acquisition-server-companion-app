package outgoingApiCaller;

import Constants.Constants;

import java.io.IOException;

/*
 This class is used to execute functions that are related to the BLE connection to the ESP32.
 This class is not inherited by any other class, it serves as a standalone object that stores the latest
 values which define the BLE GATT connection.
*/
public class bleConnection {

    private static String ble_device_name;
    private static String device_read_uuid;
    private static String device_write_uuid;
    private static String macAddress;
    public static boolean connectionStatus;

    // Returns a different (int) depending on which parameter is not ready
    public static int check_mandatory_ble_parameters(){
        /*
        Note: This function does not check if the string is formatted correctly.
        The correct formatting is defined in the (BLE_GATT_Client_for_Windows.exe) application, and it will return
        error messages if the formatting is incorrect.
        */
        if (ble_device_name.isEmpty() && macAddress.isEmpty()){
            return -1;
        }
        else if (device_read_uuid.isEmpty()){
            return -2;
        }
        else if (device_write_uuid.isEmpty()){
            return -3;
        }
        else{
            return 0;
        }
    }

    // Returns the reply from the series of API calls to the (BLE_GATT_Client_for_Windows.exe) app
    // This function is called by the different Classes that need to send a message to the ESP32
    public String send_new_ble_msg_to_esp32(String message, bleConnection currentBleConnection) throws IOException {

        String response_str;

        // These Device Name and MAC are only updated if the user has provided them (as only one of the two is required)
        if (!currentBleConnection.get_ble_device_name().isEmpty()){
            response_str = outgoingApiCaller.execute_api_call(
                    currentBleConnection.get_ble_device_name(), Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

            if (!response_str.contains("Value Updated")){
                response_str = response_str + " (While Updating Device Name)";
                return response_str;
            }
        }

        if (!currentBleConnection.get_macAddress().isEmpty()){
            response_str = outgoingApiCaller.execute_api_call(
                    currentBleConnection.get_macAddress(), Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

            if (!response_str.contains("Value Updated")){
                response_str = response_str + " (While Updating Device MAC Address)";
                return response_str;
            }
        }

        response_str = outgoingApiCaller.execute_api_call(
                currentBleConnection.get_device_read_uuid(), Constants.gatt_read_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

        if (!response_str.contains("Value Updated")){
            response_str = response_str + " (While Updating Read UUID)";
            return response_str;
        }

        response_str = outgoingApiCaller.execute_api_call(
                currentBleConnection.get_device_write_uuid(), Constants.gatt_write_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

        if (!response_str.contains("Value Updated")){
            response_str = response_str + " (While Updating Write UUID)";
            return response_str;
        }

        response_str = outgoingApiCaller.execute_api_call(
                message, Constants.msg_topic, "PUT", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

        if (!response_str.contains("Value Updated")){
            response_str = response_str + " (While Updating Message to be Sent)";
            return response_str;
        }

        response_str = outgoingApiCaller.execute_api_call(
                "", Constants.send_msg_topic, "POST", Constants.BLE_Client_hostname, Constants.BLE_Client_port);

        return response_str;
    }

    public void set_ble_device_name(String input_str){
        ble_device_name = input_str;
    }

    public static String get_ble_device_name(){ return ble_device_name; }

    public void set_device_read_uuid(String input_str){
        device_read_uuid = input_str;
    }

    public static String get_device_read_uuid(){
        return device_read_uuid;
    }

    public void set_device_write_uuid(String input_str){
        device_write_uuid = input_str;
    }

    public static String get_device_write_uuid(){
        return device_write_uuid;
    }

    public void set_macAddress(String input_str){
        macAddress = input_str;
    }

    public static String get_macAddress(){
        return macAddress;
    }

}
