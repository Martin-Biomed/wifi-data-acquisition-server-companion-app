package outgoingApiCaller;

import java.io.*;
import java.net.URL;

import Utils.httpUtils;

import static Gui.Main.logger;

// These functions will only work if the (BLE_GATT_Client_for_Windows.exe) app is run
// and configured to enable the API server
public class outgoingApiCaller {

    // We make this a separate static variable such that the value is not lost if it fails the try-catch condition
    private static String response_str = "";


    // The API server topics (URL) are defined in the Constants file

    public static String execute_api_call(
            String specific_value, String api_topic, String RequestType, String hostname, Integer port) throws IOException {

        // We define the URL to send an HTTP GET Request
        String baseURL = "http://" + hostname + ":" + port.toString();

        String encoded_field = httpUtils.return_url_encoded_str(specific_value);
        URL url = new URL(baseURL + api_topic + encoded_field);
        logger.info("Sending HTTP Request to: " + baseURL + api_topic + encoded_field);

        // kept to avoid breaking existing unit tests
        System.out.println("Sending HTTP Request to: " + baseURL + api_topic + encoded_field);

        try{
            response_str = httpUtils.generic_http_request(url, RequestType);
            System.out.println("Response from HTTP Request: " + response_str);
            logger.info("Response from HTTP Request: " + response_str);
            return response_str;
        }
        catch (IOException e){
            // This error can be thrown by a number of situations.
            // Situations include, wrong input type provided as a string, or an inability to make external queries

            // kept to avoid breaking existing unit tests
            System.out.println("Error Detected: " + e.toString());
            logger.error("Error Detected: " + e.toString());
            return response_str;
        }
    }
}
