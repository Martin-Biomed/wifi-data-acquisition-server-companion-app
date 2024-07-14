package outgoingApiCaller;

import Utils.stringUtils;
import apiMsg.apiMsgConstructors;
import gpsProcessing.gpsParser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Gui.Main.logger;

public class gpsCaller {

    private static String reply_str;

    private static String longitude;

    private static String latitude;

    // We send the API call and receive the raw JSON-formatted string from the ESP32
    public int execute_gps_location_request(bleConnection currentBleConnection) throws IOException {

        // We cannot start a BLE API call until all required BLE connection fields have been filled with values
        int result = currentBleConnection.check_mandatory_ble_parameters();

        // Create a properly formatted JSON string like the API serve expects for the "gps_location" command
        String message = apiMsgConstructors.create_gps_msg();

        // We only send a message if our BLE connection parameters have already been correctly set.
        // Note: Error -10 means there was an error when using the API calls
        if (result == 0){

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

    // The raw JSON string is expected to be provided as a single JSON key-value pair from the ESP32, we only care about the value
    public String extract_data_from_raw_json_str(String input_str){

        System.out.println("Attempting to convert JSON-formatted String to a JSON Object.");
        JSONObject jsonObject = stringUtils.convert_string_to_json_obj(input_str);

        String key_of_interest = "gps_position";
        logger.info("Attempting to extract the value of key: " + key_of_interest);
        // We extract the raw "gps_position" data that we can then parse into useful GPS formatted data
        if (jsonObject.keySet().contains(key_of_interest)){
            //System.out.println("Returning string: " + jsonObject.get("gps_position").toString());
            return jsonObject.get(key_of_interest).toString();
        }
        else{
            return "Failed to find JSON key (gps_position) in returned message.";
        }
    }

    // We choose to extract a line of GPRMC data from the raw GPS data value
    public String extract_gprmc_data_from_msg(String message) {

        // We only want the substrings containing a $GPRMC-formatted GPS message from inside the bigger string
        String regex = "\\$GPRMC.*";

        String return_str = "No Lines Matching the following Regex pattern were found: " + regex;

        try {
            // The MULTILINE option is important because the ESP32 ends every line of GPS data with /r/n
            // A single message usually carries several lines of separate GPS data.
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(message);

            logger.info("Attempting to find matching substrings in String: \n" + message);

            // Print out all the substrings that were found (that fit the regex)
            // Note: Only the final String matching the pattern is returned from this function
            while (matcher.find()) {
                return_str = matcher.group().toString();
                logger.info("Found matching line: " + return_str);
            }
            return return_str;
        }
        catch (IllegalStateException e){
            return "Caught Error while Extracting GPRMC data: " + e.toString();
        }
    }

    public String request_gps_gprmc_data_value(bleConnection currentBleConnection) throws IOException {

        // First, we send the API call and receive the raw JSON-formatted string from the ESP32
        int result = execute_gps_location_request(currentBleConnection);

        // The reply is saved as a static variable in the instance of the "gpsLocator" object
        String reply = get_reply_str();

        // The raw JSON string is expected to be provided as a single JSON key-value pair from the ESP32, we only care about the value
        String unformatted_gps_data = extract_data_from_raw_json_str(reply);

        // We choose to extract a line of GPRMC data from the raw GPS data value
        String gprmc_str = extract_gprmc_data_from_msg(unformatted_gps_data);
        logger.info("Received GPRMC datapoint: " + gprmc_str);

        return gprmc_str;
    }

    public String get_reply_str(){
        return reply_str;
    }

    public String get_latitude() { return latitude; }

    public String get_longitude() { return longitude; }

    public void set_longitude(String new_longitude) { longitude = new_longitude; }

    public void set_latitude(String new_latitude) { latitude = new_latitude; }

    // We create a JSON Object from the String because it is easier to parse in other functions
    public JSONObject create_gprmc_json_obj(String gprmc_str){

        String[] split_gprmc = gprmc_str.split(",");

        JSONObject gprmc_obj = new JSONObject();

        // The following JSON key-value mapping is based on the Sentence Structure of a GPRMC message
        gprmc_obj.put("Fix Taken (UTC)", gpsParser.parse_utc_time(split_gprmc[1]));
        gprmc_obj.put("Status", gpsParser.parse_status(split_gprmc[2]));
        gprmc_obj.put("Latitude (verbose)", gpsParser.parse_verbose_coordinates(split_gprmc[3], split_gprmc[4]));
        gprmc_obj.put("Latitude (numeric coordinates)", gpsParser.parse_numeric_coordinates(split_gprmc[3], split_gprmc[4]));
        gprmc_obj.put("Longitude (verbose)", gpsParser.parse_verbose_coordinates(split_gprmc[5], split_gprmc[6]));
        gprmc_obj.put("Longitude (numeric coordinates)", gpsParser.parse_numeric_coordinates(split_gprmc[5], split_gprmc[6]));
        gprmc_obj.put("Speed over the Ground (knots)", split_gprmc[7]);
        gprmc_obj.put("Track Angle in degrees (True)", split_gprmc[8]);
        gprmc_obj.put("Date (From GPRMC Message)", gpsParser.parse_date(split_gprmc[9]));

        set_latitude(gpsParser.parse_numeric_coordinates(split_gprmc[3], split_gprmc[4]));
        set_longitude(gpsParser.parse_numeric_coordinates(split_gprmc[5], split_gprmc[6]));

        return gprmc_obj;

    }



}
