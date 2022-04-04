package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.InsetsUIResource;

import model.Location;
import model.Settings;

public class SettingsPanel extends JPanel {

    private Settings settings;
    private Location[] locations;
    private final InsetsUIResource IN0 = new InsetsUIResource(0, 0, 0, 0);
    
    public SettingsPanel(YawaUI yawaUI) {

        super();
        settings = yawaUI.getYawa().getSettings();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel keyLabel = new JLabel();
        keyLabel.setText("API key: (from openweathermap.org)");
        add(keyLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;

        JTextField inputKeyField = new JTextField(20);
        inputKeyField.setText(settings.key);
        // inputKeyField.setFocusable(false);
        add(inputKeyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new InsetsUIResource(12, 0, 0, 0);
        add(new JLabel("Search for location: (and select)"), gbc);
        gbc.insets = IN0;

        gbc.gridy = 3;
        JTextField inputField = new JTextField(20);
        // inputField.setFocusCycleRoot(true);
        add(inputField, gbc);

        gbc.gridy = 4;
        JComboBox<Location> locationBox = new JComboBox<>();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(locationBox, gbc);

        gbc.gridy = 1; gbc.gridx = 1; gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new InsetsUIResource(0, 12, 0, 0);
        JButton saveButton = new JButton("Save");
        saveButton.setBorderPainted(false);
        add(saveButton, gbc);
        gbc.insets = IN0;

        if(settings.name != null) {
            Location currentLocation = new Location();
            currentLocation.name = settings.name;
            currentLocation.country = settings.country;
            currentLocation.state = settings.state;
            currentLocation.lat = settings.lat;
            currentLocation.lon = settings.lon;
            locationBox.addItem(currentLocation);
        }

        inputField.addActionListener(e -> {
            settings.key = inputKeyField.getText();
            locations = yawaUI.getYawa().fetchLocations(inputField.getText());
            locationBox.removeAllItems();
            for(Location l: locations) {
                locationBox.addItem(l);
            }
        });

        saveButton.addActionListener(e -> {
            Location l = (Location)locationBox.getSelectedItem();
            settings.key = inputKeyField.getText();
            settings.name = l.name;
            settings.country = l.country;
            settings.state = l.state;
            settings.lat = l.lat;
            settings.lon = l.lon;
            try {
                yawaUI.getYawa().saveSettings(settings);
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(-5);
            }
            setVisible(false);
            inputField.setText(null);
            yawaUI.setDayPanelVisible(true);
            yawaUI.refreshContent();
        });

    }

 }