// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

import MAC_OEM_Lookup.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        //String macAddress = "A4:91:B1:CE:C4:59";
        //String macAddress = "FA:8F:CA:70:F1:5C";
        //String macAddress = "C8:3A:35:27:88:23";
        String macAddress = "D8:47:32:31:9B:34";
        String mac_vendor = MAC_OEM_Lookup.get_vendor_from_mac(macAddress);
        System.out.println("Vendor for MAC Address " + macAddress + " is " + mac_vendor);

    }
}