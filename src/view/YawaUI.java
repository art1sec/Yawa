package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;

import com.google.gson.Gson;

import controller.Yawa;
import model.DroidSans;
import model.Settings;
import owm.Location;
import owm.OneCallContainer;

public class YawaUI {

    private OneCallContainer occ;
    private Settings settings;
    private Yawa yawa;

    private JFrame frame;
    private JPanel panel, settingsPanel;
    private Font myfont;
    private JButton locationButton;
    private JLabel[] hourIconLabel;
    private JLabel[] hourDetailLabel;
    private JLabel currentDetailLabel, currentIconLabel, currentTempLabel;
    private GridBagConstraints c;

    private Location[] locations;

    private final InsetsUIResource IN0 = new InsetsUIResource(0, 0, 0, 0);
    private final static String APIURL = "http://api.openweathermap.org/geo/1.0/direct?";
    
    public YawaUI(Yawa yawa) {

        this.yawa = yawa;
        this.occ = yawa.getOCC();
        this.settings = yawa.getSettings();

        UIManager.getLookAndFeelDefaults().put("Panel.font", DroidSans.load(14));
        UIManager.getLookAndFeelDefaults().put("Panel.background", new ColorUIResource(42, 42, 42));
        UIManager.getLookAndFeelDefaults().put("Panel.foreground", Color.white);
        UIManager.getLookAndFeelDefaults().put("Label.font", DroidSans.load(14));
        UIManager.getLookAndFeelDefaults().put("Label.background", new ColorUIResource(42, 42, 42));
        UIManager.getLookAndFeelDefaults().put("Label.foreground", Color.white);
        UIManager.getLookAndFeelDefaults().put("Button.font", DroidSans.load(16));
        UIManager.getLookAndFeelDefaults().put("Button.background", Color.darkGray);
        UIManager.getLookAndFeelDefaults().put("Button.foreground", Color.white);
        UIManager.getLookAndFeelDefaults().put("TextField.font", DroidSans.load(14));

        frame = new JFrame();
        panel = new JPanel();
        panel.setBackground(new ColorUIResource(42, 42, 42));
        panel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new InsetsUIResource(0, 0, 0, 0);
        c.ipadx = 0; c.ipady = 0;

    }

    public void createSettingsPanel() {

        settingsPanel = new JPanel();
        settingsPanel.setVisible(!yawa.isReady());
        settingsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel keyLabel = new JLabel();
        keyLabel.setText("API key: (from openweathermap.org)");
        settingsPanel.add(keyLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;

        JTextField inputKeyField = new JTextField(20);
        try {
            inputKeyField.setText(settings.key);
        } catch (Exception e) {
            // pass
        }
        
        settingsPanel.add(inputKeyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new InsetsUIResource(12, 0, 0, 0);
        settingsPanel.add(new JLabel("Search for location: (and select)"), gbc);
        gbc.insets = IN0;

        gbc.gridy = 3;
        JTextField inputField = new JTextField(20);
        settingsPanel.add(inputField, gbc);

        gbc.gridy = 4;
        JComboBox<Location> locationBox = new JComboBox<>();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        settingsPanel.add(locationBox, gbc);

        gbc.gridy = 0; gbc.gridx = 1; gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new InsetsUIResource(0, 12, 0, 0);
        JButton saveButton = new JButton("Save");
        settingsPanel.add(saveButton, gbc);
        gbc.insets = IN0;

        inputField.addActionListener(e -> {
            try {
                settings.key = inputKeyField.getText();
            } catch (Exception infe) {
                settings = new Settings();
                settings.key = inputKeyField.getText();
            }
            locations = fetchLocations(inputField.getText());
            locationBox.removeAllItems();
            for(Location l: locations) {
                locationBox.addItem(l);
            }
        });

        saveButton.addActionListener(e -> {
            Location l = locations[locationBox.getSelectedIndex()];
            try {
                settings.key = inputKeyField.getText();
            } catch (Exception sabe) {
                settings = new Settings();
                settings.key = inputKeyField.getText();
            }
            settings.name = l.name;
            settings.country = l.country;
            settings.state = l.state;
            settings.lat = l.lat;
            settings.lon = l.lon;
            try {
                yawa.saveSettings(settings);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            settingsPanel.setVisible(false);
            refreshContent();
        });

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


    public void createInitialLayout() {

        /*
         * Let's start with the settings button
         * which is also the subtitle of oúr app
         * showing the current location
        */
        c.gridy = 0; c.gridx = 0;
        c.gridwidth = 6; c.gridheight = 1;
        c.anchor = GridBagConstraints.WEST;
        locationButton = new JButton();
        locationButton.addActionListener(e -> {
            settingsPanel.setVisible(!settingsPanel.isVisible());
        });
        locationButton.setFocusable(false);
        locationButton.setBorderPainted(false);
        panel.add(locationButton, c);
        c.anchor = GridBagConstraints.CENTER;

        c.insets = new InsetsUIResource(0, 0, 16, 0);

        /*
         * Current weather icon
        */
        c.gridy = 1; c.gridx=0;
        c.gridwidth = 2; c.gridheight = 4;
        currentIconLabel = new JLabel();
        panel.add(currentIconLabel, c);

        /*
         * Detail panel (table)
        */
        c.gridy = 1; c.gridx = 4;
        myfont = DroidSans.load(15);
        currentDetailLabel = new JLabel();
        currentDetailLabel.setFont(myfont);
        panel.add(currentDetailLabel, c);

        /*
         * Current temperature
        */
        c.gridy = 1; c.gridx=2;
        myfont = DroidSans.load(48, true);
        currentTempLabel = new JLabel();
        currentTempLabel.setFont(myfont);
        panel.add(currentTempLabel, c);
        
        /*
         * Precipitation within next hour (to be added)
        */

        /*
         * 12 hourly forecasts in one row
        */
        hourIconLabel = new JLabel[12];
        hourDetailLabel = new JLabel[12];
        myfont = DroidSans.load(16);
        c.gridwidth = 1; c.gridheight = 1;
        c.insets = new InsetsUIResource(0, 8, 0, 8);
        c.anchor = GridBagConstraints.CENTER;

        for(int i=0; i<12; i++) {

            c.gridy = 5; c.gridx = i;
            hourIconLabel[i] = new JLabel();
            hourIconLabel[i].setFont(myfont);
            c.anchor = GridBagConstraints.CENTER;
            panel.add(hourIconLabel[i], c);

            c.gridy = 6; c.gridx = i;
            hourDetailLabel[i] = new JLabel();
            hourDetailLabel[i].setFont(myfont);
            c.insets = new InsetsUIResource(0, 8, 16, 8);
            c.anchor = GridBagConstraints.CENTER;
            panel.add(hourDetailLabel[i], c);
            c.insets = new InsetsUIResource(0, 8, 0, 8);

        }

        c.insets = IN0;
        c.gridy = 0; c.gridx = 6;
        c.gridwidth = 6; c.gridheight = 5;
        panel.add(settingsPanel, c);

        frame.add(panel);

    }

    public void refreshContent() {

        String text;
        occ = yawa.getOCC();

        if(!yawa.isReady() || settings.name.length()<=0) { return; }
        
        frame.setTitle("Yet Another Weather App - "+occ.current.getDt()
                .format(DateTimeFormatter.ofPattern("dd.MM. HH:mm")));
        
        locationButton.setText(settings.name+", "+
            settings.country+((settings.state.length()>0)?(", "+settings.state):""));
        currentIconLabel.setIcon(getIcon(occ.current.getWeather()[0].getId()));

        text = "<html><style>td {text-align:right;}</style>"+
            "<table><tr><td></td><td><span style=font-size:120%;>♒</span> " +
            occ.current.getHumidity()+" %</td></tr><tr><td>"+
            "<span style=font-size:300%;font-weight:bold;>"+
            getWindDirection(occ.current.getWindDeg())+"</span></td><td>" +
            occ.current.getWindSpeed()+" km/h <br> "+
            occ.current.getWindGust()+" <span style=font-size:75%;>km/h</span> " +
            "</td></tr></table></html>";
        currentDetailLabel.setText(text);
        text = "<html>"+occ.current.getTemp()+
            " °C<br><div style=font-size:75%;font-weight:normal;text-align:right>"+
            occ.current.getFeelsLike()+" °C</div></html>";
        currentTempLabel.setText(text);

        for(int i=0; i<12; i++) {
            OneCallContainer.Hourly hour = occ.hourly[i];
            hourIconLabel[i].setIcon(getIcon(hour.getWeather()[0].getId(), 1, hour.getHour()));

            hourIconLabel[i].setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                hour.getHour().format(DateTimeFormatter.ofPattern("HH:mm")),
                TitledBorder.LEFT, TitledBorder.TOP,
                DroidSans.load(12),
                Color.white));
            
            hourDetailLabel[i].setText("<html><div style=text-align:center><big>"+
                hour.getTemp()+"</big> <small>°C</small><br>"+
                hour.getWindSpeed()+" <small>km/h</small><br>"+
                hour.getPop()+" <small>%</small></div></html>");
        }

        frame.pack();
        frame.setSize(frame.getWidth(), frame.getHeight()+8);
        frame.setMinimumSize(new DimensionUIResource(frame.getWidth(), frame.getHeight()+8));

    }

    public void createAndShowGUI() {

        createSettingsPanel();
        createInitialLayout();
        refreshContent();

        // frame.getContentPane().setBackground(new ColorUIResource(42, 42, 42));
        // frame.getContentPane().setForeground(new ColorUIResource(255, 255, 255));
        frame.pack();
        frame.setMinimumSize(new DimensionUIResource(frame.getWidth(), frame.getHeight()+8));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }


    public ImageIcon getIcon(int id) {
        return getIcon(id, 0);
    }

    public ImageIcon getIcon(int id, int size) {
        return getIcon(id, 0, occ.current.getDt());
    }

    public ImageIcon getIcon(int id, int size, LocalDateTime t) {

        java.net.URL iconURL;

        LocalDateTime sunrise = occ.current.getSunrise();
        LocalDateTime sunset = occ.current.getSunset();

        String daylight = "day";
        if(t.isBefore(sunrise) || t.isAfter(sunset)) {
            daylight = "night";
        }

        String path = "/res/png/";
        if(size>0) { path += "small/"; }
        
        switch(id) {
            /*
             *   Group 2xx: Thunderstorm
            */
            case 200:
            case 201:
            case 202:
            
            case 210:
            case 211:
            case 212:
            
            case 221:
            iconURL = getClass().getResource(path+"thunderstorm.png"); break;
            
            case 230:
            case 231:
            case 232:
            iconURL = getClass().getResource(path+"thundershower_"+daylight+".png"); break;

            /*
             *   Group 5xx: Rain
            */
            case 500:
            iconURL = getClass().getResource(path+"light_rain.png"); break;
            case 501:
            iconURL = getClass().getResource(path+"moderate_rain.png"); break;
            case 502:
            case 503:
            case 504:
            iconURL = getClass().getResource(path+"heavy_rain.png"); break;

            /*
             *   Group 6xx: Snow
            */
            case 600:
            iconURL = getClass().getResource(path+"light_snow.png"); break;
            case 601:
            iconURL = getClass().getResource(path+"snow.png"); break;

            /*
             *   Group 7xx: Atmosphere
            */
            case 701:
            iconURL = getClass().getResource(path+"mist.png"); break;
            case 721:
            iconURL = getClass().getResource(path+"haze_"+daylight+".png"); break;

            /*
             *   Group 800: Clear
            */
            case 800:
            iconURL = getClass().getResource(path+"clear_"+daylight+".png"); break;
            
            /*
             *   Group 80x: Clouds
            */
            case 801:
            iconURL = getClass().getResource(path+"few_clouds_"+daylight+".png"); break;
            
            case 802:
            iconURL = getClass().getResource(path+"scattered_clouds_"+daylight+".png"); break;

            case 803:
            iconURL = getClass().getResource(path+"broken_clouds_"+daylight+".png"); break;
            
            case 804:
            iconURL = getClass().getResource(path+"overcast_clouds.png"); break;
            
            /*
             *   default (should'nt ever take place)
            */
            default:
            iconURL = getClass().getResource(path+"na.png");
        
        }

        return (new ImageIcon(iconURL));

    }


    public String getWindDirection(int degree) {

        String direction;
        if(degree > 22 && degree <= 67) direction = "↙";
        else if(degree > 67 && degree <= 112) direction = "⬅";
        else if(degree > 112 && degree <= 157) direction = "↖";
        else if(degree > 157 && degree <= 202) direction = "⬆";
        else if(degree > 202 && degree <= 247) direction = "↗";
        else if(degree > 247 && degree <= 292) direction = "➡";
        else if(degree > 292 && degree <= 337) direction = "↘";
        else direction = "⬇";
        return direction;

    }
}
