package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundPanel extends JPanel {
    private final BufferedImage backgroundImage;

    public BackgroundPanel(String path) {
        BufferedImage tempImage = null;
        try {
            tempImage = ResourceLoader.loadImage(path);
        } catch (Exception e) {
            System.err.println("Nie udało się załadować tła: " + path);
            e.printStackTrace();
        }
        this.backgroundImage = tempImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK); // Чорний фон як резервний варіант
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.drawString("Brak obrazu tła", getWidth() / 2 - 50, getHeight() / 2);
        }
    }
}
