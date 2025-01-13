/**
 * @file GameBase.java
 * @brief Abstrakcyjna klasa bazowa dla gry Kółko i Krzyżyk.
 *
 * Klasa definiuje podstawowe mechanizmy gry, takie jak interfejs użytkownika, zarządzanie stanem gry,
 * oraz obsługę różnych trybów gry.
 */
package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @class GameBase
 * @brief Abstrakcyjna klasa bazowa implementująca wspólne funkcje gry Kółko i Krzyżyk.
 */
public abstract class GameBase extends JFrame implements GameMode {
    /**
     * @brief Przyciski reprezentujące planszę gry 3x3.
     */
    protected final JButton[][] buttons = new JButton[3][3];
    /**
     * @brief Tablica przechowująca aktualny stan gry.
     * Wartości: "X", "O" lub null (dla pustej komórki).
     */
    protected final String[][] gameState = new String[3][3];
    /**
     * @brief Obrazek symbolu X.
     */
    protected BufferedImage obrazekX = ResourceLoader.loadImage("/images/x.png");
    /**
     * @brief Obrazek symbolu O.
     */
    protected BufferedImage obrazekO = ResourceLoader.loadImage("/images/o.png");
    /**
     * @brief Flaga określająca, czy gracz wraca do menu.
     */
    protected boolean isReturningToMenu = false;
    /**
     * @brief Zalogowany użytkownik.
     */
    protected String loggedInUser = null;
    /**
     * @brief Symbol gracza (domyślnie "X").
     */
    protected String playerSymbol = "X";
    /**
     * @brief Tryb gry: "local", "vs_bot" lub "online".
     */
    private String gameMode = "local"; // Можливо значення: "local", "vs_bot", "online"
    /**
     * @brief Konstruktor tworzący okno gry z domyślnym trybem lokalnym.
     * @param title Tytuł okna.
     */
    public GameBase(String title) {
        // Inicjalizacja okna
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Początkowy rozmiar okna
        int windowSize = 600;
        setSize(windowSize, windowSize);
        setMinimumSize(new Dimension(400, 400));

        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo.png");
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3, 0, 20));
        gamePanel.setOpaque(false);

        initializeButtons(gamePanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20);
        backgroundPanel.add(gamePanel, gbc);


        int gridSize = (int) (windowSize * 0.65); // Rozmiar planszy
        gamePanel.setPreferredSize(new Dimension(gridSize, gridSize));
        updateButtonSizes(gridSize / 3);
        addEscKeyListener();

        setVisible(true);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setGridSize(gamePanel);
            }
        });
    }

    /**
     * @brief Konstruktor tworzący okno gry z określonym trybem i symbolem gracza.
     * @param title Tytuł okna.
     * @param gameMode Wybrany tryb gry.
     * @param playerSymbol Symbol przypisany do gracza.
     */
    public GameBase(String title, String gameMode, String playerSymbol) {
        this(title);
        this.gameMode = gameMode;
        this.playerSymbol = playerSymbol;
    }
    /**
     * @brief Ustawia zalogowanego użytkownika.
     * @param username Nazwa użytkownika.
     */
    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
    }
    /**
     * @brief Inicjalizuje przyciski na planszy gry.
     * @param panel Panel, do którego dodawane są przyciski.
     */
    private void initializeButtons(JPanel panel) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setOpaque(false);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorderPainted(false);
                buttons[i][j].setEnabled(true);
                buttons[i][j].setText("");
                buttons[i][j].setIcon(null);
                gameState[i][j] = null;
                int finalI = i;
                int finalJ = j;
                buttons[i][j].addActionListener(e -> makeMove(finalI, finalJ));
                panel.add(buttons[i][j]);
            }
        }
    }

    /**
     * @brief Ustawia rozmiar planszy gry na podstawie rozmiaru okna.
     * @param gamePanel Panel zawierający planszę gry.
     */
    private void setGridSize(JPanel gamePanel) {
        int windowWidth = getWidth();
        int windowHeight = getHeight();

        int gridSize = (int) (Math.min(windowWidth, windowHeight) * 0.65);
        gamePanel.setPreferredSize(new Dimension(gridSize, gridSize));

        updateButtonSizes(gridSize / 3);
        revalidate();
        repaint();
    }

    /**
     * @brief Aktualizuje rozmiar przycisków na planszy.
     * @param buttonSize Nowy rozmiar przycisków.
     */
    private void updateButtonSizes(int buttonSize) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setPreferredSize(new Dimension(buttonSize, buttonSize));
                String player = gameState[i][j];
                if (player != null) {
                    buttons[i][j].setIcon(getPlayerIcon(player, buttonSize));
                }
            }
        }
    }

    /**
     * @brief Aktualizuje tytuł okna, dodając nazwę zalogowanego użytkownika.
     */
    public void updateWindowTitle() {
        if (loggedInUser != null) {
            setTitle(getTitle() + " - Zalogowano jako: " + loggedInUser);
        }
    }
    /**
     * @brief Pobiera ikonę odpowiadającą symbolowi gracza.
     * @param player Symbol gracza ("X" lub "O").
     * @param buttonSize Rozmiar przycisku, do którego ikona ma być dopasowana.
     * @return Ikona gracza jako obiekt ImageIcon.
     */
    protected ImageIcon getPlayerIcon(String player, int buttonSize) {
        if (player == null || player.isEmpty()) return null;

        BufferedImage image = "X".equals(player) ? obrazekX : obrazekO;
        if (image == null) return null;

        int scaledSize = (int) (buttonSize * 0.8);
        Image scaledImage = image.getScaledInstance(scaledSize, scaledSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * @brief Resetuje grę, czyszcząc planszę i stan gry.
     *
     * Jeśli użytkownik jest zalogowany, aktualizuje statystyki w bazie danych
     * w oparciu o wynik ostatniej gry.
     *
     * @note Metoda wykonuje reset gry, przywracając wszystkie pola do stanu początkowego.
     */
    @Override
    public void resetGame() {
        if (loggedInUser != null) {
            String winner = GameLogic.getWinner(gameState);
            boolean isDraw = isBoardFull() && winner == null;

            if (!isDraw) {
                boolean win = Objects.equals(winner, playerSymbol);
                DatabaseManager.updateUserStats(loggedInUser, win, gameMode);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setIcon(null);
                buttons[i][j].setEnabled(true);
                gameState[i][j] = null;
            }
        }

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    /**
     * @brief Sprawdza, czy gra zakończyła się wygraną lub remisem.
     *
     * @return true, jeśli gra się zakończyła (wygrana lub remis), false w przeciwnym razie.
     *
     * @details Wyświetla komunikaty dla gracza o wyniku gry. Jeśli gra się zakończyła, resetuje planszę.
     */
    protected boolean checkGameStatus() {
        String winner = GameLogic.getWinner(gameState);
        boolean isDraw = isBoardFull() && winner == null;

        if (winner != null) {
            JOptionPane.showMessageDialog(this, "Wygrywa gracz: " + winner + "!", "Wynik gry", JOptionPane.INFORMATION_MESSAGE);
        } else if (isDraw) {
            JOptionPane.showMessageDialog(this, "Remis!", "Wynik gry", JOptionPane.INFORMATION_MESSAGE);
        } else {
            return false;
        }
        resetGame();
        return true;
    }
    /**
     * @brief Sprawdza, czy wszystkie pola na planszy są zajęte.
     *
     * @return true, jeśli plansza jest pełna, false w przeciwnym razie.
     */
    private boolean isBoardFull() {
        for (String[] row : gameState) {
            for (String cell : row) {
                if (cell == null) return false;
            }
        }
        return true;
    }
    /**
     * @brief Wyświetla komunikat o błędzie w oknie dialogowym.
     *
     * @param message Treść komunikatu błędu.
     */
    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Błąd", JOptionPane.ERROR_MESSAGE);
    }
    /**
     * @brief Wyświetla aktualny stan planszy w konsoli.
     *
     * @details Metoda drukuje stan gry w formacie tekstowym, gdzie puste pola
     * są reprezentowane kropkami, a symbole graczy jako "X" i "O".
     */
    void printGameState() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(gameState[i][j] == null ? "." : gameState[i][j]);
            }
            System.out.println();
        }
    }
    /**
     * @brief Powraca do głównego menu gry po potwierdzeniu przez użytkownika.
     *
     * @details Wyświetla okno dialogowe z pytaniem o potwierdzenie. Jeśli użytkownik zaakceptuje,
     * zamyka aktualne okno gry i otwiera ekran startowy. Jeśli użytkownik odmówi, pozostaje w grze.
     */
    protected void returnToMainMenu() {
        if (isReturningToMenu) return;
        isReturningToMenu = true;

        int option = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz wrócić do głównego menu?",
                "Powrót do menu", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            new StartScreen(loggedInUser);
        } else {
            isReturningToMenu = false;
        }
    }
    /**
     * @brief Dodaje obsługę klawisza ESC do powrotu do menu głównego.
     *
     * @details Przypisuje akcję powrotu do głównego menu do klawisza ESCAPE,
     * która jest wykonywana w kontekście aktywnego okna gry.
     */
    private void addEscKeyListener() {
        JRootPane rootPane = this.getRootPane();

        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "returnToMainMenu");
        actionMap.put("returnToMainMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu();
            }
        });
    }
}