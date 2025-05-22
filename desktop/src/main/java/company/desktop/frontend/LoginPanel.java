package company.desktop.frontend;

import company.desktop.controller.LoginController;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Войти");
    private final JLabel statusLabel = new JLabel(" ");

    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Логин:"), gbc);

        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(loginButton, gbc);

        gbc.gridy = 3;
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, gbc);

        LoginController controller = new LoginController(usernameField, passwordField, statusLabel);
        loginButton.addActionListener(controller);
    }
}
