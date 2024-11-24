package gra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final BufferedImage obrazekX = ResourceLoader.loadImage("/images/x.png");
    private final BufferedImage obrazekO = ResourceLoader.loadImage("/images/o.png");

    public Main(String typ, String ip) {
        setTitle("Kółko i Krzyżyk - " + typ);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo.png");
//        backgroundPanel.setLayout(new GridLayout(3, 3));
//        add(backgroundPanel);
//
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                przyciski[i][j] = new JButton();
//                przyciski[i][j].setFocusPainted(false);
//                przyciski[i][j].addActionListener(this);
//                backgroundPanel.add(przyciski[i][j]);
//            }
//        }
        backgroundPanel.setLayout(null);
        Dimension dimension = getSize();
        int gridSize = 3; // Liczba wierszy i kolumn w siatce
        double scale = 288 / dimension.getWidth() * 1.25; // Współczynnik zmniejszenia przycisków (70%)

        double width2 = dimension.width * 0.6;
        double height2 = dimension.height * 0.6;
        // Obliczanie szerokości i wysokości przycisków z uwzględnieniem współczynnika skali
        int buttonWidth = (int) (width2 / gridSize * scale); // Szerokość przycisków po zmniejszeniu
        int buttonHeight = (int) (height2 / gridSize * scale); // Wysokość przycisków po zmniejszeniu
        double padding = dimension.width * 0.1; // Mały odstęp

        double marginX = dimension.width * 0.2; // Margines od lewej krawędzi tła
        double marginY = dimension.height * 0.2; // Margines od górnej krawędzi tła

        // Dodanie przycisków do panelu z ręcznym ustawieniem rozmiarów
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                przyciski[i][j] = new JButton("");
                double x = marginX + j * (buttonWidth + padding); // Pozycja pozioma z uwzględnieniem marginesu i padding
                double y = marginY + i * (buttonHeight + padding); // Pozycja pionowa z uwzględnieniem marginesu i padding
                przyciski[i][j].setBounds((int) x, (int) y, buttonWidth, buttonHeight); // Określenie pozycji i rozmiaru
                przyciski[i][j].setFocusPainted(false);
                przyciski[i][j].setOpaque(false); // Przezroczyste tło przycisków
                przyciski[i][j].setContentAreaFilled(false);
                przyciski[i][j].setBorderPainted(false);
                przyciski[i][j].addActionListener(this);
                backgroundPanel.add(przyciski[i][j]);
            }
        }
        // Dodanie panelu tła do okna
        add(backgroundPanel);

        setNetworkConnection(typ, ip);
        setVisible(true);
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

        ImageIcon icon = new ImageIcon(
                turaGraczaX ? obrazekX.getScaledInstance(clickedButton.getWidth(), clickedButton.getHeight(), Image.SCALE_SMOOTH)
                        : obrazekO.getScaledInstance(clickedButton.getWidth(), clickedButton.getHeight(), Image.SCALE_SMOOTH)
        );
        clickedButton.setIcon(icon);

        mojaTura = false;
        liczbaRuchow++;

        int x = -1, y = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (przyciski[i][j] == clickedButton) {
                    x = i;
                    y = j;
                }
            }
        }

        out.println(x + "," + y);

        if (GameLogic.checkWin(przyciski)) {
            JOptionPane.showMessageDialog(this, "Wygrywa: " + (turaGraczaX ? "X" : "O"));
            resetGame();
        } else if (liczbaRuchow == 9) {
            JOptionPane.showMessageDialog(this, "Remis!");
            resetGame();
        }

        turaGraczaX = !turaGraczaX;
    }

    private void listenForMoves() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String[] move = line.split(",");
                int x = Integer.parseInt(move[0]);
                int y = Integer.parseInt(move[1]);

                JButton button = przyciski[x][y];
                ImageIcon icon = new ImageIcon(
                        turaGraczaX ? obrazekX.getScaledInstance(button.getWidth(), button.getHeight(), Image.SCALE_SMOOTH)
                                : obrazekO.getScaledInstance(button.getWidth(), button.getHeight(), Image.SCALE_SMOOTH)
                );
                button.setIcon(icon);

                mojaTura = true;
                liczbaRuchow++;

                if (GameLogic.checkWin(przyciski)) {
                    JOptionPane.showMessageDialog(this, "Wygrywa: " + (turaGraczaX ? "X" : "O"));
                    resetGame();
                } else if (liczbaRuchow == 9) {
                    JOptionPane.showMessageDialog(this, "Remis!");
                    resetGame();
                }

                turaGraczaX = !turaGraczaX;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetGame() {
        for (JButton[] row : przyciski) {
            for (JButton button : row) {
                button.setIcon(null);
            }
        }
        turaGraczaX = true;
        liczbaRuchow = 0;
        mojaTura = true;
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
                String ip = JOptionPane.showInputDialog("Podaj adres IP serwera:");
                new Main("Klient", ip);
            });

            wyborOkna.add(serwerButton);
            wyborOkna.add(klientButton);
            wyborOkna.setVisible(true);
        });
    }
}

