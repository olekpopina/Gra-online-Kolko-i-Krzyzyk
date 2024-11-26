package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;

public class KolkoIKrzyzykMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame wyborOkna = new JFrame("Wybierz tryb gry");
            wyborOkna.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            wyborOkna.setSize(300, 200);
            wyborOkna.setLayout(new GridLayout(4, 1));

            JButton localGameButton = new JButton("Gra na jednym komputerze");
            JButton vsComputerButton = new JButton("Gra przeciw komputerowi");
            JButton serverButton = new JButton("Gra jako serwer");
            JButton clientButton = new JButton("Gra jako klient");

            localGameButton.addActionListener(e -> {
                wyborOkna.dispose();
                new LocalGame();
            });

            vsComputerButton.addActionListener(e -> {
                wyborOkna.dispose();
                new VsComputerGame();
            });

            serverButton.addActionListener(e -> {
                wyborOkna.dispose();
                new ServerGame();
            });

            clientButton.addActionListener(e -> {
                wyborOkna.dispose();
                String ip = JOptionPane.showInputDialog("Podaj IP serwera:");
                new ClientGame(ip);
            });

            wyborOkna.add(localGameButton);
            wyborOkna.add(vsComputerButton);
            wyborOkna.add(serverButton);
            wyborOkna.add(clientButton);
            wyborOkna.setVisible(true);
        });
    }
}
