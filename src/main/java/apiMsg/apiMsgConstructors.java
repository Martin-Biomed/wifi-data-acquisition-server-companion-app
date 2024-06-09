package apiMsg;

import org.json.JSONObject;

// This class is used to compose JSON-formatted strings to send to the (BLE_GATT_Client_for_Windows.exe) API server
public class apiMsgConstructors {

    public static String create_wifi_scan_msg(){
        JSONObject obj = new JSONObject();
        obj.put("cmd","wifi_scan");
        return obj.toString();
    }

    public static String create_wifi_connect_msg(String ssid_str, String pwd_str){
        JSONObject obj = new JSONObject();
        obj.put("cmd","wifi_conn");
        obj.put("wifi_ssid", ssid_str);
        obj.put("wifi_pwd", pwd_str);
        return obj.toString();
    }

    public static String create_ping_msg(String host){
        JSONObject obj = new JSONObject();
        obj.put("cmd","ping");
        obj.put("host", host);
        return obj.toString();
    }

}
