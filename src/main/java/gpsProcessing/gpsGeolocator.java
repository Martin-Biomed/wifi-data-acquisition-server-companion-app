package gpsProcessing;

import Constants.Constants;
import Utils.httpUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/** This Class is intended to be used to try to determine the address of where the ESP32 has been deployed based on the
 * GPS numerical coordinates returned from the ESP32.
 *
 * Note: This Class is only useful once we have successfully parsed the GPS coordinates from the ESP32.
 *
 * To extract the Coordinates, we use an online API defined by (geoapify_reverse_geocoding_url).
 */

public class gpsGeolocator {

    public static String send_geoapify_api_request(String latitude, String longitude, String api_key) throws IOException {

        String url_str = Constants.geoapify_reverse_geocoding_url + "?lat=" + latitude + "&lon=" + longitude
                        + "&apiKey=" + api_key;

        System.out.println("Sending API Call to Geoapify: " + url_str);
        URL api_url = new URL(url_str);
        String response = httpUtils.generic_http_request(api_url, "GET");
        System.out.println("Received the following response from Geoapify: " + response);

        return response;
    }

    public static String parse_geoapify_api_response(String init_str){

        // The response from the API is given as a JSON object
        JSONObject geoapify_obj = new JSONObject(init_str);

        System.out.println("Attempting to extract JSON Array from initial string");

        // The address is given as JSON key-value pair in the "features" JSON Array
        JSONArray features = (JSONArray) geoapify_obj.get("features");

        System.out.println("Features JSON Array: " + features.toString());
        System.out.println("There are " + features.length() + " JSON Objects in the Array.");

        String return_str = "Problem while parsing Geoapify API response";

        // We have to iterate over all available JSON objects in the Array
        for (int i = 0; i < features.length(); i++)
        {
            JSONObject current_json_obj = features.getJSONObject(i);

            System.out.println("Iterating over JSON Object: " + current_json_obj.toString());

            // We print the available keys for this JSON object
            for (String key : current_json_obj.keySet()){
                System.out.println("Key available for JSON Object: " + key);
            }

            // The string that we care about is the content matching the "formatted" key
            if (current_json_obj.has("properties")) {

                System.out.println("Searching the properties JSON Object given by Geoapify API reply.");

                // The nested "properties" JSON Object has a set of JSON key-value pairs, we need to search for the
                // correct key (formatted) in this JSON object.
                JSONObject properties_obj = current_json_obj.getJSONObject("properties");

                System.out.println("Found required data in JSON Object: " + properties_obj.get("formatted").toString());
                return_str = properties_obj.get("formatted").toString();
            }
        }
        return return_str;
    }

}
