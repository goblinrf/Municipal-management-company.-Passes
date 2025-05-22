package company.desktop;

import company.desktop.frontend.LoginPanel;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        super("Авторизация");

        LoginPanel panel = new LoginPanel();
        this.add(panel);

        this.setSize(400, 250);
        this.setMinimumSize(new Dimension(350, 200));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
