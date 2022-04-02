
import java.io.IOException;

import controller.Yawa;
import view.YawaUI;

public class App {

    public static void main(String[] args) throws IOException {

        Yawa yawa = new Yawa();
        YawaUI yawaUI = new YawaUI(yawa);
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    yawaUI.createAndShowGUI();
                }
            }
        );

    }
}