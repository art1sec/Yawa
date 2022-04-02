package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;

import controller.Yawa;
import model.Settings;
import owm.OneCallContainer;

public class MainPanel extends JPanel {

    private OneCallContainer occ;
    private Settings settings;

    private Font myfont;
    private JButton locationButton;
    private JLabel[] hourIconLabel, hourDetailLabel;
    private JLabel currentDetailLabel, currentIconLabel, currentTempLabel;
    private final InsetsUIResource IN0 = new InsetsUIResource(0, 0, 0, 0);

    public MainPanel(JPanel settingsPanel, boolean isReady) {

        super();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

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
        locationButton.setVisible(isReady);
        add(locationButton, c);
        c.anchor = GridBagConstraints.CENTER;

        c.insets = new InsetsUIResource(0, 0, 16, 0);

        /*
         * Current weather icon
        */
        c.gridy = 1; c.gridx=0;
        c.gridwidth = 2; c.gridheight = 4;
        currentIconLabel = new JLabel();
        add(currentIconLabel, c);

        /*
         * Detail panel (table)
        */
        c.gridy = 1; c.gridx = 4;
        myfont = DroidSans.load(15);
        currentDetailLabel = new JLabel();
        currentDetailLabel.setFont(myfont);
        add(currentDetailLabel, c);

        /*
         * Current temperature
        */
        c.gridy = 1; c.gridx=2;
        myfont = DroidSans.load(48, true);
        currentTempLabel = new JLabel();
        currentTempLabel.setFont(myfont);
        add(currentTempLabel, c);
        
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
            add(hourIconLabel[i], c);

            c.gridy = 6; c.gridx = i;
            hourDetailLabel[i] = new JLabel();
            hourDetailLabel[i].setFont(myfont);
            c.insets = new InsetsUIResource(0, 8, 8, 8);
            c.anchor = GridBagConstraints.CENTER;
            add(hourDetailLabel[i], c);
            c.insets = new InsetsUIResource(0, 8, 0, 8);

        }

        c.insets = IN0;
        c.gridy = 0; c.gridx = 6;
        c.gridwidth = 6; c.gridheight = 5;
        add(settingsPanel, c);

    }


    public void refreshContent(JFrame frame, Yawa yawa) {

        String text;
        occ = yawa.getOCC();
        settings = yawa.getSettings();

        if(!yawa.isReady() || settings.name.length()<=0) { return; }
        
        frame.setTitle("Yet Another Weather App - "+occ.current.getDt()
                .format(DateTimeFormatter.ofPattern("dd.MM. HH:mm")));
        
        locationButton.setText(settings.name+", "+
            settings.country+((settings.state.length()>0)?(", "+settings.state):""));
        locationButton.setVisible(true);

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
        frame.setSize(frame.getWidth(), frame.getHeight());
        frame.setMinimumSize(new DimensionUIResource(frame.getWidth(), frame.getHeight()));

    }

    
    private ImageIcon getIcon(int id) {
        return getIcon(id, 0);
    }

    private ImageIcon getIcon(int id, int size) {
        return getIcon(id, 0, occ.current.getDt());
    }

    private ImageIcon getIcon(int id, int size, LocalDateTime t) {

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


    private String getWindDirection(int degree) {

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
