package kolkoikrzyzyk;

import java.io.*;
import java.net.*;

public class ServerGame extends GameBase {
    private PrintWriter out;
    private BufferedReader in;

    public ServerGame() {
        super("Gra jako serwer");
        setupServer();
    }

    private void setupServer() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            Socket socket = serverSocket.accept();
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::listenForMoves).start();
        } catch (IOException e) {
            showError("Błąd serwera: " + e.getMessage());
        }
    }

    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null) return;

        gameState[row][col] = "X"; // Сервер завжди "X"
        buttons[row][col].setIcon(getPlayerIcon("X"));
        out.println(row + "," + col + ",X");

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
