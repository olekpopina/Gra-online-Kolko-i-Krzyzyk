package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * @brief Klasa reprezentująca grę w trybie serwera w grze "Kółko i Krzyżyk".
 *
 * @details Klasa obsługuje tworzenie serwera, oczekiwanie na połączenie klienta, wymianę danych
 * między serwerem a klientem oraz zarządzanie rozgrywką. Serwer gra jako "X" i zaczyna pierwszą turę.
 * Wykorzystuje gniazda sieciowe oraz wątki do komunikacji i rozgrywki w trybie online.
 */
public class ServerGame extends GameBase {
    private PrintWriter out; /**< Strumień wyjściowy do komunikacji z klientem */
    private BufferedReader in; /**< Strumień wejściowy do komunikacji z klientem */
    private boolean isMyTurn = true; /**< Flaga oznaczająca, czy to tura serwera (X) */
    private ServerSocket serverSocket;  /**< Gniazdo serwera do nasłuchiwania połączeń */
    private Socket clientSocket;     /**< Gniazdo klienta do komunikacji */
    private JFrame waitingScreen; /**< Ekran oczekiwania na klienta */

    /**
     * @brief Konstruktor inicjalizujący grę w trybie serwera.
     *
     * @details Inicjalizuje ekran oczekiwania na klienta oraz uruchamia serwer na porcie 5000.
     * Po nawiązaniu połączenia z klientem, serwer przechodzi do trybu gry.
     */
    public ServerGame() {
        super("Gra jako serwer ( X )", "online", "X");
        initializeWaitingScreen(); /**< Inicjalizuje ekran oczekiwania */
        startServer();              /**< Uruchamia serwer */
        setVisible(false);          /**< Ukrywa główne okno gry na czas oczekiwania na klienta */
    }

    /**
     * @brief Inicjalizuje ekran oczekiwania na klienta.
     *
     * @details Tworzy okno z informacją o oczekiwaniu na klienta, wyświetla adres IP serwera oraz
     * informuje o stanie połączenia.
     */
    private void initializeWaitingScreen() {
        // Tworzenie okna oczekiwania
        waitingScreen = new JFrame("Oczekiwanie na klienta...");
        waitingScreen.setSize(600, 550);
        waitingScreen.setLocationRelativeTo(null);
        waitingScreen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Ustawienia tła oraz etykiet
        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo_oczekiwania.jpg");
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        JLabel waitingLabel = new JLabel("Oczekiwanie na klienta...");
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        waitingLabel.setForeground(Color.BLUE);
        waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ipLabel = new JLabel("Twój adres IP: " + getLocalIPAddress());
        ipLabel.setFont(new Font("Arial", Font.BOLD, 16));
        ipLabel.setForeground(Color.BLUE);
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        backgroundPanel.add(Box.createVerticalGlue());
        backgroundPanel.add(waitingLabel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundPanel.add(ipLabel);
        backgroundPanel.add(Box.createVerticalGlue());

        waitingScreen.setContentPane(backgroundPanel);
        waitingScreen.setVisible(true);
    }

    /**
     * @brief Uruchamia serwer, oczekując na połączenie klienta.
     *
     * @details Serwer nasłuchuje na porcie 5000, po nawiązaniu połączenia z klientem,
     * inicjuje strumienie komunikacyjne oraz przechodzi do rozgrywki.
     * Uruchamia również nowy wątek do nasłuchiwania ruchów od klienta.
     */
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);  /**< Tworzenie gniazda serwera na porcie 5000 */
                System.out.println("Serwer uruchomiony. Oczekiwanie na klienta...");

                clientSocket = serverSocket.accept(); /**< Czekanie na połączenie klienta */
                System.out.println("Klient połączony: " + clientSocket.getInetAddress());

                // Zamykanie okna oczekiwania
                SwingUtilities.invokeLater(() -> waitingScreen.dispose());

                // Inicjalizowanie strumieni do wymiany danych
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Pokazywanie okna gry po połączeniu z klientem
                SwingUtilities.invokeLater(() -> setVisible(true));

                // Uruchamianie wątku nasłuchującego ruchy klienta
                new Thread(this::listenForClientMoves).start();

            } catch (IOException e) {
                showError("Błąd serwera: " + e.getMessage()); /**< Obsługuje błędy związane z serwerem */
            }
        }).start();
    }
    /**
     * @brief Pobiera adres IP lokalnej maszyny.
     *
     * @return String Adres IP maszyny serwera.
     *
     * @details Metoda sprawdza interfejsy sieciowe komputera i zwraca adres IP dla interfejsu Wi-Fi,
     * jeśli jest dostępny. W przypadku błędu lub braku połączenia Wi-Fi, zwraca odpowiedni komunikat.
     */
    private String getLocalIPAddress() {
        try {
            for (NetworkInterface networkInterface : java.util.Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    for (InetAddress address : java.util.Collections.list(networkInterface.getInetAddresses())) {
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            String displayName = networkInterface.getDisplayName().toLowerCase();
                            if (displayName.contains("wi-fi") || displayName.contains("wlan") || displayName.contains("wireless")) {
                                return address.getHostAddress(); /**< Zwraca adres IP interfejsu Wi-Fi */
                            }
                        }
                    }
                }
            }
            return "Nie znaleziono IP dla Wi-Fi"; /**< Jeśli nie znaleziono IP */
        } catch (Exception e) {
            return "Błąd przy uzyskiwaniu IP: " + e.getMessage(); /**< Obsługuje błędy przy uzyskiwaniu adresu IP */
        }
    }

    @Override
    /**
     * @brief Wykonuje ruch serwera w grze.
     *
     * @details Sprawdza, czy pole jest już zajęte, czy to tura serwera oraz czy strumień wyjściowy
     * jest zainicjalizowany. Jeśli warunki są spełnione, serwer wykonuje ruch, wysyła informację o ruchu
     * do klienta oraz sprawdza status gry.
     *
     * @param row Indeks wiersza, w którym wykonano ruch.
     * @param col Indeks kolumny, w której wykonano ruch.
     */
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null || !isMyTurn || out == null) return;

        gameState[row][col] = "X"; // Serwer gra jako "X"
        buttons[row][col].setIcon(getPlayerIcon("X", buttons[row][col].getWidth()));
        buttons[row][col].setEnabled(false);
        isMyTurn = false;

        out.println(row + "," + col + ",X"); // Wysyłanie ruchu do klienta
        checkGameStatus();
    }
    /**
     * @brief Nasłuchuje ruchów wykonanych przez klienta.
     *
     * @details Odczytuje dane wysłane przez klienta (ruchy) i aktualizuje stan gry.
     * Jeśli klient rozłączy się lub wystąpi błąd podczas komunikacji, połączenie jest zamykane.
     *
     * @throws IOException W przypadku problemów z komunikacją z klientem.
     */
    private void listenForClientMoves() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                // Przetwarzanie ruchu od klienta
                String[] parts = line.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                String player = parts[2];

                gameState[row][col] = player;
                buttons[row][col].setIcon(getPlayerIcon(player, buttons[row][col].getWidth()));
                buttons[row][col].setEnabled(false);

                isMyTurn = true; // Teraz tura serwera
                checkGameStatus();
            }

            // Jeśli readLine() zwróciło null, klient się rozłączył
            handleClientDisconnect();
        } catch (IOException e) {
            handleClientDisconnect();
        }
    }
    /**
     * @brief Obsługuje rozłączenie klienta.
     *
     * @details Wyświetla komunikat o utracie połączenia z klientem, zamyka połączenie
     * i przechodzi do głównego menu.
     */
    private void handleClientDisconnect() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Połączenie z klientem zostało utracone.",
                    "Błąd połączenia", JOptionPane.ERROR_MESSAGE);
            closeConnection(); // Zamykanie połączeń
            returnToMainMenu(); // Powrót do głównego menu
        });
    }
    /**
     * @brief Zamyka połączenie z klientem i serwerem.
     *
     * @details Zamyka wszystkie strumienie oraz gniazdo serwera i klienta.
     * Obsługuje wyjątki związane z zamykaniem połączeń.
     */
    private void closeConnection() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Serwer zamknięty.");
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas zamykania połączenia: " + e.getMessage());
        }
    }



    @Override
    /**
     * @brief Powraca do głównego menu.
     *
     * @details Wyświetla okno dialogowe z zapytaniem, czy gracz chce wrócić do głównego menu.
     * Jeśli potwierdzi, zamykane są połączenia oraz okno gry i uruchamiane jest menu główne.
     *
     * @note Metoda unika wielokrotnego wywołania dzięki flagom.
     */
    protected void returnToMainMenu() {
        if (isReturningToMenu) return; // Unika wielokrotnego wywołania
        isReturningToMenu = true;

        int option = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz wrócić do głównego menu?",
                "Powrót do menu", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            closeConnection(); // Zamykanie serwera
            SwingUtilities.invokeLater(() -> {
                dispose();
                new StartScreen(loggedInUser); // Powrót do ekranu startowego
            });
        } else {
            isReturningToMenu = false; // Resetowanie flagi
        }
    }
}
