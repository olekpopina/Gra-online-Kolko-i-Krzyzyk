package kolkoikrzyzyk;

import javax.swing.*;

public class KolkoIKrzyzykMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager.initializeDatabase();
            new StartScreen(null); // Користувач не залогінений за замовчуванням
        });
    }
}
