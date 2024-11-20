package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;

public class Main extends JFrame implements ActionListener {
    private JButton[][] przyciski = new JButton[3][3];
    private boolean turaGraczaX = true;
    private boolean turaGraczaO = false;
    private int liczbaRuchow = 0;
    private boolean mojaTura;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedImage obrazekX;
    private BufferedImage obrazekO;

    private class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel(String s) {
            try {

                backgroundImage = ImageIO.read(new File(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public Main(String typ, String ip) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.4); // 40% szerokości ekranu
        int height = width; // Kwadratowe okno
        setSize(width, height);
        setTitle("Kółko i Krzyżyk - " + typ);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            obrazekX = ImageIO.read(new File("D:\\Gra-online-kolko-krzyzyk\\x.png"));
            obrazekO = ImageIO.read(new File("D:\\Gra-online-kolko-krzyzyk\\o.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel("D:\\Gra-online-kolko-krzyzyk\\tlo.png");

        backgroundPanel.setLayout(null);

        int gridSize = 3; // Liczba wierszy i kolumn w siatce
        double scale = 0.6; // Współczynnik zmniejszenia przycisków (70%)

        int width2 = 494;
        int height2 = 456;
        // Obliczanie szerokości i wysokości przycisków z uwzględnieniem współczynnika skali
        int buttonWidth = (int) (width2 / gridSize * scale); // Szerokość przycisków po zmniejszeniu
        int buttonHeight = (int) (height2 / gridSize * scale); // Wysokość przycisków po zmniejszeniu
        int padding = 35; // Mały odstęp

        int marginX = 120; // Margines od lewej krawędzi tła
        int marginY = 120; // Margines od górnej krawędzi tła

        // Dodanie przycisków do panelu z ręcznym ustawieniem rozmiarów
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                przyciski[i][j] = new JButton("");
                int x = marginX + j * (buttonWidth + padding); // Pozycja pozioma z uwzględnieniem marginesu i padding
                int y = marginY + i * (buttonHeight + padding); // Pozycja pionowa z uwzględnieniem marginesu i padding
                przyciski[i][j].setBounds(x, y, buttonWidth, buttonHeight); // Określenie pozycji i rozmiaru
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

        // Ustawienie połączenia sieciowego
        try {
            if (typ.equals("Serwer")) {
                ServerSocket serverSocket = new ServerSocket(5000);
                socket = serverSocket.accept();
                mojaTura = true;  // Serwer zaczyna
            } else {
                socket = new Socket(ip, 5000);
                mojaTura = false; // Klient czeka na ruch serwera
            }
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(this::nasluchujRuchy).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

@Override
public void actionPerformed(ActionEvent e) {
    JButton kliknietyPrzycisk = (JButton) e.getSource();

    if (!mojaTura || kliknietyPrzycisk.getIcon() != null) {
        return;
    }

    // Pobranie wymiarów przycisku
    int szerokosc = kliknietyPrzycisk.getWidth();
    int wysokosc = kliknietyPrzycisk.getHeight();

    Image scaledImage;
    if (turaGraczaX) {
        // Skalowanie obrazka X do aktualnych rozmiarów przycisku
        scaledImage = obrazekX.getScaledInstance(szerokosc, wysokosc, Image.SCALE_SMOOTH);
        kliknietyPrzycisk.setIcon(new ImageIcon(scaledImage));
        turaGraczaX = false;
        turaGraczaO = true;
    } else if (turaGraczaO) {
        // Skalowanie obrazka O do aktualnych rozmiarów przycisku
        scaledImage = obrazekO.getScaledInstance(szerokosc, wysokosc, Image.SCALE_SMOOTH);
        kliknietyPrzycisk.setIcon(new ImageIcon(scaledImage));
        turaGraczaO = false;
        turaGraczaX = true;
    }

    mojaTura = false;
    liczbaRuchow++;

    // Znajdź współrzędne klikniętego przycisku
    int x = -1, y = -1;
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            if (przyciski[i][j] == kliknietyPrzycisk) {
                x = i;
                y = j;
            }
        }
    }

    // Przesłanie ruchu do przeciwnika
    out.println(x + "," + y);

    // Sprawdzenie, czy ktoś wygrał lub czy jest remis
    if (czyKtosWygral()) {
        String zwyciezca = turaGraczaX ? "O" : "X"; // Odwrócona tura wskazuje zwycięzcę
        JOptionPane.showMessageDialog(this, "Wygrywa: " + zwyciezca);
        resetGry();
    } else if (liczbaRuchow == 9) {
        JOptionPane.showMessageDialog(this, "Remis!");
        resetGry();
    }
}


    private void nasluchujRuchy() {
        try {
            String linia;
            while ((linia = in.readLine()) != null) {
                String[] ruch = linia.split(",");
                int x = Integer.parseInt(ruch[0]);
                int y = Integer.parseInt(ruch[1]);

                // Pobranie wymiarów przycisku
                int szerokosc = przyciski[x][y].getWidth();
                int wysokosc = przyciski[x][y].getHeight();

                // Skalowanie obrazu w zależności od tury
                Image scaledImage;
                if (turaGraczaX) {
                    scaledImage = obrazekX.getScaledInstance(szerokosc, wysokosc, Image.SCALE_SMOOTH);
                    przyciski[x][y].setIcon(new ImageIcon(scaledImage));
                    turaGraczaX = false;
                    turaGraczaO = true;
                } else if (turaGraczaO) {
                    scaledImage = obrazekO.getScaledInstance(szerokosc, wysokosc, Image.SCALE_SMOOTH);
                    przyciski[x][y].setIcon(new ImageIcon(scaledImage));
                    turaGraczaO = false;
                    turaGraczaX = true;
                }

                mojaTura = true;
                liczbaRuchow++;

                // Sprawdzenie, czy ktoś wygrał
                if (czyKtosWygral()) {
                    String zwyciezca = turaGraczaX ? "O" : "X";
                    JOptionPane.showMessageDialog(this, "Wygrywa: " + zwyciezca);
                    resetGry();
                } else if (liczbaRuchow == 9) {
                    JOptionPane.showMessageDialog(this, "Remis!");
                    resetGry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private boolean czyKtosWygral() {
    for (int i = 0; i < 3; i++) {
        // Sprawdzamy wiersze
        if (przyciski[i][0].getIcon() != null &&
                przyciski[i][0].getIcon().equals(przyciski[i][1].getIcon()) &&
                przyciski[i][1].getIcon().equals(przyciski[i][2].getIcon())) {
            return true;
        }
        // Sprawdzamy kolumny
        if (przyciski[0][i].getIcon() != null &&
                przyciski[0][i].getIcon().equals(przyciski[1][i].getIcon()) &&
                przyciski[1][i].getIcon().equals(przyciski[2][i].getIcon())) {
            return true;
        }
    }
    // Sprawdzamy przekątne
    if (przyciski[0][0].getIcon() != null &&
            przyciski[0][0].getIcon().equals(przyciski[1][1].getIcon()) &&
            przyciski[1][1].getIcon().equals(przyciski[2][2].getIcon())) {
        return true;
    }
    if (przyciski[0][2].getIcon() != null &&
            przyciski[0][2].getIcon().equals(przyciski[1][1].getIcon()) &&
            przyciski[1][1].getIcon().equals(przyciski[2][0].getIcon())) {
        return true;
    }
    return false;
}

    private void resetGry() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                przyciski[i][j].setIcon(null);
            }
        }
        turaGraczaX = true;
        turaGraczaO = false;
        liczbaRuchow = 0;
        mojaTura = true;
    }
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        JFrame wyborOkna = new JFrame("Wybierz tryb");
        wyborOkna.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wyborOkna.setSize(300, 150);
        wyborOkna.setLayout(new GridLayout(3, 1)); //3

        JLabel label = new JLabel("Wybierz, czy chcesz być serwerem czy klientem:", SwingConstants.CENTER);
        wyborOkna.add(label);

        JButton serwerButton = new JButton("Serwer");
        JButton klientButton = new JButton("Klient");

        serwerButton.addActionListener(e -> {
            wyborOkna.dispose(); // Zamknięcie okna wyboru
            SwingUtilities.invokeLater(() -> {
                Main gra = new Main("Serwer", "localhost");
                gra.setVisible(true);
            });
        });

        klientButton.addActionListener(e -> {
            wyborOkna.dispose(); // Zamknięcie okna wyboru
            JFrame ipOkno = new JFrame("Podaj adres IP");
            ipOkno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ipOkno.setSize(300, 150);
            ipOkno.setLayout(new BorderLayout());

            JLabel ipLabel = new JLabel("Podaj adres IP serwera:");
            JTextField ipField = new JTextField("192.168.0.16"); // Domyślne IP

            JButton polaczButton = new JButton("Połącz");
            polaczButton.addActionListener(ev -> {
                ipOkno.dispose(); // Zamknięcie okna z adresem IP
                String ip = ipField.getText();
                SwingUtilities.invokeLater(() -> {
                    Main gra = new Main("Klient", ip);
                    gra.setVisible(true);
                });
            });

            ipOkno.add(ipLabel, BorderLayout.NORTH);
            ipOkno.add(ipField, BorderLayout.CENTER);
            ipOkno.add(polaczButton, BorderLayout.SOUTH);
            ipOkno.setVisible(true);
        });

        wyborOkna.add(serwerButton);
        wyborOkna.add(klientButton);
        wyborOkna.setVisible(true);
    });
}
}