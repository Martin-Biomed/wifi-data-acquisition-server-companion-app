package outgoingApiCalls;

import Constants.Constants;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.io.IOException;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.junit.Before;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static outgoingApiCalls.outgoingApiCalls.return_url_encoded_str;

public class outgoingApiCallsTest {

    private String device_name = "Default Name";

    // The length of the string to be generated for the device length
    private Integer name_len = 20;


    // Generates a Random UTF-8 String of specified length
    public String generateRandomUTFString(int length){
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

    // Required to be able to Mock an actual API call to the selected Port.
    // We chose the port we allocate for the (BLE_GATT_Client_for_Windows.exe) app
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().port(5900));

    @Before
    public void before_test() throws IOException {
        System.out.println("Starting WireMock");
        wireMockRule.start();
        WireMock.reset();
    }

    @Test
    public void test_put_ble_device_name_valid_request() throws IOException {

        this.device_name = generateRandomUTFString(this.name_len);
        System.out.println("Generated Device Name: " + this.device_name);

        String encoded_str = return_url_encoded_str(this.device_name);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.device_name_topic + encoded_str))
                .willReturn(WireMock.aResponse().
                        withBody("Value Updated")
                        .withStatus(200)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_name_topic + encoded_str);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCalls.put_ble_device_name(this.device_name);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertEquals(response, "Value Updated");

    }

    @Test
    public void test_put_ble_device_name_empty_string() throws IOException {

        this.device_name = "";
        System.out.println("Generated Device Name: " + this.device_name);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.device_name_topic + this.device_name))
                .willReturn(WireMock.aResponse().
                        withBody("The requested URL was not found on the server. " +
                                "If you entered the URL manually please check your spelling and try again.")
                        .withStatus(404)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_name_topic + this.device_name);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCalls.put_ble_device_name(this.device_name);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("404"));
    }

    @Test
    public void test_put_ble_device_name_inactive_server() throws IOException {

        // For this test, we avoid creating a Mock version of the (BLE_GATT_Client_for_Windows.exe) app.
        this.device_name = generateRandomUTFString(this.name_len);
        System.out.println("Generated Device Name: " + this.device_name);

        // We stop the rule so that the Stubs we configured in the previous steps aren't invoked on our API request
        // Other methods of trying to reset the stubs didn't seem to work.
        wireMockRule.stop();

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCalls.put_ble_device_name(this.device_name);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("Connection refused: connect"));
    }

}