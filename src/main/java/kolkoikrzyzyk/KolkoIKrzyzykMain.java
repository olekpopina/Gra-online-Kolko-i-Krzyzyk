package kolkoikrzyzyk;

import javax.swing.*;

/**
 * @brief Główna klasa aplikacji "Kółko i Krzyżyk".
 *
 * @details Klasa zawiera metodę główną, która uruchamia aplikację.
 * Inicjalizuje bazę danych i otwiera ekran startowy.
 */
public class KolkoIKrzyzykMain {

    /**
     * @brief Punkt wejściowy aplikacji.
     *
     * @param args Argumenty wiersza poleceń (nie są używane).
     *
     * @details Metoda główna uruchamia aplikację w kontekście wątku graficznego Swing.
     * Inicjalizuje bazę danych za pomocą {@link DatabaseManager#initializeDatabase()}
     * oraz otwiera ekran startowy za pomocą {@link StartScreen}.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager.initializeDatabase(); ///< Inicjalizacja bazy danych.
            new StartScreen(null); ///< Otwarcie ekranu startowego bez zalogowanego użytkownika.
        });
    }
}
