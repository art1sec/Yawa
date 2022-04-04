package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;

import model.OneCallContainer;

public class DayPanel extends JPanel{
    
    private JLabel[] dayIconLabel;

    public DayPanel(YawaUI yawaUI) {

        super();
        setLayout(new GridBagLayout());

        JButton toggleButton = new JButton("o o o");
        toggleButton.setOpaque(true);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusable(false);
        toggleButton.setFont(DroidSans.load(24));
        toggleButton.setToolTipText("toggle forecast panel");
        add(toggleButton);

        toggleButton.addActionListener(l -> {
            yawaUI.getMainPanel().setDailyPage(yawaUI.getMainPanel().getDailyPage()>0?0:1);
            yawaUI.refreshContent();
        });

        // /*
        //  * 6 daily forecasts in one row
        // */
        // dayIconLabel = new JLabel[6];
        // // myfont = DroidSans.load(16);
        // // c.insets = new InsetsUIResource(0, 8, 0, 8);

        // for(int i=0; i<4; i++) {
        //     dayIconLabel[i] = new JLabel();
        //     // hourIconLabel[i].setFont(myfont);
        //     // c.anchor = GridBagConstraints.CENTER;
        //     add(dayIconLabel[i]);
        // }

    }


    // public void refreshContent(OneCallContainer occ) {
        
    //     for(int i=0; i<4; i++) {

    //         OneCallContainer.Daily day = occ.daily[i];
    //         dayIconLabel[i].setIcon(new WeatherIcon(day.getWeather()[0].getId(), true, 1));

    //         // System.out.println(day.getWeather()[0].getId()+": "+
    //         //     day.getWeather()[0].getDescription());
            
    //         dayIconLabel[i].setBorder(BorderFactory.createTitledBorder(
    //             BorderFactory.createEmptyBorder(),
    //             day.getDt().format(DateTimeFormatter.ofPattern("ccc")),
    //             TitledBorder.LEFT, TitledBorder.TOP,
    //             DroidSans.load(12),
    //             Color.white));
            
    //         // hourDetailLabel[i].setText("<html><div style=text-align:center><big>"+
    //         //     hour.getTemp()+"</big> <small>°C</small><br>"+
    //         //     hour.getWindSpeed()+" <small>km/h</small><br>"+
    //         //     hour.getPop()+" <small>%</small></div></html>");
    //     }

    // }

}
