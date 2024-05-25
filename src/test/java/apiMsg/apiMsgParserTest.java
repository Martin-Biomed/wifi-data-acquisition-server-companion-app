package apiMsg;

import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import Utils.stringUtils;
import Utils.httpUtils;
import Utils.simulationUtils;
import apiMsg.apiMsgParser;

import customExceptions.customException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class apiMsgParserTest {

    // Object is created as a rule that expects none exception is thrown so this rule doesn’t affect all existing test methods.
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_wifi_scan_response_success() throws UnsupportedEncodingException, customException {

        int json_strings = 12;
        int num_of_keys = 4;

        StringBuilder combined_response_str = new StringBuilder(new String());

        // We create a JSON Object Array that will store the fake generated objects for comparison with wifi parsing
        JSONObject[] simulated_json_obj_arr = new JSONObject[json_strings];

        // We have to generate a simulated (random) reply to prove the functions can handle edge cases
        for (int i = 0; i < json_strings; i++) {
            String response_str = simulationUtils.generate_random_1d_json_str(num_of_keys, 20, 40);
            simulated_json_obj_arr[i] = stringUtils.convert_string_to_json_obj(response_str);
            //System.out.println("Generated JSON String (Fake Response): " + response_str);
            combined_response_str.append(response_str);
        }

        System.out.println("Combined JSON String (Simulated Response): \n" + combined_response_str);

        // Parsing simulated wifi_scan response (as if it had been generated from the ESP32 running wifi-data-acquisition-server)
        JSONObject[] parsed_json_arr = apiMsgParser.parse_wifi_scan_response(combined_response_str.toString());

        // We compare the generated Objects with the wifi_parsed objects
        for (int i = 0; i < json_strings; i++) {
            Assert.assertEquals(simulated_json_obj_arr[i].toString(), parsed_json_arr[i].toString());
        }
    }

    @Test
    public void test_wifi_scan_response_not_json_formatted() throws customException {

        int json_strings = 4;

        StringBuilder combined_response_str = new StringBuilder(new String());

        // We have to generate a simulated (random) reply to prove the functions can handle edge cases
        for (int i = 0; i < json_strings; i++) {
            String response_str = simulationUtils.generateRandomUTFString(10);
            combined_response_str.append(response_str);
            combined_response_str.append(",");
        }

        System.out.println("Combined JSON String (Simulated Response): \n" + combined_response_str);

        // We declare the Exception details that we expect to receive after we call the last function in this test
        thrown.expect(customException.class);
        thrown.expectMessage("An Exception occurred while Splitting the JSON string");

        JSONObject[] parsed_json_arr = apiMsgParser.parse_wifi_scan_response(combined_response_str.toString());
    }

    @Test
    public void test_wifi_scan_response_json_array() throws customException, UnsupportedEncodingException {

        int json_strings = 4;
        int num_of_keys = 4;


        // We create a JSONArray that will store the fake generated objects
        // The JSONArray format is incompatible with the (split_json_1d_str) function that is used when parsing Wi-Fi scan replies
        JSONArray simulated_json_obj_arr = new JSONArray();

        // We have to generate a simulated (random) reply to prove the functions can handle edge cases
        for (int i = 0; i < json_strings; i++) {
            String response_str = simulationUtils.generate_random_1d_json_str(num_of_keys, 20, 40);
            simulated_json_obj_arr.put(stringUtils.convert_string_to_json_obj(response_str));
        }

        System.out.println("Generated JSONArray Object: \n" + simulated_json_obj_arr);

        // We declare the Exception details that we expect to receive after we call the last function in this test
        thrown.expect(customException.class);
        thrown.expectMessage("An Exception occurred while Splitting the JSON string");

        JSONObject[] parsed_json_arr = apiMsgParser.parse_wifi_scan_response(simulated_json_obj_arr.toString());
    }
}