package outgoingApiCaller;

import Utils.stringUtils;
import apiMsg.apiMsgConstructors;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class pingHost {

    private String raw_reply_str;
    private String host_ip_address;

    private int ping_ttl;
    private int ping_seq_num;
    private int ping_elapsed_time;

    public int execute_host_ping(bleConnection currentBleConnection, String host) throws IOException {

        // We cannot start a Wi-Fi AP scan until all required BLE connection fields have been filled with values
        int result = currentBleConnection.check_mandatory_ble_parameters();

        String response_str;

        // Create a properly formatted JSON string like the API serve expects for the "ping" command
        String message = apiMsgConstructors.create_ping_msg(host);

        // We only send a message if our BLE connection parameters have already been correctly set.
        // Note: Error -10 means there was an error when using the API calls
        if (result == 0){

            raw_reply_str = currentBleConnection.send_new_ble_msg_to_esp32(message, currentBleConnection);

            // If the string is not JSON formatted, then it is likely an error message response from the BLE Client App
            if (!stringUtils.check_valid_json_str(raw_reply_str)){
                raw_reply_str = raw_reply_str + " (After sending the message)";
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
    // the "Ping Details" Tab
    public int format_json_str(String input_str) {

        System.out.println("About to Process the following string: " + input_str);
        JSONObject jsonObject = stringUtils.convert_string_to_json_obj(input_str);

        // We will replace the existing key-value pairs with the properly formatted key-value pairs

        // Return Value Meaning: (-2 = Error with Wi-Fi AP Connection), (-1 = Error with Ping), (0 = Success)

        // This key-value pair is not directly displayed on the GUI, but is used by other GUI functions to determine status
        if (jsonObject.keySet().contains("ap_creds_valid")) {
            if (!Objects.equals(jsonObject.get("ap_creds_valid").toString(), "1")) {
                return -2;
            }
        }
        // This key-value pair is not directly displayed on the GUI, but is used by other GUI functions to determine status
        if (jsonObject.keySet().contains("wifi_conn_success")) {
            if (!Objects.equals(jsonObject.get("wifi_conn_success").toString(), "1")) {
                return -2;
            }
        }
        // This key-value pair is not directly displayed on the GUI, but is used by other GUI functions to determine status
        if (jsonObject.keySet().contains("ping success")) {
            if (!Objects.equals(jsonObject.get("ping success").toString(), "1")) {
                return -1;
            }
        }

        if (jsonObject.keySet().contains("target_host_ip")) {
            String ip_address = jsonObject.get("target_host_ip").toString();
            set_ip_address(ip_address);
        }

        if (jsonObject.keySet().contains("ping_ttl")) {
            String ttl = jsonObject.get("ping_ttl").toString();
            set_ping_ttl(Integer.valueOf(ttl));
        }

        if (jsonObject.keySet().contains("ping_seq_num")) {
            String seq_num = jsonObject.get("ping_seq_num").toString();
            set_ping_seq_num(Integer.valueOf(seq_num));
        }

        if (jsonObject.keySet().contains("ping_elapsed_time_ms")) {
            String elapsed_time = jsonObject.get("ping_elapsed_time_ms").toString();
            set_ping_elapsed_time(Integer.valueOf(elapsed_time));
        }
        return 0;
    }

    public void set_raw_reply_str(String input_str){
        raw_reply_str = input_str;
    }

    public String get_raw_reply_str(){
        return raw_reply_str;
    }

    public void set_ip_address(String address){
        host_ip_address = address;
    }

    public String get_ip_address(){
        return host_ip_address;
    }

    public void set_ping_ttl(int ttl){
        ping_ttl = ttl;
    }

    public int get_ping_ttl(){
        return ping_ttl;
    }

    public void set_ping_seq_num(int seq_num){
        ping_seq_num = seq_num;
    }

    public int get_ping_seq_num(){
        return ping_seq_num;
    }

    public void set_ping_elapsed_time(int elapsed_time){
        ping_elapsed_time = elapsed_time;
    }

    public int get_ping_elapsed_time(){
        return ping_elapsed_time;
    }



}
