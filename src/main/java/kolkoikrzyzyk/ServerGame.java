package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ServerGame extends GameBase {
    private PrintWriter out;
    private BufferedReader in;
    private boolean isMyTurn = true; // Сервер починає першим
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private JFrame waitingScreen;

    public ServerGame() {
        super("Gra jako serwer");
        initializeWaitingScreen(); // Показуємо екран очікування
        startServer();             // Запускаємо сервер
        setVisible(false);         // Вікно гри поки що приховане
    }

    private void initializeWaitingScreen() {
        // Створення вікна очікування
        waitingScreen = new JFrame("Oczekiwanie na klienta...");
        waitingScreen.setSize(600, 550);
        waitingScreen.setLocationRelativeTo(null);
        waitingScreen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Створення фону
        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo_oczekiwania.jpg");
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        // Створення та налаштування мітки "Oczekiwanie na klienta"
        JLabel waitingLabel = new JLabel("Oczekiwanie na klienta...");
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        waitingLabel.setForeground(Color.BLUE);
        waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрування по горизонталі

        // Створення та налаштування мітки з IP-адресою
        JLabel ipLabel = new JLabel("Twój adres IP: " + getLocalIPAddress());
        ipLabel.setFont(new Font("Arial", Font.BOLD, 16));
        ipLabel.setForeground(Color.BLUE);
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрування по горизонталі

        // Додаємо мітки з відступами
        backgroundPanel.add(Box.createVerticalGlue()); // Простір перед мітками
        backgroundPanel.add(waitingLabel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Відступ між мітками
        backgroundPanel.add(ipLabel);
        backgroundPanel.add(Box.createVerticalGlue()); // Простір після міток

        // Встановлюємо фон як вміст вікна
        waitingScreen.setContentPane(backgroundPanel);
        waitingScreen.setVisible(true);
    }


    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000); // Створення сервера на порту 5000
                System.out.println("Serwer uruchomiony. Oczekiwanie na klienta...");

                clientSocket = serverSocket.accept(); // Чекаємо на підключення клієнта
                System.out.println("Klient połączony: " + clientSocket.getInetAddress());

                // Закриваємо вікно очікування
                SwingUtilities.invokeLater(() -> waitingScreen.dispose());

                // Ініціалізуємо потоки для обміну даними
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Відображаємо вікно гри після підключення клієнта
                SwingUtilities.invokeLater(() -> setVisible(true));

                // Починаємо слухати хід клієнта
                new Thread(this::listenForMoves).start();

            } catch (IOException e) {
                showError("Błąd serwera: " + e.getMessage());
            }
        }).start();
    }

    private String getLocalIPAddress() {
        try {
            for (NetworkInterface networkInterface : java.util.Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    for (InetAddress address : java.util.Collections.list(networkInterface.getInetAddresses())) {
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            String displayName = networkInterface.getDisplayName().toLowerCase();
                            if (displayName.contains("wi-fi") || displayName.contains("wlan") || displayName.contains("wireless")) {
                                return address.getHostAddress();
                            }
                        }
                    }
                }
            }
            return "Nie znaleziono IP dla Wi-Fi";
        } catch (Exception e) {
            return "Błąd przy uzyskiwaniu IP: " + e.getMessage();
        }
    }

    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null || !isMyTurn || out == null) return;

        gameState[row][col] = "X"; // Сервер грає за X
        buttons[row][col].setIcon(getPlayerIcon("X", buttons[row][col].getWidth()));
        buttons[row][col].setEnabled(false);
        isMyTurn = false;

        out.println(row + "," + col + ",X"); // Надсилаємо хід клієнту
        checkAndSaveGameResult();
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

                isMyTurn = true; // Після отримання ходу сервер знову може ходити
                checkGameStatus();
            }
        } catch (IOException e) {
            showError("Błąd sieci: " + e.getMessage());
        }
    }
}
