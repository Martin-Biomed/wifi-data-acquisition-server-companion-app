package gpsProcessing;

import java.text.DecimalFormat;
import java.util.Objects;

import static Gui.Main.logger;

/** This Class is intended to be used to parse the raw JSON key-value pairs received from the ESP32 when we request
 * a GPS position (No internet required, just successful BLE GATT messaging to the ESP32).
 *
 * Note: This Class is only designed to be used with GPRMC sentence data, and turn it into more human-readable data
 * that can be displayed on the GUI.
 *
 */

public class gpsParser {

    public static String parse_utc_time(String input_str){

        if (input_str.isEmpty()){
            return "GPRMC message did not provide a TimeStamp";
        }

        if (input_str.length()>6){
            // We know that UTC time will always be passed with 6 digits before a decimal point
            String hours = input_str.substring(0, 2);
            String minutes = input_str.substring(2, 4);
            String seconds = input_str.substring(4, 6);
            String remainder = input_str.substring(6);

            String final_utc_time = hours + ":" + minutes + ":" + seconds + remainder;
            logger.info("Parsed the following time from GPRMC msg: " + final_utc_time);
            return final_utc_time;
        }
        else {
            logger.info("UTC Time contains unexpected number of Digits");
            return "UTC Time contains unexpected number of Digits";
        }
    }

    public static String parse_status(String input_str){

        if (input_str.isEmpty()){
            return "GPRMC message did not provide a Status";
        }

        // There should only ever be two options from this field
        if (Objects.equals(input_str, "A")){
            return "Active";
        }
        else if (Objects.equals(input_str, "V")) {
            return "Void";
        }
        else {
            return "Unknown GPRMC Status Option from ESP32";
        }
    }

    public static String parse_verbose_coordinates(String raw_coordinates, String direction){

        if (raw_coordinates.isEmpty() || direction.isEmpty()){
            return "GPRMC message did not provide valid coordinates";
        }

        String final_coordinates = "There was an error when processing the provided coordinates";

        //We know the non-angular parts of the coordinate will always require two digits on the left side of the decimal point
        String[] split_coordinates = raw_coordinates.split("\\.");

        String angle = split_coordinates[0].substring(0, split_coordinates[0].length()-2);
        logger.info("Parsed the following angle from provided coordinates: " + angle);

        String minutes =
                split_coordinates[0].substring(split_coordinates[0].length()-2) + "." + split_coordinates[1];

        logger.info("Remainder of the Coordinate String: " + minutes);

        final_coordinates = angle + " deg " + minutes + " " + direction;

        return final_coordinates;
    }

    public static String parse_numeric_coordinates(String raw_coordinates, String direction){

        if (raw_coordinates.isEmpty() || direction.isEmpty()){
            System.out.println("GPRMC message did not provide valid coordinates");
            return "GPRMC message did not provide valid coordinates";
        }

        String final_coordinates = "There was an error when processing the provided coordinates";

        //We know the non-angular parts of the coordinate will always require two digits on the left side of the decimal point
        String[] split_coordinates = raw_coordinates.split("\\.");

        String angle = split_coordinates[0].substring(0, split_coordinates[0].length()-2);
        logger.info("Parsed the following angle from provided coordinates: " + angle);

        String minutes =
                split_coordinates[0].substring(split_coordinates[0].length()-2) + "." + split_coordinates[1];
        logger.info("Remainder of the Coordinate String: " + minutes);

        // The formula for extracting Absolute Map Coordinates is:
        // Decimal Degrees = Angle + (Minutes/60) [Sign dictated by direction]

        // Creating an object of DecimalFormat class
        DecimalFormat df_obj = new DecimalFormat("#.###########");

        float coordinates = Float.valueOf(angle) + (Float.valueOf(minutes)/60);

        float direction_sign = 0;

        if (direction.matches("S") || direction.matches("W")){
            direction_sign = -1;
        }
        else if (direction.matches("N") || direction.matches("E")) {
            direction_sign = 1;
        }
        else {
            logger.error("Error parsing the direction value of the GPRMC coordinates");
            direction_sign = 0;
        }

        coordinates = coordinates * direction_sign;

        String formatted_coordinates = df_obj.format(coordinates);

        return formatted_coordinates;

    }


    public static String parse_date(String input_str){

        if (input_str.isEmpty()){
            return "Returned GPRMC message did not provide a Date";
        }

        if (input_str.length()>5){
            // We know that UTC time will always be passed with 6 digits before a decimal point
            String day = input_str.substring(0, 2);
            String month = input_str.substring(2, 4);
            String year = input_str.substring(4);

            String final_date = day+ "-" + month + "-" + year;
            logger.info("Parsed the following date from GPRMC msg: " + final_date);
            return final_date;
        }
        else {
            return "Date contains unexpected number of Digits";
        }
    }

}
