package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @brief Panel tła dla aplikacji Kółko i Krzyżyk.
 *
 * Klasa odpowiedzialna za wyświetlanie obrazu tła lub domyślnego tła
 * w przypadku, gdy obraz nie może zostać załadowany.
 */
public class BackgroundPanel extends JPanel {
    /**
     * @brief Obraz tła do wyświetlenia w panelu.
     */
    private final BufferedImage backgroundImage;

    /**
     * @brief Konstruktor klasy BackgroundPanel.
     *
     * Próbuje załadować obraz tła na podstawie podanej ścieżki.
     *
     * @param path Ścieżka do obrazu tła.
     */
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

    /**
     * @brief Nadpisana metoda rysująca komponent.
     *
     * Rysuje obraz tła lub wyświetla czarny ekran z komunikatem o błędzie,
     * jeśli obraz nie został załadowany.
     *
     * @param g Obiekt Graphics używany do rysowania.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK); ///< Czarny kolor jako rezerwowy.
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED); ///< Czerwony kolor dla tekstu błędu.
            g.drawString("Brak obrazu tła", getWidth() / 2 - 50, getHeight() / 2);
        }
    }
}
