package MAC_OEM_Lookup;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import Constants.Constants;

public class MAC_OEM_Lookup {

    // Java Functions require you to throw an exception when a possible error can be anticipated.
    public static String get_vendor_from_mac(String macAddress) throws IOException {
        try {
            // We define the URL to send an HTTP GET Request
            String baseURL = Constants.MAC_OEM_Lookup_baseURL;
            URL url = new URL(baseURL + macAddress);
            System.out.println("Sending HTTP Request to: " + baseURL + macAddress);

            // Opening the connection to the online API URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);

            // Check if connection was successful
            int responseCode = conn.getResponseCode();
            String contentType = conn.getHeaderField("Content-Type");
            String userAgent = conn.getRequestProperty("User-Agent");

            if (responseCode == HttpURLConnection.HTTP_OK){

                // Sets up a char input stream from the raw byte stream received from the API call.
                InputStreamReader input_char_stream = new InputStreamReader(conn.getInputStream());

                // A Buffered Reader lets you read and generate strings from a char input stream. We use the default size.
                BufferedReader stream_reader = new BufferedReader(input_char_stream);

                // We expect the vendor to be provided as multiple strings (from the API website).
                StringBuilder vendor = new StringBuilder();
                String vendor_line;
                while ((vendor_line = stream_reader.readLine()) != null) {
                    vendor.append(vendor_line);
                }
                stream_reader.close();

                // We convert the StringBuilder object to a String and return it
                return (vendor.toString());
            }
            else {
                System.out.println("HTTP Request Failed with Code: " + responseCode);
                System.out.println("HTTP Content Type: " + contentType);
                System.out.println("HTTP User Agent: " + userAgent);
                return null;
            }

        } catch (IOException e){
            // This error can be thrown by a number of situations.
            // Situations include, wrong input type provided as macAddress, or an inability to make external queries
            return null;
        }
    }
}
