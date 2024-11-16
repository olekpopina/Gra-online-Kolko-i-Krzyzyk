/*
package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Main extends JFrame implements ActionListener {
    private JButton[][] przyciski = new JButton[3][3];
    private boolean turaGraczaX = true;
    private boolean turaGraczaO = false;
    private int liczbaRuchow = 0;
    private boolean mojaTura;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Main(String typ, String ip) {
        setTitle("Kółko i Krzyżyk - " + typ);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                przyciski[i][j] = new JButton("");
                przyciski[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                przyciski[i][j].setFocusPainted(false);
                przyciski[i][j].addActionListener(this);
                add(przyciski[i][j]);
            }
        }

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

        if (!mojaTura || !kliknietyPrzycisk.getText().equals("")) {
            return;
        }

        // Zależnie od tury ustaw odpowiedni symbol
        if (turaGraczaX) {
            kliknietyPrzycisk.setText("X");
            turaGraczaX = false;  // Zakończenie tury X
            turaGraczaO = true;   // Teraz tura O
        } else if (turaGraczaO) {
            kliknietyPrzycisk.setText("O");
            turaGraczaO = false;  // Zakończenie tury O
            turaGraczaX = true;   // Teraz ponownie tura X
        }

        mojaTura = false;  // Czeka na ruch przeciwnika
        liczbaRuchow++;

        int x = -1, y = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (przyciski[i][j] == kliknietyPrzycisk) {
                    x = i;
                    y = j;
                }
            }
        }
        out.println(x + "," + y);  // Wysłanie ruchu do przeciwnika

        if (czyKtosWygral()) {
            String zwyciezca = kliknietyPrzycisk.getText();
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

                // Ustaw odpowiedni symbol na podstawie obecnej tury
                if (turaGraczaX) {
                    przyciski[x][y].setText("X");
                    turaGraczaX = false;
                    turaGraczaO = true;
                } else if (turaGraczaO) {
                    przyciski[x][y].setText("O");
                    turaGraczaO = false;
                    turaGraczaX = true;
                }

                mojaTura = true;  // Przełącz na swoją turę
                liczbaRuchow++;

                if (czyKtosWygral()) {
                    String zwyciezca = przyciski[x][y].getText();
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
            if (przyciski[i][0].getText().equals(przyciski[i][1].getText()) &&
                    przyciski[i][1].getText().equals(przyciski[i][2].getText()) &&
                    !przyciski[i][0].getText().equals("")) {
                return true;
            }
            if (przyciski[0][i].getText().equals(przyciski[1][i].getText()) &&
                    przyciski[1][i].getText().equals(przyciski[2][i].getText()) &&
                    !przyciski[0][i].getText().equals("")) {
                return true;
            }
        }
        if (przyciski[0][0].getText().equals(przyciski[1][1].getText()) &&
                przyciski[1][1].getText().equals(przyciski[2][2].getText()) &&
                !przyciski[0][0].getText().equals("")) {
            return true;
        }
        if (przyciski[0][2].getText().equals(przyciski[1][1].getText()) &&
                przyciski[1][1].getText().equals(przyciski[2][0].getText()) &&
                !przyciski[0][2].getText().equals("")) {
            return true;
        }
        return false;
    }

    private void resetGry() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                przyciski[i][j].setText("");
            }
        }
        turaGraczaX = true;
        turaGraczaO = false;
        liczbaRuchow = 0;
        mojaTura = true;  // Resetowanie tury
    }

    public static void main(String[] args) {
        String typ = JOptionPane.showInputDialog("Wpisz 'Serwer' aby hostować lub 'Klient' aby się połączyć:");
        String ip = typ.equals("Klient") ? JOptionPane.showInputDialog("Podaj adres IP serwera:") : "localhost";

        SwingUtilities.invokeLater(() -> {
            Main gra = new Main(typ, ip);
            gra.setVisible(true);
        });
    }
}
*/

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

    // Panel tła
    private class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel(String s) {
            try {
                backgroundImage = ImageIO.read(new File("D:\\Gra-online-kolko-krzyzyk\\tlo.png")); // Podaj ścieżkę do pliku
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

        // Ustawienie panelu tła
        BackgroundPanel backgroundPanel = new BackgroundPanel("D:\\Gra-online-kolko-krzyzyk\\tlo.png");
        backgroundPanel.setLayout(new GridLayout(3, 3));


        // Dodanie przycisków do gry
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                przyciski[i][j] = new JButton("");
                przyciski[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                przyciski[i][j].setFocusPainted(false);
                przyciski[i][j].setOpaque(false);  // Przezroczyste tło przycisków
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

        if (!mojaTura || !kliknietyPrzycisk.getText().equals("")) {
            return;
        }

        if (turaGraczaX) {
            kliknietyPrzycisk.setText("X");
            turaGraczaX = false;
            turaGraczaO = true;
        } else if (turaGraczaO) {
            kliknietyPrzycisk.setText("O");
            turaGraczaO = false;
            turaGraczaX = true;
        }

        mojaTura = false;
        liczbaRuchow++;

        int x = -1, y = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (przyciski[i][j] == kliknietyPrzycisk) {
                    x = i;
                    y = j;
                }
            }
        }
        out.println(x + "," + y);

        if (czyKtosWygral()) {
            String zwyciezca = kliknietyPrzycisk.getText();
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

                if (turaGraczaX) {
                    przyciski[x][y].setText("X");
                    turaGraczaX = false;
                    turaGraczaO = true;
                } else if (turaGraczaO) {
                    przyciski[x][y].setText("O");
                    turaGraczaO = false;
                    turaGraczaX = true;
                }

                mojaTura = true;
                liczbaRuchow++;

                if (czyKtosWygral()) {
                    String zwyciezca = przyciski[x][y].getText();
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
            if (przyciski[i][0].getText().equals(przyciski[i][1].getText()) &&
                    przyciski[i][1].getText().equals(przyciski[i][2].getText()) &&
                    !przyciski[i][0].getText().equals("")) {
                return true;
            }
            if (przyciski[0][i].getText().equals(przyciski[1][i].getText()) &&
                    przyciski[1][i].getText().equals(przyciski[2][i].getText()) &&
                    !przyciski[0][i].getText().equals("")) {
                return true;
            }
        }
        if (przyciski[0][0].getText().equals(przyciski[1][1].getText()) &&
                przyciski[1][1].getText().equals(przyciski[2][2].getText()) &&
                !przyciski[0][0].getText().equals("")) {
            return true;
        }
        if (przyciski[0][2].getText().equals(przyciski[1][1].getText()) &&
                przyciski[1][1].getText().equals(przyciski[2][0].getText()) &&
                !przyciski[0][2].getText().equals("")) {
            return true;
        }
        return false;
    }

    private void resetGry() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                przyciski[i][j].setText("");
            }
        }
        turaGraczaX = true;
        turaGraczaO = false;
        liczbaRuchow = 0;
        mojaTura = true;
    }

    public static void main(String[] args) {
        String typ = JOptionPane.showInputDialog("Wpisz 'Serwer' aby hostować lub 'Klient' aby się połączyć:");
        String ip = typ.equals("Klient") ? JOptionPane.showInputDialog("Podaj adres IP serwera:") : "localhost";

        SwingUtilities.invokeLater(() -> {
            Main gra = new Main(typ, ip);
            gra.setVisible(true);
        });
    }
}
