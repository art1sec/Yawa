package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import controller.Yawa;
import model.DroidSans;
import owm.OneCallContainer;

public class YawaUI_old {
    
    private JFrame frame;
    private JPanel currentPanel;
    private JPanel[][] hourPanel = new JPanel[3][4];
    private OneCallContainer occ;

    public YawaUI_old(Yawa yawa) {

        UIManager.getLookAndFeelDefaults().put("Label.font", DroidSans.load(24, true));
        UIManager.getLookAndFeelDefaults().put("Panel.font", DroidSans.load(24, true));
        // UIManager.put("Panel.font", LiberationFont.loadFont(24, true));
        // UIManager.put("Label.font", LiberationFont.loadFont(24, true));
        occ = yawa.getOCC();
        frame = new JFrame("Yet Another Weather App - "+occ.current.getDt().format(DateTimeFormatter.ofPattern("dd.MM. HH:mm")));
        currentPanel = new JPanel();
        currentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 48, 0));
        currentPanel.setBackground(new ColorUIResource(191, 191, 207));
        currentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createBevelBorder(BevelBorder.LOWERED),
            BorderFactory.createTitledBorder("occ.current.getDt()")));
        
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.PAGE_AXIS));
        tempPanel.setBackground(new ColorUIResource(191, 191, 207));
        JLabel currentTemp = new JLabel(occ.current.getTemp()+" °C");
        Font myfont = DroidSans.load(48, true);
        // currentTemp.setFont(new Font("Sans", Font.BOLD, 48));
        currentTemp.setFont(myfont);
        tempPanel.add(currentTemp);
        HeadDescLabel cd = new HeadDescLabel(occ.current.getWeather()[0].getDescription());
        tempPanel.add(cd);
        currentPanel.add(tempPanel);

        java.net.URL ci = getClass().getResource("/res/icons/"+occ.current.getWeather()[0].getIcon()+"@2x.png");
        JLabel currentIconLabel = new JLabel(new ImageIcon(ci));
        currentPanel.add(currentIconLabel);

        JPanel curDetails = new JPanel();
        curDetails.setLayout(new BoxLayout(curDetails, BoxLayout.PAGE_AXIS));
        // curDetails.setBackground(new ColorUIResource(191, 191, 255));
        curDetails.setBackground(new ColorUIResource(191, 191, 207));

        HeadLabel fl = new HeadLabel("Gefühlt "+occ.current.getFeelsLike()+" °C");
        curDetails.add(fl);
        HeadLabel hl = new HeadLabel("Feuchtigkeit "+occ.current.getHumidity()+" %");
        curDetails.add(hl);
        HeadLabel ws = new HeadLabel("Wind: "+occ.current.getWindSpeed()+" km/h, "+
            occ.current.getWindDeg()+" °");
        curDetails.add(ws);
        HeadLabel wg = new HeadLabel("Böen: "+occ.current.getWindGust()+" km/h");
        curDetails.add(wg);

        currentPanel.add(curDetails);

        for(int i=0; i<3; i++) {

            for(int j=0; j<4; j++) {

                OneCallContainer.Hourly hour = occ.hourly[i*4+j];

                hourPanel[i][j] = new JPanel();
                hourPanel[i][j].setBackground(new ColorUIResource(191, 191, 223));
                hourPanel[i][j].setLayout(new BorderLayout());
                
                LocalDateTime dt = hour.getHour();
                Border insideBorder = BorderFactory.createTitledBorder(dt.format(DateTimeFormatter.ofPattern("HH:mm"))+":");
                Border outsideBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
                hourPanel[i][j].setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));
                
                java.net.URL icon = getClass().getResource("/res/icons/"+hour.getWeather()[0].getIcon()+"@2x.png");
                JLabel iconLabel = new JLabel(new ImageIcon(icon));
                hourPanel[i][j].add(iconLabel, BorderLayout.WEST);

                JLabel tempLabel = new JLabel(Math.round(hour.getTemp())+" °C");
                tempLabel.setFont(new Font("Sans", Font.BOLD, 32));
                hourPanel[i][j].add(tempLabel, BorderLayout.EAST);

                JPanel details = new JPanel();
                details.setLayout(new BoxLayout(details, BoxLayout.PAGE_AXIS));
                details.setBackground(new ColorUIResource(223, 223, 223));

                DescLabel descLabel = new DescLabel(hour.getWeather()[0].getDescription());
                descLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
                details.add(descLabel);

                DescLabel feelsLikeLabel = new DescLabel("Gefühlt "+Math.round(hour.getFeelsLike())+" °C");
                feelsLikeLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
                details.add(feelsLikeLabel);

                DescLabel humLabel = new DescLabel("Feuchtigkeit "+hour.getHumidity()+" %");
                humLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
                details.add(humLabel);
                
                DescLabel windSpeedLabel = new DescLabel("Wind: "+Math.round(hour.getWindSpeed())+
                    " km/h, "+hour.getWindDeg()+" °");
                windSpeedLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
                details.add(windSpeedLabel);

                DescLabel windGustLabel = new DescLabel("Böen: "+Math.round(hour.getWindGust())+" km/h");
                windGustLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
                details.add(windGustLabel);
                
                hourPanel[i][j].add(details, BorderLayout.SOUTH);

            }
        }
    }

    public void createAndShowGUI() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.gridx = 0; c.gridy = 0;
        c.gridwidth = 4;
        frame.add(currentPanel, c);
        c.gridwidth = 1;

        c.ipadx = 24; // c.ipady = 12;

        for(int i=0; i<3; i++) {
            for(int j=0; j<4; j++) {
                c.gridx = j; c.gridy = 1+i;
                frame.add(hourPanel[i][j], c);
            }
        }

        setLookAndFeel();
        // frame.getContentPane().setBackground(Color.red);
        // SwingUtilities.updateComponentTreeUI(frame);
        
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(frame.getSize());
        frame.setResizable(false);

    }


    public void setLookAndFeel() {

        try {
            if (System.getProperty("os.name").startsWith("Linuxxx")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } else {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            }
            // SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | InstantiationException | 
            IllegalAccessException | UnsupportedLookAndFeelException cnfe) {
            // pass
        }
    }
}
