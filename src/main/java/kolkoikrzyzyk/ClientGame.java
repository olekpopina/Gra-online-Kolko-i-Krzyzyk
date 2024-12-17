package kolkoikrzyzyk;

import java.io.*;
import java.net.*;

public class ClientGame extends GameBase {
    private PrintWriter out;
    private BufferedReader in;
    private boolean isMyTurn = false; // Клієнт починає другим

    public ClientGame(String serverIp) {
        super("Gra jako klient");
        connectToServer(serverIp);
    }

    private void connectToServer(String serverIp) {
        try {
            Socket socket = new Socket(serverIp, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::listenForMoves).start();
        } catch (IOException e) {
            showError("Nie można połączyć się z serwerem: " + e.getMessage());
        }
    }

    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null || !isMyTurn) return; // Перевірка черги ходу

        gameState[row][col] = "O"; // Клієнт завжди "O"
        buttons[row][col].setIcon(getPlayerIcon("O", buttons[row][col].getWidth()));
        buttons[row][col].setEnabled(false);
        isMyTurn = false; // Завершуємо хід

        out.println(row + "," + col + ",O"); // Відправляємо хід серверу
        checkGameStatus();
    }

    private void listenForMoves() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                String player = parts[2];

                gameState[row][col] = player;
                buttons[row][col].setIcon(getPlayerIcon(player, buttons[row][col].getWidth()));
                buttons[row][col].setEnabled(false);

                isMyTurn = true; // Після отримання ходу, клієнт може ходити
                checkGameStatus();
            }
        } catch (IOException e) {
            showError("Błąd sieci: " + e.getMessage());
        }
    }
}

