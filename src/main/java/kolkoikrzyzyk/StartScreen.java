package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @brief Ekran startowy gry "Kółko i Krzyżyk".
 *
 * @details Klasa odpowiedzialna za wyświetlanie ekranu startowego, na którym użytkownik może
 * zobaczyć swoje dane (jeśli jest zalogowany), najlepszych graczy oraz wybrać opcje gry.
 *
 * @note Ekran wyświetla informacje o zalogowanym użytkowniku oraz najlepszych graczach w grze.
 */
public class StartScreen extends JFrame {
    private String loggedInUser; ///< Zmienna przechowująca imię zalogowanego użytkownika

    /**
     * @brief Konstruktor ekranu startowego.
     *
     * @details Inicjalizuje ekran startowy z odpowiednim tytułem, który zawiera imię zalogowanego użytkownika.
     * Jeśli użytkownik nie jest zalogowany, wyświetla się "Gość".
     *
     * @param username Nazwa użytkownika, który jest zalogowany.
     */
    public StartScreen(String username) {
        this.loggedInUser = username; // Imię zalogowanego użytkownika
        setTitle("Kółko i Krzyżyk - Zalogowano jako: " + (username != null ? username : "Gość"));
        initializeUI();  // Inicjalizacja interfejsu użytkownika
    }
    /**
     * @brief Inicjalizuje komponenty graficzne ekranu startowego.
     *
     * @details Ustawia okno gry (wielkość, tytuł, pozycję) oraz komponenty takie jak tło,
     * panele z informacjami o użytkowniku i najlepszych graczach, oraz przyciski do interakcji.
     */
    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        // Ustawienie tła ekranu startowego
        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/StartScreen.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Górna panel: logowanie użytkownika i top-10 graczy
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel userPanel = createUserPanel();// Tworzy panel z informacjami o użytkowniku
        JPanel topPlayersPanel = createTopPlayersPanel();// Tworzy panel z najlepszymi graczami

        topPanel.add(userPanel);// Dodanie panelu użytkownika
        topPanel.add(topPlayersPanel);// Dodanie panelu użytkownika

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        // Panel z przyciskami
        JPanel buttonPanel = createButtonPanel();
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // Ustawienie paska menu
        setJMenuBar(createMenuBar());

        setVisible(true);// Ustawienie widoczności okna
    }

    /**
     * @brief Tworzy panel z informacjami o użytkowniku.
     *
     * @details Panel wyświetla nazwę zalogowanego użytkownika lub informację o gościu.
     * Używa niestandardowego rysowania tła z przezroczystością.
     *
     * @return Zwraca stworzony panel z informacjami o użytkowniku.
     */
    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 150)); // Czarny kolor z 150 poziomem przezroczystości (0-255)
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        userPanel.setOpaque(false); // Ustawienie przezroczystości panelu
        JLabel userLabel = new JLabel("Zalogowano jako: " + (loggedInUser != null ? loggedInUser : "Gość"));
        userLabel.setFont(new Font("Arial", Font.BOLD, 22));
        userLabel.setForeground(Color.YELLOW);// Kolor tekstu na żółto
        userPanel.add(userLabel); // Dodanie etykiety do panelu
        return userPanel;// Zwrócenie panelu z informacjami o użytkowniku
    }

    /**
     * @brief Tworzy panel z listą najlepszych graczy.
     *
     * @details Panel wyświetla listę najlepszych graczy, pobraną z bazy danych, z tytułem "Najlepsi gracze".
     * Dla każdego gracza wyświetlana jest etykieta z jego nazwą. Panel jest wyświetlany na ekranie startowym.
     *
     * @return Zwraca panel zawierający listę najlepszych graczy.
     */
    private JPanel createTopPlayersPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Najlepsi gracze:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        // Pobieramy listę najlepszych graczy z bazy danych
        List<String> topPlayers = DatabaseManager.getTopPlayers();
        for (String player : topPlayers) {
            JLabel playerLabel = new JLabel(player);
            playerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            playerLabel.setForeground(Color.WHITE);
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(playerLabel);
        }
        return panel;
    }

    /**
     * @brief Tworzy panel z przyciskami do wyboru różnych opcji gry.
     *
     * @details Panel zawiera przyciski, które umożliwiają użytkownikowi rozpoczęcie gry na różnych trybach:
     * lokalna gra, gra przeciwko komputerowi, gra jako serwer lub klient. Dodatkowo umożliwia logowanie, wylogowanie
     * oraz wyjście z gry. Każdy przycisk jest powiązany z odpowiednią akcją.
     *
     * @return Zwraca panel z przyciskami.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Przycisk do gry na jednym komputerze
        JButton localGameButton = createStyledButton("Gra na jednym komputerze");
        gbc.gridy = 0;
        panel.add(localGameButton, gbc);
        localGameButton.addActionListener(e -> {
            dispose();
            LocalGame game = new LocalGame();
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });

        // Przycisk do gry przeciwko komputerowi
        JButton vsComputerButton = createStyledButton("Gra przeciwko komputerowi");
        gbc.gridy = 1;
        panel.add(vsComputerButton, gbc);
        vsComputerButton.addActionListener(e -> {
            dispose();
            VsComputerGame game = new VsComputerGame();
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });

        // Przycisk do gry jako serwer
        JButton serverButton = createStyledButton("Gra jako serwer");
        gbc.gridy = 2;
        panel.add(serverButton, gbc);
        serverButton.addActionListener(e -> {
            dispose();
            ServerGame game = new ServerGame();
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });

        // Przycisk do gry jako klient
        JButton clientButton = createStyledButton("Gra jako klient");
        gbc.gridy = 3;
        panel.add(clientButton, gbc);
        clientButton.addActionListener(e -> {
            String ip = JOptionPane.showInputDialog("Podaj IP serwera:");
            dispose();
            ClientGame game = new ClientGame(ip);
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });
        // Przycisk do logowania
        JButton loginButton = createStyledButton("Zaloguj się");
        gbc.gridy = 4;
        panel.add(loginButton, gbc);
        loginButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Podaj nazwę użytkownika:");
            String password = JOptionPane.showInputDialog("Podaj hasło:");
            if (DatabaseManager.authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Zalogowano jako: " + username);
                dispose();
                new StartScreen(username);
            } else {
                JOptionPane.showMessageDialog(this, "Nieprawidłowy login lub hasło!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Przycisk do wylogowania
        JButton logoutButton = createStyledButton("Wyloguj się");
        gbc.gridy = 5;
        panel.add(logoutButton, gbc);
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Wylogowano!");
            dispose();
            new StartScreen(null);
        });
        // Przycisk do wyjścia z gry
        JButton exitButton = createStyledButton("Wyjście");
        gbc.gridy = 6;
        panel.add(exitButton, gbc);
        exitButton.addActionListener(e -> System.exit(0));

        return panel;
    }

    /**
     * @brief Tworzy pasek menu z opcjami dla użytkownika.
     *
     * @details Pasek menu zawiera opcje związane z zarządzaniem użytkownikiem: rejestracja, zmiana hasła,
     * pokazanie statystyk. Opcje zmiany hasła i pokazania statystyk są dostępne tylko po zalogowaniu.
     *
     * @return Zwraca pasek menu dla aplikacji.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu userMenu = new JMenu("Korzystnik");

        // Opcja do rejestracji nowego użytkownika
        JMenuItem registerMenuItem = new JMenuItem("Zarejestruj nowego użytkownika");
        registerMenuItem.addActionListener(e -> showRegisterDialog());
        userMenu.add(registerMenuItem);

        // Opcja zmiany hasła
        JMenuItem changePasswordMenuItem = new JMenuItem("Zmień hasło");
        changePasswordMenuItem.setEnabled(loggedInUser != null); // Tylko po zalogowaniu
        changePasswordMenuItem.addActionListener(e -> showChangePasswordDialog());
        userMenu.add(changePasswordMenuItem);

        // Opcja pokazania statystyk użytkownika
        JMenuItem viewStatsMenuItem = new JMenuItem("Pokaż statystyki");
        viewStatsMenuItem.setEnabled(loggedInUser != null); // Tylko po zalogowaniu
        viewStatsMenuItem.addActionListener(e -> showUserStats());
        userMenu.add(viewStatsMenuItem);

        // Opcja usunięcia użytkownika
        JMenuItem deleteUserMenuItem = new JMenuItem("Usuń konto");
        deleteUserMenuItem.setEnabled(loggedInUser != null); // Opcja dostępna tylko po zalogowaniu
        deleteUserMenuItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Czy na pewno chcesz usunąć swoje konto?",
                    "Potwierdzenie usunięcia konta",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = DatabaseManager.deleteUser(loggedInUser);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Twoje konto zostało pomyślnie usunięte.");
                    loggedInUser = null; // Rozloguj użytkownika
                    dispose(); // Zamknij bieżące okno
                    new StartScreen(null); // Powróć do ekranu głównego
                } else {
                    JOptionPane.showMessageDialog(this, "Wystąpił problem podczas usuwania konta.",
                            "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        userMenu.add(deleteUserMenuItem);

        menuBar.add(userMenu);
        return menuBar;
    }

    /**
     * @brief Wyświetla okno dialogowe rejestracji nowego użytkownika.
     *
     * @details Okno dialogowe umożliwia wprowadzenie nazwy użytkownika oraz hasła. Po zatwierdzeniu,
     * jeśli użytkownik nie istnieje w bazie danych, konto jest tworzone, a użytkownik otrzymuje stosowny komunikat.
     * W przeciwnym razie pojawia się komunikat o błędzie, informujący, że użytkownik już istnieje.
     */
    private void showRegisterDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Podaj nazwę użytkownika:", usernameField,
                "Podaj hasło:", passwordField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Rejestracja nowego użytkownika", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (DatabaseManager.registerUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Konto zostało utworzone!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Użytkownik już istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @brief Wyświetla okno dialogowe zmiany hasła.
     *
     * @details Okno dialogowe umożliwia wprowadzenie starego hasła, nowego hasła oraz potwierdzenia nowego hasła.
     * Jeśli nowe hasła nie są zgodne, użytkownik otrzymuje komunikat o błędzie. W przypadku poprawnego wprowadzenia,
     * hasło jest zmieniane, a użytkownik otrzymuje stosowny komunikat.
     */
    private void showChangePasswordDialog() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        Object[] message = {
                "Podaj stare hasło:", oldPasswordField,
                "Podaj nowe hasło:", newPasswordField,
                "Potwierdź nowe hasło:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Zmień hasło", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Nowe hasła nie pasują do siebie!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (DatabaseManager.changePassword(loggedInUser, oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(this, "Hasło zostało zmienione!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Nie udało się zmienić hasła!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @brief Tworzy przycisk z niestandardowym stylem.
     *
     * @details Tworzy przycisk z określonym tekstem, czcionką, tłem, ramką i innymi właściwościami.
     * Przyciski są używane w różnych miejscach w interfejsie użytkownika do inicjowania akcji.
     *
     * @param text Tekst, który ma być wyświetlony na przycisku.
     *
     * @return Zwraca utworzony przycisk.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setFocusPainted(false);
        button.setOpaque(true);
        return button;
    }

    /**
     * @brief Wyświetla statystyki użytkownika.
     *
     * @details Jeśli użytkownik jest zalogowany, wyświetlane są szczegółowe statystyki jego gier,
     * takie jak liczba rozegranych gier online, wygranych gier, przegranych gier oraz stosunek wygranych
     * do rozegranych gier. Pokazywane są również statystyki gier przeciwko komputerowi oraz gier lokalnych.
     *
     * @details Jeśli użytkownik nie jest zalogowany lub nie udało się pobrać statystyk z bazy danych,
     * wyświetlany jest odpowiedni komunikat o błędzie.
     */
    private void showUserStats() {
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "Zaloguj się, aby zobaczyć statystyki.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Pobranie statystyk użytkownika
        UserStats stats = DatabaseManager.getUserStats(loggedInUser);
        if (stats == null) {
            JOptionPane.showMessageDialog(this, "Nie udało się pobrać statystyk użytkownika.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Formatowanie i wyświetlanie statystyk
        String message = String.format(
                "<html><body>" +
                        "<h2>Statystyki dla użytkownika: %s</h2>" +
                        "<ul>" +
                        "<li>Rozegrane gry (online): %d</li>" +
                        "<li>Wygrane gry (online): %d</li>" +
                        "<li>Przegrane gry (online): %d</li>" +
                        "<li>Stosunek wygranych do rozegranych gier (online): %.2f</li>" +
                        "<li>Wygrane gry przeciw komputerowi: %d</li>" +
                        "<li>Rozegrane gry lokalne: %d</li>" +
                        "</ul>" +
                        "</body></html>",
                loggedInUser,
                stats.getGamesPlayed(),
                stats.getWins(),
                stats.getLosses(),
                stats.getWinRatio(),
                stats.getGamesVsBot(),
                stats.getGamesLocal()
        );
        JOptionPane.showMessageDialog(this, message, "Statystyki użytkownika", JOptionPane.INFORMATION_MESSAGE);
    }
}
