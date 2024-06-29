package distanceCalcs;


import Constants.Constants;

/**
 * This function calculates the Free Space Path Loss (FSPL)
 Refer to: https://www.electronics-notes.com/articles/antennas-propagation/propagation-overview/free-space-path-loss.php

 The full equation is: FSPL = 20log(d) + 20log(f) + 32.44 - Gtx - Grx [d = distance in km, f = MHz]

 A Wi-Fi Antenna (specifically that of the ESP32) is designed to be omnidirectional (no directional gain during Tx or Rx).
 An assumption is made that both wireless APs are using omnidirectional antennas (Gtx = Grx = 0)

 The Total Losses is given by: Free Space Path Loss (FSPL) + Attenuation Losses

 The following equation is derived:
 Received Power [RSSI (dBm)] = Transmitted Power - (FSPL + Attenuation Losses)

 The max transmission power for 2.4GHz tends to be: 12dBm (phones) - 20dBm (dedicated APs)
 Refer to: https://metis.fi/en/2017/10/txpower/

 The final formula derivation for distance is:

 FSPL = Transmitted Power - Attenuation Losses - Received Power
 20log(d) = Transmitted Power - Attenuation Losses - Received Power - 20log(f) - 32.44
 d = 10 ^ [(Transmitted Power - Attenuation Losses - Received Power - 20log(f) - 32.44)/20]

*/

public class distanceCalculator {

    private static int frequencyChannel;

    // This variable determines if the AP is coming from a Mobile Device or dedicated Wi-Fi AP
    private static boolean deviceType = false;

    // Centre Frequency of the Current AP (MHz)
    private static int centreFrequency = Constants.default_centre_frequency;

    public void setFrequencyChannel(int channel){ frequencyChannel = channel; }

    public void setCentreFrequency(int channel){
        centreFrequency = getFrequencyFromWifiChannel(channel);
    }

    public void setDeviceType(boolean device){
        deviceType = device;
    }

    public static double estimateDistanceFromWifiAP(int channel, boolean device, int rssi, float attenuation){

        float frequency = getFrequencyFromWifiChannel(channel);

        int transmit_power;

        // If we are dealing with a Mobile device, use the lower known power level
        if (device){
            transmit_power = 12;
        }
        else {
            transmit_power = 20;
        }

        double remaining_formula;
        double distance;

        // Calculate Distance based on input parameters:
        remaining_formula = transmit_power - attenuation - rssi - 20*Math.log10(frequency) - 32.44;
        distance = Math.pow(10, remaining_formula/20);

        System.out.println("Calculated Distance (km): " + distance);
        return distance;
    }

    public static int getFrequencyFromWifiChannel(int channel){

        int currentCentreFrequency;

        switch(channel){
            case(1):
                currentCentreFrequency = 2412;
                break;
            case(2):
                currentCentreFrequency = 2417;
                break;
            case(3):
                currentCentreFrequency = 2422;
                break;
            case(4):
                currentCentreFrequency = 2427;
                break;
            case(5):
                currentCentreFrequency = 2432;
                break;
            case(6):
                currentCentreFrequency = 2437;
                break;
            case(7):
                currentCentreFrequency = 2442;
                break;
            case(8):
                currentCentreFrequency = 2447;
                break;
            case(9):
                currentCentreFrequency = 2452;
                break;
            case(10):
                currentCentreFrequency = 2457;
                break;
            case(11):
                currentCentreFrequency = 2462;
                break;
            case(12):
                currentCentreFrequency = 2467;
                break;
            case(13):
                currentCentreFrequency = 2472;
                break;
            case(14):
                currentCentreFrequency = 2484;
                break;

            default:
                // If no Band is found, then we are forced to use the basic value of 2.4GHz
                currentCentreFrequency = 2400;
        }
        return currentCentreFrequency;
    }
}



