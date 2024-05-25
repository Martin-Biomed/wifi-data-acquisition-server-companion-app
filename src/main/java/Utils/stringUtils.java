package Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

import customExceptions.customException;

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
                System.out.println("Custom Exception has been triggered");
                throw new customException("Input string " + init_string + " is a JSON Array (not compatible with this func)");
            }
            else if (!check_valid_json_str(init_string)){
                System.out.println("Custom Exception has been triggered");
                throw new customException("Input string " + init_string + " is not JSON formatted.");
            }
            else {
                // We split the strings based on the "}" at the end of a JSON 1D Object
                String[] split_string = init_string.split("((?<=}))");
                int arrayLength = split_string.length;
                System.out.println("Array has " + arrayLength + " terms.");

                System.out.println("The array has the following Sub-Strings:");

                /*
                for (String s : split_string) {
                    System.out.println(s);
                }
                */

                return split_string;
            }
        } catch (Exception e){
            // If an exception is triggered, then we return an empty array of Strings
            System.out.println("Caught the Custom Exception");
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
            System.out.println("Adding the following key to array: " + next_key);
            array_of_keys.add(next_key);
        }
        return array_of_keys;
    }
}
