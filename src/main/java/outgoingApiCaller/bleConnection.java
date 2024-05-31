package outgoingApiCaller;

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

    public void set_ble_device_name(String input_str){
        ble_device_name = input_str;
    }

    public static String get_ble_device_name(){
        return ble_device_name;
    }

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
