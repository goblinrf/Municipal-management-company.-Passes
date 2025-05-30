package company.desktop.controller;

import company.desktop.frontend.MainFrame;
import company.desktop.model.LoginResponse;
import company.desktop.service.AuthService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController implements ActionListener {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;
    private final JFrame loginFrame; // передаём ссылку на окно логина

    public LoginController(JTextField usernameField, JPasswordField passwordField, JLabel statusLabel, JFrame loginFrame) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.statusLabel = statusLabel;
        this.loginFrame = loginFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Введите логин и пароль.");
            return;
        }

        LoginResponse response = AuthService.login(username, password);
        if (response.success()) {
            statusLabel.setText("Вход успешен!");

            // Закрыть окно логина
            loginFrame.dispose();

            // Открыть главное окно и передать токен
            SwingUtilities.invokeLater(() -> new MainFrame(response.token()));
        } else {
            statusLabel.setText("Ошибка: " + response.message());
        }
    }
}
