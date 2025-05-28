package company.desktop.frontend;

import company.desktop.LoginFrame;
import company.desktop.frontend.AddressPanel;
import company.desktop.frontend.PassPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final JPanel contentPanel = new JPanel(new CardLayout());

    public MainFrame(String token) {
        super("Панель управления");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addressBtn = new JButton("Адреса");
        JButton passBtn = new JButton("Пропуска");
        JButton reportBtn = new JButton("Отчёты");
        JButton logoutBtn = new JButton("Выход");
        header.add(addressBtn);
        header.add(passBtn);
        header.add(reportBtn);
        header.add(logoutBtn);


        contentPanel.add(new AddressPanel(token), "addresses");
        contentPanel.add(new PassPanel(token), "passes");
        contentPanel.add(new ReportPanel(token), "report");

        addressBtn.addActionListener(e -> switchTo("addresses"));
        passBtn.addActionListener(e -> switchTo("passes"));
        reportBtn.addActionListener(e -> switchTo("report"));
        logoutBtn.addActionListener(e -> {
            company.desktop.service.AuthService.logout();
            dispose();
            new LoginFrame();
        });

        add(header, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void switchTo(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }
}
