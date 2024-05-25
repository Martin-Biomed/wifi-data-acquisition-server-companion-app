package Utils;

import Constants.Constants;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.*;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class httpUtilsTest {

    private Integer string_test_len = 20;

    private static int free_port = httpUtils.findFreePort();

    // Required to be able to Mock an actual API call to the selected Port.
    // We chose the port we allocate for the (BLE_GATT_Client_for_Windows.exe) app
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().port(free_port));

    @Test
    public void test_generic_http_request_valid_request() throws IOException {

        String random_string = simulationUtils.generateRandomUTFString(this.string_test_len);
        String encoded_str = httpUtils.return_url_encoded_str(random_string);
        URL selected_url = new URL(
                "http://" + Constants.BLE_Client_hostname + ":" + free_port + Constants.device_name_topic + encoded_str);
        System.out.println("Generated URL: " + encoded_str);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.device_name_topic + encoded_str))
                .willReturn(WireMock.aResponse().
                        withBody("Value Updated")
                        .withStatus(200)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_name_topic + encoded_str);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = httpUtils.generic_http_request(selected_url, "PUT");

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertEquals(response, "Value Updated");

    }

    @Test
    public void test_generic_http_request_invalid_request_type() throws IOException {

        String random_string = simulationUtils.generateRandomUTFString(this.string_test_len);
        String encoded_str =  httpUtils.return_url_encoded_str(random_string);
        URL selected_url = new URL(
                "http://" + Constants.BLE_Client_hostname + ":" + free_port + Constants.device_name_topic + encoded_str);
        System.out.println("Generated URL: " + encoded_str);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo(Constants.device_name_topic + encoded_str))
                .willReturn(WireMock.aResponse()
                        .withBody("The method is not allowed for the selected URL.")
                        .withStatus(405)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_name_topic + encoded_str);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = httpUtils.generic_http_request(selected_url, "GET");

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("405"));

    }


}