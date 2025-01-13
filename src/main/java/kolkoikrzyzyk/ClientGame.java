package kolkoikrzyzyk;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class ClientGame extends GameBase {
    private boolean isMyTurn = false; // Клієнт починає другим
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public ClientGame(String serverIp) {
        super("Gra jako klient ( O )", "online", "O");
        connectToServer(serverIp);
    }

    private void connectToServer(String serverIp) {
        try {
            socket = new Socket(serverIp, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::listenForMoves).start();
        } catch (IOException e) {
            showError("Nie można połączyć się z serwerem: " + e.getMessage());
            closeConnection(); // Закриваємо, якщо виникла помилка
        }
    }

    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null || !isMyTurn) return;

        gameState[row][col] = "O"; // Клієнт грає за O
        buttons[row][col].setIcon(getPlayerIcon("O", buttons[row][col].getWidth()));
        buttons[row][col].setEnabled(false);
        isMyTurn = false;

        out.println(row + "," + col + ",O"); // Відправляємо хід серверу
        checkGameStatus();
    }

    private void listenForMoves() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if ("DISCONNECT".equals(line)) { // Сервер повідомляє про розрив
                    handleServerDisconnect();
                    return;
                }

                // Обробка ходу сервера
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

            // Якщо readLine повернуло null
            handleServerDisconnect();
        } catch (IOException e) {
            handleServerDisconnect();
        }
    }

    private void handleServerDisconnect() {
        JOptionPane.showMessageDialog(this, "Połączenie z serwerem zostało utracone.",
                "Błąd połączenia", JOptionPane.ERROR_MESSAGE);

        closeConnection();
        returnToMainMenu();
    }

    @Override
    protected void returnToMainMenu() {
        int option = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz wrócić do głównego menu?",
                "Powrót do menu", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                out.println("DISCONNECT"); // Повідомляємо сервер про розрив
            } catch (Exception ignored) {
            }
            closeConnection();
            dispose();
            new StartScreen(loggedInUser);
        }
    }

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

