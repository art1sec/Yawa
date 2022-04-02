package view;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.google.gson.Gson;

import controller.Yawa;
import model.Settings;
import owm.Location;

public class SettingsWindow extends JFrame {

    private Settings settings;
    private Location[] locations;
    private JComboBox<Location> locationBox;
    private JButton saveButton = new JButton("Save");
    private JTextField inputField = new JTextField(30);

    private final static String APIURL = "http://api.openweathermap.org/geo/1.0/direct?";

    SettingsWindow(JFrame frame, Settings settings, YawaUI mainWindow, Yawa yawa) {

        this.settings = settings;

        setLayout(new BorderLayout(8, 8));

        locationBox = new JComboBox<Location>();

        inputField.addActionListener(e -> {
            
            locations = fetchLocations(inputField.getText());

            locationBox.removeAllItems();
            for(Location l: locations) {
                locationBox.addItem(l);
            }

        });

        saveButton.addActionListener(e -> {
            Location l = locations[locationBox.getSelectedIndex()];
            settings.name = l.name;
            settings.country = l.country;
            settings.state = l.state;
            settings.lat = l.lat;
            settings.lon = l.lon;
            try {
                yawa.saveSettings(settings);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //yawa.callOwmApi();
            
            mainWindow.refreshContent();
        });

        add(inputField, BorderLayout.NORTH);
        add(locationBox, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        pack();
        // setSize(frame.getSize().width/4*3, (int)(frame.getSize().height*0.9));
        setLocationRelativeTo(frame);
        setVisible(true);

    }
    
    private Location[] fetchLocations(String q) {
        String json = "";
        try {
            URL url = new URL(APIURL+"q="+q+"&limit=5&appid="+settings.key);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.connect();
            InputStream is = c.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            json = br.readLine();
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.fromJson(json, Location[].class);

    }
}
