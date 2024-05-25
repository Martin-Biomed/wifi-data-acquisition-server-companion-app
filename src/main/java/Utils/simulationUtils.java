package Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import Utils.stringUtils;

public class simulationUtils {

    // Generates a Random UTF-8 String of specified length
    public static String generateRandomUTFString(int length){
        // choose a Character random from this String
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString = new String(array, StandardCharsets.UTF_8);

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // From the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);
            String ch_str = Character.toString(ch); // Required because of data type compatibility with Regex matching

            // Not all UTF-8 encodeable chars are printable
            if (StandardCharsets.UTF_8.newEncoder().canEncode(ch) &&
                    ch_str.matches("[\\x20-\\x7E\\xA0-\\xFF]") &&
                    (length > 0)) {

                r.append(ch);
                length--;
            }
        }
        // return the resultant string
        return r.toString();
    }

    // This function can be used to generate a JSON-formatted String with completely random keys and values
    public static String generate_random_1d_json_str(int num_of_keys, int key_length, int value_length)
            throws UnsupportedEncodingException {

        System.out.println("Creating 1D JSON String...");

        JSONObject randomJsonObj = new JSONObject();
        for (int i=0; i<num_of_keys; i++){
            String key = generateRandomUTFString(key_length);
            String value = generateRandomUTFString(value_length);
            randomJsonObj.put(httpUtils.return_url_encoded_str(key), httpUtils.return_url_encoded_str(value));
            //System.out.println("Adding JSON Str: " + randomJsonObj);
        }
        return randomJsonObj.toString();
    }
}
