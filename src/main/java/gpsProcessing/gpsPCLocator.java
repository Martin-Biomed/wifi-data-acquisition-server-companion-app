package gpsProcessing;

import Constants.Constants;
import Utils.httpUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/** This Class is intended to be used to try to determine the Coordinates of the PC running this application
 * based on the IP address allocated by the ISP.
 *
 * To extract the Coordinates, we use an online API defined by (ip_to_coordinates_api_url). Because of the innacuracy
 * of extracting coordinates using this method, there aren't any distance-based calcs made from these coordinates.
 *
 */
public class gpsPCLocator {

    public static JSONObject getComputerCoordinates() throws MalformedURLException {

        String pc_gps_api_url = Constants.ip_to_coordinates_api_url;

        URL url = new URL(pc_gps_api_url);
        System.out.println("Attempting to send request to API: " + pc_gps_api_url);

        try {
            String json_str = httpUtils.generic_http_request(url, "GET");
            System.out.println("Received the following response from the API: " + json_str);
            JSONObject json_obj = new JSONObject(json_str);
            return json_obj;

        } catch (IOException e) {
            System.out.println("Error retrieving PC Coordinates: " + e.toString());
            throw new RuntimeException(e);
        }
    }

}
