package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class WaitingScreen extends JFrame {
    public WaitingScreen() {
        String ipAddress = getLocalIPAddress();
        setTitle("Oczekiwanie na klienta...");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Створення фону
        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo.png");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Текст для відображення
        JLabel waitingLabel = new JLabel("Oczekiwanie na klienta...");
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 22));
        waitingLabel.setForeground(Color.WHITE);
        waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel ipLabel = new JLabel("Twój adres IP: " + ipAddress);
        ipLabel.setFont(new Font("Arial", Font.BOLD, 18));
        ipLabel.setForeground(Color.YELLOW);
        ipLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Додавання компонентів
        backgroundPanel.add(waitingLabel, BorderLayout.CENTER);
        backgroundPanel.add(ipLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Nie udało się uzyskać IP";
        }
    }
}
