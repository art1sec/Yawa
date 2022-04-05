package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;

import model.OneCallContainer;
import model.Settings;

public class MainPanel extends JPanel {

    private int dailyPage;
    private DayPanel dayPanel;
    private OneCallContainer occ;
    private Settings settings;

    private Font myfont;
    private JButton locationButton;
    private JLabel[] hourIconLabel, hourDetailLabel;
    private JLabel currentDetailLabel, currentIconLabel, currentTempLabel;
    private final InsetsUIResource IN0 = new InsetsUIResource(0, 0, 0, 0);

    public MainPanel(SettingsPanel settingsPanel, DayPanel dayPanel, boolean isReady) {

        super();
        dailyPage = 0;
        this.dayPanel = dayPanel;
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
            dayPanel.setVisible(!dayPanel.isVisible());
            settingsPanel.setVisible(!settingsPanel.isVisible());
        });
        locationButton.setFocusable(false);
        locationButton.setBorderPainted(false);
        locationButton.setToolTipText("click to open settings panel");
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
        hourIconLabel = new JLabel[48];
        hourDetailLabel = new JLabel[48];
        myfont = DroidSans.load(16);
        c.gridwidth = 1; c.gridheight = 1;
        c.insets = new InsetsUIResource(0, 8, 0, 8);
        c.anchor = GridBagConstraints.CENTER;

        for(int i = 0; i < 12; i++) {

            c.gridy = 5; c.gridx = i;
            hourIconLabel[i] = new JLabel();
            hourIconLabel[i].setFont(myfont);
            add(hourIconLabel[i], c);

            c.gridy = 6; c.gridx = i;
            hourDetailLabel[i] = new JLabel();
            hourDetailLabel[i].setFont(myfont);
            c.insets = new InsetsUIResource(0, 8, 8, 8);
            add(hourDetailLabel[i], c);
            c.insets = new InsetsUIResource(0, 8, 0, 8);

        }

        c.insets = IN0;
        c.gridy = 0; c.gridx = 6;
        c.gridwidth = 6; c.gridheight = 5;
        add(settingsPanel, c);
        // c.fill = GridBagConstraints.BOTH;
        // c.ipady = 48;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.insets = new InsetsUIResource(0, 0, 24, 0);
        add(dayPanel, c);

    }


    public void refreshContent(YawaUI yawaUI) {

        String text;
        occ = yawaUI.getYawa().getOCC();
        settings = yawaUI.getYawa().getSettings();

        if(!yawaUI.getYawa().isReady() || settings.name.length()<=0) { return; }

        dayPanel.setAlert();
        
        yawaUI.setTitle("Yet Another Weather App - "+occ.current.getDt()
                .format(DateTimeFormatter.ofPattern("dd.MM. HH:mm")));
        
        locationButton.setText(settings.name+", "+
            settings.country+((settings.state != null)?(", "+settings.state):""));
        locationButton.setVisible(true);

        currentIconLabel.setIcon(new WeatherIcon(occ.current.getWeather()[0].getId(),
            isDaylight(occ.current.getDt()), 0));
        currentIconLabel.setToolTipText(occ.current.getWeather()[0].getDescription());
        

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

        int start = dailyPage * 12;
        for(int i = 0; i < 12; i++) {
            OneCallContainer.Hourly hour = occ.hourly[start+i];
            hourIconLabel[i].setIcon(new WeatherIcon(hour.getWeather()[0].getId(),
                isDaylight(hour.getHour()), 1));

            String ttString = hour.getHour().format(DateTimeFormatter.ofPattern("cccc"));
            ttString += " "+hour.getHour().format(DateTimeFormatter.ofPattern("HH"))+" Uhr: ";
            hourIconLabel[i].setToolTipText(ttString+hour.getWeather()[0].getDescription());

            Color color = dailyPage > 1 ? Color.yellow : Color.white;
            hourIconLabel[i].setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                hour.getHour().format(DateTimeFormatter.ofPattern("HH:mm")),
                TitledBorder.LEFT, TitledBorder.TOP,
                DroidSans.load(14),
                color));
            
            hourDetailLabel[i].setText("<html><div style=text-align:center><big>"+
                hour.getTemp()+"</big> <small>°C</small><br>"+
                hour.getWindSpeed()+" <small>km/h</small><br>"+
                hour.getPop()+" <small>%</small></div></html>");
        }

        // yawaUI.pack();
        // yawaUI.setSize(yawaUI.getWidth(), yawaUI.getHeight());
        // yawaUI.setMinimumSize(new DimensionUIResource(yawaUI.getWidth(), yawaUI.getHeight()));

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


    private boolean isDaylight(LocalDateTime t) {

        LocalTime sunrise = occ.current.getSunrise().toLocalTime();
        LocalTime sunset = occ.current.getSunset().toLocalTime();
        
        return (t.toLocalTime().isAfter(sunrise) && t.toLocalTime().isBefore(sunset));
    }


    public int getDailyPage() {
        return dailyPage;
    }


    public void setDailyPage(int page) {
        dailyPage = page;
    }

}
