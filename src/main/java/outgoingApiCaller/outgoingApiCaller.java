package outgoingApiCaller;

import java.io.*;
import java.net.URL;

import Utils.httpUtils;

// These functions will only work if the (BLE_GATT_Client_for_Windows.exe) app is run
// and configured to enable the API server
public class outgoingApiCaller {

    // The API server topics (URL) are defined in the Constants file

    public static String execute_api_call(
            String specific_value, String api_topic, String RequestType, String hostname, Integer port) throws IOException {
        try {
            // We define the URL to send an HTTP GET Request
            String baseURL = "http://" + hostname + ":" + port.toString();

            String encoded_field = httpUtils.return_url_encoded_str(specific_value);
                URL url = new URL(baseURL + api_topic + encoded_field);
            System.out.println("Sending HTTP Request to: " + baseURL + api_topic + encoded_field);

            String response_str = httpUtils.generic_http_request(url, RequestType);
            System.out.println("Response from HTTP Request: " + response_str);
            return response_str;

        } catch (IOException e) {
            // This error can be thrown by a number of situations.
            // Situations include, wrong input type provided as a string, or an inability to make external queries
            System.out.println("Error Detected (put_ble_device_name): " + e.toString());
            return e.toString();
        }
    }

}
