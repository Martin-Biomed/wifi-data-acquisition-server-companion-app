package outgoingApiCaller;

import Constants.Constants;
import Utils.httpUtils;
import Utils.stringUtils;
import Utils.simulationUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.io.IOException;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.junit.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class outgoingApiCallerTest {

    private String device_name = "Default Name";
    private String device_addr = "AA:BB:CC:DD:EE:FF";

    private String device_read_uuid = "0000fef4-0000-1000-8000-00805f9b34fb";
    private String device_write_uuid = "0000dead-0000-1000-8000-00805f9b34fb";

    // The length of the string to be generated for the device length
    private Integer name_len = 20;

    private static int free_port = httpUtils.findFreePort();


    // Required to be able to Mock an actual API call to the selected Port.
    // We chose the port we allocate for the (BLE_GATT_Client_for_Windows.exe) app
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().port(free_port));


    @Test
    public void test_put_ble_device_name_valid_request() throws IOException {

        this.device_name = simulationUtils.generateRandomUTFString(this.name_len);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_name);
        System.out.println("Generated Device Name: " + encoded_str);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.device_name_topic + encoded_str))
                .willReturn(WireMock.aResponse().
                        withBody("Value Updated")
                        .withStatus(200)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_name_topic + encoded_str);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_name, Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertEquals(response, "Value Updated");

    }

    @Test
    public void test_put_ble_device_name_empty_string() throws IOException {

        System.out.println("Using Empty String");

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.device_name_topic))
                .willReturn(WireMock.aResponse().
                        withBody("The requested URL was not found on the server. " +
                                "If you entered the URL manually please check your spelling and try again.")
                        .withStatus(404)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_name_topic);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                "", Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("The requested URL was not found on the server."));
    }

    @Test
    public void test_put_ble_device_name_inactive_server() throws IOException {

        // For this test, we avoid creating a Mock version of the (BLE_GATT_Client_for_Windows.exe) app.
        this.device_name = simulationUtils.generateRandomUTFString(this.name_len);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_name);
        System.out.println("Generated Device Name: " + encoded_str);

        // We stop the rule so that the Stubs we configured in the previous steps aren't invoked on our API request
        // Other methods of trying to reset the stubs didn't seem to work.
        wireMockRule.stop();
        //wireMockServer.stop();

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_name, Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("Connection refused: connect"));
    }

    @Test
    public void test_put_ble_device_name_valid_long_request() throws IOException {

        this.device_name = simulationUtils.generateRandomUTFString(150);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_name);
        System.out.println("Generated Device Name: " + encoded_str);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.device_name_topic + encoded_str))
                .willReturn(WireMock.aResponse().
                        withBody("Value Updated")
                        .withStatus(200)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_name_topic + encoded_str);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_name, Constants.device_name_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertEquals(response, "Value Updated");
    }

    // There are less scenarios that we need to check in this set of tests because the (BLE_GATT_Client_for_Windows.exe)
    // application has its own defined criteria for what is an acceptable MAC address.
    @Test
    public void test_put_ble_device_addr_valid_request() throws IOException {

        System.out.println("Generated Device Name: " + this.device_addr);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_addr);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.device_addr_topic + encoded_str))
                .willReturn(WireMock.aResponse().
                        withBody("Value Updated")
                        .withStatus(200)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_addr_topic + encoded_str);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_addr, Constants.device_addr_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertEquals(response, "Value Updated");
    }

    @Test
    public void test_put_ble_device_addr_empty_string() throws IOException {

        System.out.println("Using Empty String");

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.device_addr_topic))
                .willReturn(WireMock.aResponse().
                        withBody("The requested URL was not found on the server. " +
                                "If you entered the URL manually please check your spelling and try again.")
                        .withStatus(404)));

        System.out.println("Configured Mock Server Response for path: " + Constants.device_addr_topic);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                "", Constants.device_addr_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("The requested URL was not found on the server."));
    }

    @Test
    public void test_put_ble_device_addr_inactive_server() throws IOException {

        // For this test, we avoid creating a Mock version of the (BLE_GATT_Client_for_Windows.exe) app.
        System.out.println("Generated Device Name: " + this.device_addr);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_addr);

        // We stop the rule so that the Stubs we configured in the previous steps aren't invoked on our API request
        // Other methods of trying to reset the stubs didn't seem to work.
        wireMockRule.stop();
        //wireMockServer.stop();

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_addr, Constants.device_addr_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("Connection refused: connect"));
    }

    // There are less scenarios that we need to check in this set of tests because the (BLE_GATT_Client_for_Windows.exe)
    // application has its own defined criteria for what is an acceptable GATT Read UUID.
    @Test
    public void test_put_ble_gatt_read_valid_request() throws IOException {

        System.out.println("Generated Device Name: " + this.device_read_uuid);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_read_uuid);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.gatt_read_topic + encoded_str))
                .willReturn(WireMock.aResponse().
                        withBody("Value Updated")
                        .withStatus(200)));

        System.out.println("Configured Mock Server Response for path: " + Constants.gatt_read_topic + encoded_str);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_read_uuid, Constants.gatt_read_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertEquals(response, "Value Updated");
    }

    @Test
    public void test_put_ble_gatt_read_empty_string() throws IOException {

        System.out.println("Using Empty String");

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.gatt_read_topic))
                .willReturn(WireMock.aResponse().
                        withBody("The requested URL was not found on the server. " +
                                "If you entered the URL manually please check your spelling and try again.")
                        .withStatus(404)));

        System.out.println("Configured Mock Server Response for path: " + Constants.gatt_read_topic);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                "", Constants.gatt_read_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("The requested URL was not found on the server."));
    }

    @Test
    public void test_put_ble_gatt_read_inactive_server() throws IOException {

        // For this test, we avoid creating a Mock version of the (BLE_GATT_Client_for_Windows.exe) app.
        System.out.println("Generated Device Name: " + this.device_read_uuid);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_read_uuid);

        // We stop the rule so that the Stubs we configured in the previous steps aren't invoked on our API request
        // Other methods of trying to reset the stubs didn't seem to work.
        wireMockRule.stop();
        //wireMockServer.stop();

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_read_uuid, Constants.gatt_read_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("Connection refused: connect"));
    }

    // There are less scenarios that we need to check in this set of tests because the (BLE_GATT_Client_for_Windows.exe)
    // application has its own defined criteria for what is an acceptable GATT Read UUID.
    @Test
    public void test_put_ble_gatt_write_valid_request() throws IOException {

        System.out.println("Generated Device Name: " + this.device_write_uuid);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_write_uuid);

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.gatt_write_topic + encoded_str))
                .willReturn(WireMock.aResponse().
                        withBody("Value Updated")
                        .withStatus(200)));

        System.out.println("Configured Mock Server Response for path: " + Constants.gatt_write_topic + encoded_str);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_write_uuid, Constants.gatt_write_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertEquals(response, "Value Updated");

    }

    @Test
    public void test_put_ble_gatt_write_empty_string() throws IOException {

        System.out.println("Using Empty String");

        // The response string we receive from the remote API server should be simply forwarded via this function
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(Constants.gatt_write_topic))
                .willReturn(WireMock.aResponse().
                        withBody("The requested URL was not found on the server. " +
                                "If you entered the URL manually please check your spelling and try again.")
                        .withStatus(404)));

        System.out.println("Configured Mock Server Response for path: " + Constants.gatt_write_topic);

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                "", Constants.gatt_write_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("The requested URL was not found on the server."));
    }

    @Test
    public void test_put_ble_gatt_write_inactive_server() throws IOException {

        // For this test, we avoid creating a Mock version of the (BLE_GATT_Client_for_Windows.exe) app.
        System.out.println("Generated Device Name: " + this.device_write_uuid);
        String encoded_str = httpUtils.return_url_encoded_str(this.device_write_uuid);

        // We stop the rule so that the Stubs we configured in the previous steps aren't invoked on our API request
        // Other methods of trying to reset the stubs didn't seem to work.
        wireMockRule.stop();
        //wireMockServer.stop();

        // After we've defined the HTTP request + response from the Mock Server, we call this modified function and
        // compare our assertions to our actual functions
        String response = outgoingApiCaller.execute_api_call(
                this.device_write_uuid, Constants.gatt_write_topic, "PUT", Constants.BLE_Client_hostname, free_port);

        System.out.println("Received response: " + response);

        Assert.assertThat(response, is(notNullValue()));
        Assert.assertTrue(response.contains("Connection refused: connect"));
    }

}