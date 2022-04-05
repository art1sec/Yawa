package view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.InsetsUIResource;

import model.OneCallContainer;

public class DayPanel extends JPanel{

    private int alertPage;
    private YawaUI yawaUI;
    private JButton warning;

    public DayPanel(YawaUI yawaUI) {

        super();
        this.yawaUI = yawaUI;
        setLayout(new GridBagLayout());
        GridBagConstraints dc = new GridBagConstraints();
        // setBorder(BorderFactory.createLineBorder(Color.white));
        // dc.ipadx = 0; dc.ipady = 0;
        // setAlignmentY(JPanel.BOTTOM_ALIGNMENT);

        dc.gridx = 0; dc.gridy = 0;
        dc.gridwidth = 1; dc.gridheight = 2;
        dc.anchor = GridBagConstraints.WEST;
        warning = new JButton();
        warning.setContentAreaFilled(false);
        warning.setBorderPainted(false);
        warning.setFocusable(false);
        warning.setMargin(new InsetsUIResource(0, 0, 0, 0));
        warning.setFont(DroidSans.load(14));
        warning.setForeground(Color.lightGray);
        add(warning, dc);

        setAlert();

        dc.gridx = 1; dc.gridy = 1;
        dc.gridwidth = 1; dc.gridheight = 1;
        dc.anchor = GridBagConstraints.SOUTH;
        JButton toggleButton = new JButton("o o o");
        toggleButton.setOpaque(true);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusable(false);
        toggleButton.setFont(DroidSans.load(24));
        toggleButton.setToolTipText("toggle forecast panel");
        // toggleButton.setBorder(BorderFactory.createLineBorder(Color.white));
        add(toggleButton, dc);

        toggleButton.addActionListener(l -> {
            // yawaUI.getMainPanel().setDailyPage(yawaUI.getMainPanel().getDailyPage()>0?0:1);
            int page = yawaUI.getMainPanel().getDailyPage() + 1;
            page = page > 3 ? 0 : page;
            yawaUI.getMainPanel().setDailyPage(page);
            yawaUI.refreshContent();
        });

        warning.addActionListener(l -> {
            alertPage++;
            if(alertPage >= yawaUI.getYawa().getOCC().alerts.length) {
                alertPage = 0;
            }
            setAlert();
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


    public void setAlert() {
        warning.setText(null);

        if(yawaUI.getYawa().isReady() && yawaUI.getYawa().getOCC().alerts != null) {
            String text = "<html><body width=\"240px\" align=\"left\"><p><b>";
            OneCallContainer.Alerts a = yawaUI.getYawa().getOCC().alerts[alertPage];

            // if(!a.getEvent().toUpperCase().equals(a.getEvent())) continue;
            text += a.getEvent()+":  "+
                a.getStart().format(DateTimeFormatter.ofPattern("ccc HH:mm"))+" - "+
                a.getEnd().format(DateTimeFormatter.ofPattern("ccc HH:mm"))+
                "</b></p><p>"+a.getDescription()+"</p></body></html>";
            
            warning.setText(text);
            
        }
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
