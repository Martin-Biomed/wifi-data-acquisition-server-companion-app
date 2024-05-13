package outgoingApiCalls;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import Constants.Constants;

// These functions will only work if the (BLE_GATT_Client_for_Windows.exe) app is run
// and configured to enable the API server
public class outgoingApiCalls {

    // The API server topics (URL) are defined in the Constants file

    // Some chars are interpreted as "special" when pasted directly into a URL, so we encode input strings instead
    public static String return_url_encoded_str(String original_str) throws UnsupportedEncodingException {
        try {
            String encoded_str = URLEncoder.encode(original_str, StandardCharsets.UTF_8.toString());
            return encoded_str;
        }
        catch (UnsupportedEncodingException e){
            return e.toString();
        }
    }

    // This generic function should be appropriate for PUT, POST and GET requests
    public static String generic_http_request(URL selected_url, String RequestType) throws IOException {
        try {
            // Opening the connection to the API URL (API server created by BLE_GATT_Client_for_Windows.exe)
            HttpURLConnection conn = (HttpURLConnection) selected_url.openConnection();
            conn.setRequestMethod(RequestType);
            conn.setInstanceFollowRedirects(true);

            // Check if connection was successful
            int responseCode = conn.getResponseCode();
            String contentType = conn.getHeaderField("Content-Type");
            String userAgent = conn.getRequestProperty("User-Agent");

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // Sets up a char input stream from the raw byte stream received from the API call.
                InputStreamReader input_char_stream = new InputStreamReader(conn.getInputStream());

                // A Buffered Reader lets you read and generate strings from a char input stream. We use the default size.
                BufferedReader stream_reader = new BufferedReader(input_char_stream);

                // We configure a generic approach to receive (potentially) multiple strings from the API server
                String response_line;
                StringBuilder response_str_builder = new StringBuilder();
                while ((response_line = stream_reader.readLine()) != null) {
                    response_str_builder.append(response_line);
                }
                stream_reader.close();

                // We convert the StringBuilder object to a String and return it
                return (response_str_builder.toString());
            }
            // If the HTTP request has failed, then we return a null string
            else {
                System.out.println("HTTP Request Failed with Code: " + responseCode);
                System.out.println("HTTP Content Type: " + contentType);
                System.out.println("HTTP User Agent: " + userAgent);
                String request_error = "Failed to connect to HTTP endpoint (Error Code: " + responseCode + ")";
                return request_error;
            }

        } catch (IOException e) {
            // This error can be thrown by a number of situations.
            // Situations include, wrong input type provided as macAddress, or an inability to make external queries
            System.out.println("Error Detected (generic_http_request): " + e.toString());
            return e.toString();
        }
    }

    public static String put_ble_device_name(String device_name) throws IOException {
        try {
            // We define the URL to send an HTTP GET Request
            String baseURL = Constants.BLE_Client_baseURL;
            String device_name_topic = Constants.device_name_topic;

            String encoded_device_name = return_url_encoded_str(device_name);
            URL url = new URL(baseURL + device_name_topic + encoded_device_name);
            System.out.println("Sending HTTP Request to: " + baseURL + device_name_topic + encoded_device_name);

            String response_str = generic_http_request(url, "PUT");
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
