package kolkoikrzyzyk;

import java.io.*;
import java.net.*;

public class ClientGame extends GameBase {
    private PrintWriter out;
    private BufferedReader in;

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
        if (gameState[row][col] != null) return;

        gameState[row][col] = "O"; // Клієнт завжди "O"
        buttons[row][col].setIcon(getPlayerIcon("O"));
        out.println(row + "," + col + ",O");

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
                buttons[row][col].setIcon(getPlayerIcon(player));

                checkGameStatus();
            }
        } catch (IOException e) {
            showError("Błąd sieci: " + e.getMessage());
        }
    }
}
