package Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Iterator;
import java.util.ArrayList;

import customExceptions.customException;

import static Gui.Main.logger;

public class stringUtils {

    // Checks if the provided string is JSON formatted
    public static boolean check_valid_json_str(String json_str){
        try {
            new JSONObject(json_str);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    // Checks if the provided string is a JSON Array
    // See: https://www.tutorialspoint.com/how-to-write-create-a-json-array-using-java
    public static boolean check_if_json_arr(String json_str){
        try {
            new JSONArray(json_str);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    // Converts an input string to a JSON Object
    public static JSONObject convert_string_to_json_obj(String init_string) {

        JSONObject jsonObject = new JSONObject(init_string);
        return jsonObject;
    }

    // This function is used to split an input string which consists of several JSON strings merged into one
    public static String[] split_json_1d_str(String init_string){
        try {
            // The manually-thrown Exceptions will trigger the creation and return of an empty array of Strings
            if (check_if_json_arr(init_string)){
                logger.error("Custom Exception has been triggered");
                throw new customException("Input string " + init_string + " is a JSON Array (not compatible with this func)");
            }
            else if (!check_valid_json_str(init_string)){
                logger.error("Custom Exception has been triggered");
                throw new customException("Input string " + init_string + " is not JSON formatted.");
            }
            else {
                // We split the strings based on the "}" at the end of a JSON 1D Object
                String[] split_string = init_string.split("((?<=}))");
                int arrayLength = split_string.length;
                logger.info("Array has " + arrayLength + " terms.");

                return split_string;
            }
        } catch (Exception e){
            // If an exception is triggered, then we return an empty array of Strings
            logger.error("Caught the Custom Exception" + e.getMessage());
            return new String[0];
        }
    }

    // This function returns an ArrayList of all the keys corresponding to this specific JSON Object
    public static ArrayList<String> return_available_json_keys(JSONObject jsonObject) throws customException {

        Iterator<String> json_keys = jsonObject.keys();

        // Check if the JSON Object has valid keys in the first place
        if (!json_keys.hasNext()){
            throw new customException("JSON Object " + jsonObject.toString() + " has no recognized keys");
        }

        // We initialise an ArrayList to store each individual key as a term in the list
        ArrayList<String> array_of_keys = new ArrayList<String>();

        while(json_keys.hasNext()){
            String next_key = json_keys.next();
            //logger.info("Adding the following key to array: " + next_key);
            array_of_keys.add(next_key);
        }
        return array_of_keys;
    }

    public static JSONObject convert_multiline_string_to_json_obj(String input_string){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            if (!input_string.contains("\n")){
                logger.error("Input String is not composed of multiple lines.");
                return jsonObject;
            }

            // Each line in the string has form: "key: value"
            String[] lines = input_string.split("\n");
            logger.info("Starting the process of splitting the String (Before Converting to JSON)");

            for (String line : lines){

                String[] key_value_str = line.split(": ");

                // Some fields in a JSON string may not meet the expected form (example: blank values)
                if (key_value_str.length > 1){
                    logger.info("The following parts have been separated: " + key_value_str[0] + "," + key_value_str[1]);
                    jsonObject.put(key_value_str[0], key_value_str[1]);
                }
            }
        }
        catch (Exception e){
            logger.error("Error while trying to convert string to JSON Object: " + e.getMessage());
        }
        return jsonObject;
    }

}
