package view;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class DroidSans {

    public static Font load(int size, boolean bold) {
        try {

            var inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("res/fonts/DroidSans"+(bold?"-Bold":"")+".ttf");
            if (inputStream == null) {
                throw new IOException("Cannot load font from resource");
            }
            var font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            return bold?font.deriveFont(Font.BOLD, size):font.deriveFont(Font.PLAIN, size);
        } catch (IOException | FontFormatException e) {
            System.out.println("Unable to use embedded font, using fallback");
            e.printStackTrace();
            return bold?new Font("Sans", Font.BOLD, size):new Font("Sans", Font.PLAIN, size);
        }
    }

    public static Font load(int size) {
        return load(size, false);
    }
}
