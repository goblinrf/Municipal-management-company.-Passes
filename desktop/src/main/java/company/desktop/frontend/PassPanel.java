package company.desktop.frontend;

import javax.swing.*;
import java.awt.*;

public class PassPanel extends JPanel {
    public PassPanel(String token) {
        setLayout(new BorderLayout());
        add(new JLabel("Раздел 'Пропуска' в разработке.", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
