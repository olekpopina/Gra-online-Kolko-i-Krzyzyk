package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;

public class KolkoIKrzyzykMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager.initializeDatabase(); // Створення бази
            new StartScreen();
        });
    }
}
