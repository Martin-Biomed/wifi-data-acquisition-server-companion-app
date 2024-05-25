package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class httpUtils {

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

            // This function only supports a sub-set of available HTTP request types
            if (!RequestType.toUpperCase().matches("POST|PUT|GET|DELETE")){
                return "Selected HTTP request type is not a valid option (POST/PUT/GET/DELETE)";
            }

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

    // Find free unused TCP port.
    public static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            return -1;
        }
    }
}
