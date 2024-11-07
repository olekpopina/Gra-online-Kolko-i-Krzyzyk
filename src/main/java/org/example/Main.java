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
                mojaTura = true;
            } else {
                socket = new Socket(ip, 5000);
                mojaTura = false;
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

        kliknietyPrzycisk.setText(turaGraczaX ? "X" : "O");
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
            String zwyciezca = turaGraczaX ? "X" : "O";
            JOptionPane.showMessageDialog(this, "Wygrywa: " + zwyciezca);
            resetGry();
        } else if (liczbaRuchow == 9) {
            JOptionPane.showMessageDialog(this, "Remis!");
            resetGry();
        }

        turaGraczaX = !turaGraczaX;
    }

    private void nasluchujRuchy() {
        try {
            String linia;
            while ((linia = in.readLine()) != null) {
                String[] ruch = linia.split(",");
                int x = Integer.parseInt(ruch[0]);
                int y = Integer.parseInt(ruch[1]);

                przyciski[x][y].setText(turaGraczaX ? "O" : "X");
                mojaTura = true;
                liczbaRuchow++;

                if (czyKtosWygral()) {
                    String zwyciezca = turaGraczaX ? "O" : "X";
                    JOptionPane.showMessageDialog(this, "Wygrywa: " + zwyciezca);
                    resetGry();
                } else if (liczbaRuchow == 9) {
                    JOptionPane.showMessageDialog(this, "Remis!");
                    resetGry();
                }

                turaGraczaX = !turaGraczaX;  // Zmiana tury
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
        liczbaRuchow = 0;
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


