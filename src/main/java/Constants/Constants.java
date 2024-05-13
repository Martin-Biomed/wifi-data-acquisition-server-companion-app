package Constants;

public class Constants {

    /** Base URL for MAC OEM Lookup API. The API from www.macvendors.com is used for this project. */
    public static final String MAC_OEM_Lookup_baseURL = "https://api.macvendors.com/";

    /** Base URL for the (BLE_GATT_Client_for_Windows.exe) application that runs the BLE Client API server. */
    public static final String BLE_Client_baseURL = "http://localhost:5900";

    /** Topics are appended at the end of the Base URL (depending on the value being updated through the API) */
    public static final String device_name_topic = "/device/name/";

}
