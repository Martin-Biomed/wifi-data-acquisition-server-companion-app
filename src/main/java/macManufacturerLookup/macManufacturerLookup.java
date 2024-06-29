package macManufacturerLookup;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import Utils.httpUtils;

import Constants.Constants;

/** This Class is intended to be used to try to determine the Equipment Manufacturer of a Wi-Fi AP
 * based on the detected MAC Address.
 *
 * To extract the Coordinates, we use an online API defined by (MAC_OEM_Lookup_baseURL).
 *
 * Calls to the API are not always guaranteed to return valid data.
 *
 */

public class macManufacturerLookup {

    // Java Functions require you to throw an exception when a possible error can be anticipated.
    public static String get_vendor_from_mac(String macAddress) throws IOException {
        try {
            // We define the URL to send an HTTP GET Request
            String baseURL = Constants.MAC_OEM_Lookup_baseURL;
            URL url = new URL(baseURL + macAddress);
            System.out.println("Sending HTTP Request to: " + baseURL + macAddress);

            // Opening the connection to the online API URL
            String response = httpUtils.generic_http_request(url, "GET");
            return response;

        } catch (IOException e){
            // This error can be thrown by a number of situations.
            // Situations include, wrong input type provided as macAddress, or an inability to make external queries
            System.out.println("Error Detected: " + e.toString());
            throw new RuntimeException(e);
        }
    }
}
