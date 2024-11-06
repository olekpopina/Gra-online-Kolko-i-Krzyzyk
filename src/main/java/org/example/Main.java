package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Main extends JFrame {
    private JButton[] buttons = new JButton[9];
    private int turn = 0;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isServer;

    public Main(String ipAddress, boolean isServer) {
        this.isServer = isServer;
        setTitle("Kółko-krzyżyk");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));


        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 60));
            buttons[i].addActionListener(new ButtonClickListener(i));
            add(buttons[i]);
        }

        if (isServer) {
            startServer();
        } else {
            connectToServer(ipAddress);
        }
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("Serwer uruchomiony, oczekiwanie na połączenie...");
                socket = serverSocket.accept();
                System.out.println("Klient połączony: " + socket.getInetAddress());
                setupStreams();
                listenForMoves();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void connectToServer(String ipAddress) {
        new Thread(() -> {
            try {
                socket = new Socket(ipAddress, 12345);
                setupStreams();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupStreams() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMoves() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split(",");
                    int index = Integer.parseInt(parts[0]);
                    String symbol = parts[1];
                    updateButton(index, symbol);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }



    private void updateButton(int index, String playerSymbol) {
        SwingUtilities.invokeLater(() -> {
            buttons[index].setText(playerSymbol);
            turn++;
            checkForWin();
        });
    }

    private class ButtonClickListener implements ActionListener {
        private int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[index].getText().equals("") && ((isServer && turn % 2 == 0) || (!isServer && turn % 2 == 1))) {
                String playerSymbol = isServer ? "X" : "O";
                buttons[index].setText(playerSymbol);
                out.println(index + "," + playerSymbol);
                turn++;
                checkForWin();
            }
        }
    }


    private void checkForWin() {

        int[][] winningConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] condition : winningConditions) {
            if (buttons[condition[0]].getText().equals(buttons[condition[1]].getText()) &&
                    buttons[condition[1]].getText().equals(buttons[condition[2]].getText()) &&
                    !buttons[condition[0]].getText().equals("")) {
                String winner = buttons[condition[0]].getText();
                JOptionPane.showMessageDialog(this, "Gratulacje! Wygrał " + winner);
                resetGame();
                return;
            }
        }

        if (turn == 9) {
            JOptionPane.showMessageDialog(this, "Remis!");
            resetGame();
        }
    }

    private void resetGame() {
        turn = 0;
        for (JButton button : buttons) {
            button.setText("");
        }
    }

    public static void main(String[] args) {
        String ipAddress = JOptionPane.showInputDialog("Wprowadź lokalne IP serwera (domyślnie 192.168.0.16):");

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = "192.168.0.16";
        }

        int option = JOptionPane.showConfirmDialog(null, "Czy chcesz być serwerem?");
        boolean isServer = (option == JOptionPane.YES_OPTION);

        String finalIpAddress = ipAddress;
        SwingUtilities.invokeLater(() -> {
            Main game = new Main(finalIpAddress, isServer);
            game.setVisible(true);
        });
    }
}


