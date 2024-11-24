package gra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends JFrame implements ActionListener {
    private final JButton[][] przyciski = new JButton[3][3];
    private boolean turaGraczaX = true;
    private boolean mojaTura;
    private int liczbaRuchow = 0;
    private final String[][] gameState = new String[3][3];


    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final BufferedImage obrazekX = ResourceLoader.loadImage("/images/x.png");
    private final BufferedImage obrazekO = ResourceLoader.loadImage("/images/o.png");

    public Main(String typ, String ip) {
        String playerSymbol = typ.equals("Serwer") ? "X" : "O";
        setTitle("Kółko i Krzyżyk - " + typ + " (Gracz: " + playerSymbol + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo.png");
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        initializeButtons(backgroundPanel);

        // Додаємо слухач для змін розміру вікна
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateButtonSizes();
            }
        });

        setNetworkConnection(typ, ip);
        setVisible(true);
    }

    private void initializeButtons(BackgroundPanel backgroundPanel) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                przyciski[i][j] = new JButton("");
                przyciski[i][j].setFocusPainted(false);
                przyciski[i][j].setOpaque(false);
                przyciski[i][j].setContentAreaFilled(false);
                przyciski[i][j].setBorderPainted(false);
                przyciski[i][j].addActionListener(this);
                backgroundPanel.add(przyciski[i][j]);
            }
        }
        updateButtonSizes(); // Встановлюємо початкові розміри
    }

    private ImageIcon getScaledIcon(BufferedImage image, int buttonWidth, int buttonHeight) {
        int scaledWidth = (int) (buttonWidth * 0.7); // Однаковий коефіцієнт для всіх іконок
        int scaledHeight = (int) (buttonHeight * 0.7);
        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private void updateButtonSizes() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int buttonWidth = panelWidth / 5; // Пропорційна ширина
        int buttonHeight = panelHeight / 5; // Пропорційна висота

        int startX = (panelWidth - buttonWidth * 3) / 2; // Центруємо по горизонталі
        int startY = (panelHeight - buttonHeight * 3) / 2; // Центруємо по вертикалі

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int x = startX + j * buttonWidth;
                int y = startY + i * buttonHeight;
                przyciski[i][j].setBounds(x, y, buttonWidth, buttonHeight);

                int finalI = i;
                int finalJ = j;
                SwingUtilities.invokeLater(() -> updateButtonIcon(przyciski[finalI][finalJ], buttonWidth, buttonHeight));
            }
        }
    }


    private void updateButtonIcon(JButton button, int width, int height) {
        if (button.getIcon() instanceof ImageIcon) {
            ImageIcon currentIcon = (ImageIcon) button.getIcon();
            if (currentIcon.getImage() == obrazekX) {
                button.setIcon(getScaledIcon(obrazekX, width, height));
            } else if (currentIcon.getImage() == obrazekO) {
                button.setIcon(getScaledIcon(obrazekO, width, height));
            }
        }
    }

    private void setNetworkConnection(String typ, String ip) {
        try {
            if (typ.equals("Serwer")) {
                ServerSocket serverSocket = new ServerSocket(5000);
                socket = serverSocket.accept();
                mojaTura = true;
            } else {
                socket = new Socket(ip, 5000);
                mojaTura = false;
            }
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(this::listenForMoves).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Помилка мережевого з'єднання", "Помилка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        if (!mojaTura || clickedButton.getIcon() != null) return;

        int buttonWidth = clickedButton.getWidth();
        int buttonHeight = clickedButton.getHeight();

        String currentPlayer = turaGraczaX ? "X" : "O";
        ImageIcon icon = turaGraczaX
                ? getScaledIcon(obrazekX, buttonWidth, buttonHeight)
                : getScaledIcon(obrazekO, buttonWidth, buttonHeight);

        clickedButton.setIcon(icon);

        // Оновлюємо стан гри
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (przyciski[i][j] == clickedButton) {
                    gameState[i][j] = currentPlayer;
                    out.println(i + "," + j + "," + currentPlayer); // Передаємо координати та гравця
                }
            }
        }

        mojaTura = false;
        liczbaRuchow++;

        // Перевіряємо перемогу
        if (GameLogic.checkWin(gameState)) {
            JOptionPane.showMessageDialog(this, "Виграє: " + currentPlayer);
            out.println("RESET");
            resetGame();
        } else if (liczbaRuchow == 9) {
            JOptionPane.showMessageDialog(this, "Нічия!");
            out.println("RESET");
            resetGame();
        }

        turaGraczaX = !turaGraczaX;
    }





    private void listenForMoves() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("RESET")) {
                    SwingUtilities.invokeLater(this::resetGame);
                    continue;
                }

                // Розбираємо хід іншого гравця
                String[] move = line.split(",");
                int x = Integer.parseInt(move[0]);
                int y = Integer.parseInt(move[1]);
                String player = move[2]; // Отримуємо гравця, який зробив хід (X або O)

                gameState[x][y] = player;

                JButton button = przyciski[x][y];
                int buttonWidth = button.getWidth();
                int buttonHeight = button.getHeight();

                ImageIcon icon = player.equals("X")
                        ? getScaledIcon(obrazekX, buttonWidth, buttonHeight)
                        : getScaledIcon(obrazekO, buttonWidth, buttonHeight);

                SwingUtilities.invokeLater(() -> button.setIcon(icon));

                // Перевіряємо перемогу
                if (GameLogic.checkWin(gameState)) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Виграє: " + player);
                        out.println("RESET");
                        resetGame();
                    });
                } else if (liczbaRuchow == 9) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Нічия!");
                        out.println("RESET");
                        resetGame();
                    });
                }

                mojaTura = true; // Інший гравець завершив хід
                liczbaRuchow++;
                turaGraczaX = player.equals("O"); // Наступний хід для протилежного гравця
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                przyciski[i][j].setIcon(null);
                gameState[i][j] = null; // Очищуємо стан гри
            }
        }

        turaGraczaX = !turaGraczaX;
        liczbaRuchow = 0;
        mojaTura = turaGraczaX;
    }

    private void setEnabledButtons(boolean enabled) {
        for (JButton[] row : przyciski) {
            for (JButton button : row) {
                button.setEnabled(enabled);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame wyborOkna = new JFrame("Wybierz tryb");
            wyborOkna.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            wyborOkna.setSize(300, 150);
            wyborOkna.setLayout(new GridLayout(2, 1));

            JButton serwerButton = new JButton("Serwer");
            JButton klientButton = new JButton("Klient");

            serwerButton.addActionListener(e -> {
                wyborOkna.dispose();
                new Main("Serwer", "localhost");
            });

            klientButton.addActionListener(e -> {
                wyborOkna.dispose();
                JFrame ipFrame = new JFrame("Podaj IP serwera");
                ipFrame.setSize(300, 150);
                ipFrame.setLayout(new BorderLayout());

                JTextField ipField = new JTextField("192.168.1.12"); // Автозаповнення IP
                JButton connectButton = new JButton("Połącz");

                connectButton.addActionListener(ev -> {
                    ipFrame.dispose();
                    String ip = ipField.getText();
                    new Main("Klient", ip);
                });

                ipFrame.add(new JLabel("IP serwera:"), BorderLayout.NORTH);
                ipFrame.add(ipField, BorderLayout.CENTER);
                ipFrame.add(connectButton, BorderLayout.SOUTH);
                ipFrame.setVisible(true);
            });

            wyborOkna.add(serwerButton);
            wyborOkna.add(klientButton);
            wyborOkna.setVisible(true);
        });
    }
}
