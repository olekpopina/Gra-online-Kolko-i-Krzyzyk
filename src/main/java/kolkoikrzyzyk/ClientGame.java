package kolkoikrzyzyk;
import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * @brief Klasa reprezentująca grę jako klient w trybie online.
 *
 * Klasa dziedziczy po GameBase i obsługuje logikę gry oraz komunikację z serwerem.
 */
public class ClientGame extends GameBase {
    /**
     * @brief Określa, czy ruch należy do klienta.
     */
    private boolean isMyTurn = false; // Klient zaczyna drugi.

    /**
     * @brief Gniazdo sieciowe do komunikacji z serwerem.
     */
    private Socket socket;

    /**
     * @brief Strumień wyjściowy do wysyłania danych do serwera.
     */
    private PrintWriter out;

    /**
     * @brief Strumień wejściowy do odbierania danych z serwera.
     */
    private BufferedReader in;

    /**
     * @brief Konstruktor klasy ClientGame.
     *
     * Tworzy instancję gry jako klient i łączy się z serwerem.
     *
     * @param serverIp Adres IP serwera.
     */
    public ClientGame(String serverIp) {
        super("Gra jako klient ( O )", "online", "O");
        connectToServer(serverIp);
    }

    /**
     * @brief Łączy się z serwerem gry.
     *
     * Tworzy gniazdo sieciowe i uruchamia wątek do nasłuchiwania ruchów serwera.
     *
     * @param serverIp Adres IP serwera.
     */
    private void connectToServer(String serverIp) {
        try {
            socket = new Socket(serverIp, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::listenForMoves).start();
        } catch (IOException e) {
            showError("Nie można połączyć się z serwerem: " + e.getMessage());
            closeConnection(); // Zamknięcie połączenia w przypadku błędu.
        }
    }

    /**
     * @brief Wykonuje ruch gracza.
     *
     * Aktualizuje stan gry, wysyła ruch do serwera i sprawdza status gry.
     *
     * @param row Wiersz planszy.
     * @param col Kolumna planszy.
     */
    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null || !isMyTurn) return;

        gameState[row][col] = "O"; // Klient gra jako O.
        buttons[row][col].setIcon(getPlayerIcon("O", buttons[row][col].getWidth()));
        buttons[row][col].setEnabled(false);
        isMyTurn = false;

        out.println(row + "," + col + ",O"); // Wysłanie ruchu do serwera.
        checkGameStatus();
    }

    /**
     * @brief Nasłuchuje ruchów serwera.
     *
     * Przetwarza dane przesyłane przez serwer i aktualizuje stan gry.
     */
    private void listenForMoves() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if ("DISCONNECT".equals(line)) { // Serwer rozłączył się.
                    handleServerDisconnect();
                    return;
                }

                // Przetwarzanie ruchu serwera.
                String[] parts = line.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                String player = parts[2];

                gameState[row][col] = player;
                buttons[row][col].setIcon(getPlayerIcon(player, buttons[row][col].getWidth()));
                buttons[row][col].setEnabled(false);

                isMyTurn = true;
                checkGameStatus();
            }

            // Jeśli readLine zwróciło null.
            handleServerDisconnect();
        } catch (IOException e) {
            handleServerDisconnect();
        }
    }

    /**
     * @brief Obsługuje rozłączenie serwera.
     *
     * Wyświetla komunikat o utracie połączenia i powraca do menu głównego.
     */
    private void handleServerDisconnect() {
        JOptionPane.showMessageDialog(this, "Połączenie z serwerem zostało utracone.",
                "Błąd połączenia", JOptionPane.ERROR_MESSAGE);

        closeConnection();
        returnToMainMenu();
    }

    /**
     * @brief Powrót do menu głównego.
     *
     * Wyświetla komunikat potwierdzenia przed powrotem do menu.
     */
    @Override
    protected void returnToMainMenu() {
        if (isReturningToMenu) return; // Uniknięcie wielokrotnego wywołania.
        isReturningToMenu = true;

        int option = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz wrócić do głównego menu?",
                "Powrót do menu", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                if (out != null) {
                    out.println("DISCONNECT"); // Powiadomienie serwera o rozłączeniu.
                }
            } catch (Exception ignored) {
            }
            closeConnection();
            SwingUtilities.invokeLater(() -> {
                dispose();
                new StartScreen(loggedInUser);
            });
        } else {
            isReturningToMenu = false; // Reset flagi.
        }
    }

    /**
     * @brief Zamyka połączenie z serwerem.
     *
     * Zamyka strumienie wejściowe, wyjściowe oraz gniazdo sieciowe.
     */
    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Zamknięto połączenie.");
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
}
