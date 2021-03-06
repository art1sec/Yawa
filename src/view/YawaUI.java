package view;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;

import controller.Yawa;

public class YawaUI extends JFrame {

    private Yawa yawa;

    private DayPanel dayPanel;
    private MainPanel mainPanel;
    private SettingsPanel settingsPanel;
    
    public YawaUI(Yawa yawa) {

        this.yawa = yawa;

        UIManager.getLookAndFeelDefaults().put("Panel.font", DroidSans.load(14));
        UIManager.getLookAndFeelDefaults().put("Label.font", DroidSans.load(14));
        UIManager.getLookAndFeelDefaults().put("TextArea.font", DroidSans.load(14));
        UIManager.getLookAndFeelDefaults().put("TextField.font", DroidSans.load(14));
        // UIManager.getLookAndFeelDefaults().put("ComboBox.font", DroidSans.load(14));

        UIManager.getLookAndFeelDefaults().put("Button.font", DroidSans.load(16));
        UIManager.getLookAndFeelDefaults().put("ToolTip.font", DroidSans.load(16));

        UIManager.getLookAndFeelDefaults().put("Panel.background", new ColorUIResource(42, 42, 42));
        UIManager.getLookAndFeelDefaults().put("Panel.foreground", Color.white);

        UIManager.getLookAndFeelDefaults().put("Label.background", new ColorUIResource(42, 42, 42));
        UIManager.getLookAndFeelDefaults().put("Label.foreground", Color.white);

        UIManager.getLookAndFeelDefaults().put("TextArea.background", new ColorUIResource(42, 42, 42));
        UIManager.getLookAndFeelDefaults().put("TextArea.foreground", Color.white);

        UIManager.getLookAndFeelDefaults().put("Button.background", Color.darkGray);
        UIManager.getLookAndFeelDefaults().put("Button.foreground", Color.white);

        UIManager.getLookAndFeelDefaults().put("ToolTip.background", Color.white);
        UIManager.getLookAndFeelDefaults().put("ToolTip.foreground", Color.darkGray);

    }


    public void createAndShowGUI() {

        boolean displaySettingsPanel = (yawa.getSettings().getName() == null) ? true : false;

        settingsPanel = new SettingsPanel(this);
        settingsPanel.setVisible(displaySettingsPanel);

        dayPanel = new DayPanel(this);
        // dayPanel.refreshContent(yawa.getOCC());
        dayPanel.setVisible(!displaySettingsPanel);

        mainPanel = new MainPanel(settingsPanel, dayPanel, !displaySettingsPanel);
        mainPanel.refreshContent(this);
        add(mainPanel);

        pack();
        setMinimumSize(new DimensionUIResource(getWidth(), getHeight()+8));
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }


    public Yawa getYawa() {
        return yawa;
    }


    public MainPanel getMainPanel() {
        return mainPanel;
    }


    public void refreshContent() {
        mainPanel.refreshContent(this);
        // dayPanel.refreshContent(yawa.getOCC());
    }


    public void setDayPanelVisible(boolean b) {
        dayPanel.setVisible(b);
    }

}
