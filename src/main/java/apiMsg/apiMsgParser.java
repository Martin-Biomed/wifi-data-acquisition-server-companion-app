package apiMsg;

import Utils.stringUtils;
import customExceptions.customException;
import org.json.JSONObject;

import static Gui.Main.logger;

/** This Class was used for initial Wi-Fi Scan Tests prior to GUI Integration, it is no longer relevant for the
 * finished application but kept due to existing Unit Tests and in case of usefulness. */

public class apiMsgParser {

    // This function returns a JSON Object array [1 Object per Wi-Fi AP] based off the response from a "wifi_scan".
    public static JSONObject[] parse_wifi_scan_response(String response_str) throws customException {

        String[] split_string;

        split_string = stringUtils.split_json_1d_str(response_str);
        int arrayLength = split_string.length;

        // An array length of 0 means that the JSON string has triggered an Exception in the "split_json_str" function
        // Since we can't stop the code execution on the function we called, we will return an empty object
        if (arrayLength == 0){
            throw new customException("An Exception occurred while Splitting the JSON string");
        }

        JSONObject[] json_obj_arr = new JSONObject[arrayLength];
        logger.info("There are " + arrayLength + " Wi-Fi APs detected.");

        logger.info("The array has the following JSON Objects:");
        for (int i = 0; i < arrayLength; i++) {
            JSONObject jsonObject = stringUtils.convert_string_to_json_obj(split_string[i]);
            //System.out.println(split_string[i]);
            //System.out.println("Extracting the available keys for this JSON object... ");
            //ArrayList<String> jsonObject_keys = stringUtils.return_available_json_keys(jsonObject);
            json_obj_arr[i] = jsonObject;
            logger.info("Adding new item to JSON Object Array: " + json_obj_arr[i]);
        }
        return json_obj_arr;
    }

    // The reply to a Wi-Fi connection should always be a single 1D JSON String, parsing is just converting to a JSON Object
    public static JSONObject parse_wifi_connect_response(String input_str){
        JSONObject jsonObject = stringUtils.convert_string_to_json_obj(input_str);
        return jsonObject;
    }

    // The reply to a ping should always be a single 1D JSON String, parsing is just converting to a JSON Object
    public static JSONObject parse_ping_response(String input_str){
        JSONObject jsonObject = stringUtils.convert_string_to_json_obj(input_str);
        return jsonObject;
    }

}
