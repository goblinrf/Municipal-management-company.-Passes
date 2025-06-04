package company.desktop.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import company.desktop.model.Pass;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static company.desktop.service.PassService.generateCode;

public class PassRenewDialog extends JPanel {
    private final JTextField codeField;
    private final JButton generateCodeButton;
    private final JXDatePicker datePicker;
    private final Pass original;
    private static final ObjectMapper mapper = new ObjectMapper();


    public PassRenewDialog(Pass pass) {
        this.original = pass;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Метка даты
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Новая дата окончания:"), gbc);

        // Календарь (JXDatePicker)
        gbc.gridx = 1;
        datePicker = new JXDatePicker();
        datePicker.setDate(Date.from(pass.limitation().plusDays(30)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        add(datePicker, gbc);

        // Метка кода
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Новый код:"), gbc);

        // Поле кода
        gbc.gridx = 1;
        codeField = new JTextField(String.valueOf(generateCode()));
        add(codeField, gbc);

        // Кнопка генерации
        gbc.gridx = 2;
        generateCodeButton = new JButton("Сгенерировать");
        generateCodeButton.addActionListener(e -> codeField.setText(String.valueOf(generateCode())));
        add(generateCodeButton, gbc);
    }

    public Map<String, Object> getUpdatedPass() {
        Date selectedDate = datePicker.getDate();
        if (selectedDate == null) return null;

        LocalDate newDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Map<String, Object> data = new HashMap<>();
        if (newDate != null) {
            data.put("limitation", newDate.toString());
        }
        String code = codeField.getText();
        if (code != null && code.matches("\\d{6}")) {
            data.put("code", code);
        }

        return data;

    }
}
