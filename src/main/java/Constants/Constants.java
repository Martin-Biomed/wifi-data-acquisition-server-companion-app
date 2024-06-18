package Constants;

public class Constants {

    /** Base URL for MAC OEM Lookup API. The API from www.macvendors.com is used for this project. */
    public static final String MAC_OEM_Lookup_baseURL = "https://api.macvendors.com/";

    /** Base URL for the (BLE_GATT_Client_for_Windows.exe) application that runs the BLE Client API server. */
    public static final String BLE_Client_hostname = "localhost";
    public static final Integer BLE_Client_port = 5900;

    /** These topics cover all supported options from the (BLE_GATT_Client_for_Windows.exe) application. */
    /** Topics are appended at the end of the Base URL (depending on the value being updated through the API) */
    public static final String device_name_topic = "/device/name/";
    public static final String device_addr_topic = "/device/address/";
    public static final String gatt_read_topic = "/gatt/read/";
    public static final String gatt_write_topic = "/gatt/write/";
    public static final String msg_topic = "/msg/";
    public static final String send_msg_topic = "/send_msg";
    public static final String result_topic = "/result_str";

    /** Base URL for the Geoapify API for extracting data from GPRMC sentences */
    public static final String geoapify_reverse_geocoding_url = "https://api.geoapify.com/v1/geocode/reverse";

}
