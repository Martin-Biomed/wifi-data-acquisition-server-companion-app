package Gui;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import java.text.DecimalFormat;

public class DistanceCalculatorController {

    @FXML
    private TextArea light_attenuation_textarea, medium_attenuation_textarea, high_attenuation_textarea;
    @FXML
    private Label heading_label;

    public void initialize(){

        // We set a limit for how many decimal places we represent on the TextAreas
        DecimalFormat number_format = new DecimalFormat("#.##");

        // The original values are in (km)
        double low_att_distance = distanceCalcGuiUpdates.get_latest_low_att_distance();
        double medium_att_distance = distanceCalcGuiUpdates.get_latest_medium_att_distance();
        double high_att_distance = distanceCalcGuiUpdates.get_latest_high_att_distance();

        // We convert the values to (m)
        low_att_distance = low_att_distance * 1000;
        medium_att_distance = medium_att_distance * 1000;
        high_att_distance = high_att_distance * 1000;

        String ssid = distanceCalcGuiUpdates.get_latest_ssid();
        heading_label.setText(ssid);

        light_attenuation_textarea.setText(number_format.format(low_att_distance));
        medium_attenuation_textarea.setText(number_format.format(medium_att_distance));
        high_attenuation_textarea.setText(number_format.format(high_att_distance));

        light_attenuation_textarea.setEditable(false);
        medium_attenuation_textarea.setEditable(false);
        high_attenuation_textarea.setEditable(false);
    }

}
