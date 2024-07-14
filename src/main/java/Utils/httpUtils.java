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

import static Gui.Main.logger;

public class httpUtils {

    // We make this a separate static variable such that the value is not lost if it fails the try-catch condition
    private static String raw_reply_content;
    private static int response_code;

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
            response_code = conn.getResponseCode();
            String contentType = conn.getHeaderField("Content-Type");
            String userAgent = conn.getRequestProperty("User-Agent");

            if (response_code < 300) {
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
                raw_reply_content = response_str_builder.toString();
                return raw_reply_content;
            }
            // If the HTTP request has failed, then we return the "failure" message string defined in the
            // (BLE_GATT_Client_for_Windows.exe) application
            else {
                // Print statements kept to avoid affecting the unit tests
                System.out.println("HTTP Request Failed with Code: " + response_code);
                System.out.println("HTTP Content Type: " + contentType);
                System.out.println("HTTP User Agent: " + userAgent);

                logger.error("HTTP Request Failed with Code: " + response_code);
                logger.info("HTTP Content Type: " + contentType);
                logger.info("HTTP User Agent: " + userAgent);
                // Sets up a char input stream from the raw byte stream (specifically Error Byte Stream) received from the API call.
                BufferedReader stream_reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

                // We configure a generic approach to receive (potentially) multiple strings from the API server
                String response_line;
                StringBuilder response_str_builder = new StringBuilder();
                while ((response_line = stream_reader.readLine()) != null) {
                    response_str_builder.append(response_line);
                }
                raw_reply_content = response_str_builder.toString();
                return raw_reply_content;
            }

        } catch (IOException e) {
            // This error can be thrown by a number of situations (handled entirely by the Java app).
            // Situations include, wrong input type provided as macAddress, or an inability to make external queries

            // Print statements kept to avoid affecting the unit tests
            System.out.println("Error Detected (generic_http_request): " + e.toString());
            System.out.println("Returning Error Message: " + e.toString());

            logger.error("Error Detected (generic_http_request): " + e.toString());
            logger.error("Returning Error Message: " + e.toString());
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
