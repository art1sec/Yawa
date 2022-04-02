package view;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;

import controller.Yawa;

public class YawaUI {

    private Yawa yawa;

    private JFrame frame;
    private MainPanel mainPanel;
    private SettingsPanel settingsPanel;
    
    public YawaUI(Yawa yawa) {

        this.yawa = yawa;

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

    }


    public void createAndShowGUI() {

        settingsPanel = new SettingsPanel(this, yawa);
        settingsPanel.setVisible(!yawa.isReady());

        mainPanel = new MainPanel(settingsPanel, yawa.isReady());
        mainPanel.refreshContent(frame, yawa);
        frame.add(mainPanel);

        frame.pack();
        frame.setSize(frame.getWidth(), frame.getHeight());
        frame.setMinimumSize(new DimensionUIResource(frame.getWidth(), frame.getHeight()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }


    public void refreshContent(Yawa yawa) {
        mainPanel.refreshContent(frame, yawa);
    }

}
