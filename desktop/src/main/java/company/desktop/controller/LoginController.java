package company.desktop.controller;

import company.desktop.model.LoginResponse;
import company.desktop.service.AuthService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController implements ActionListener {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    public LoginController(JTextField usernameField, JPasswordField passwordField, JLabel statusLabel) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.statusLabel = statusLabel;
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
            // TODO: Открыть главное окно
        } else {
            statusLabel.setText("Ошибка: " + response.message());
        }
    }
}
