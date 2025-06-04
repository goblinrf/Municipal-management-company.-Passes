package company.desktop.frontend;

import company.desktop.model.Address;
import company.desktop.model.Pass;
import company.desktop.model.enums.KindPassType;
import company.desktop.model.enums.PassType;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static company.desktop.service.PassService.generateCode;

public class PassDialog extends JPanel {
    private final JTextField titleField = new JTextField(15);
    private final JComboBox<PassType> typeBox = new JComboBox<>(PassType.values());
    private final JComboBox<KindPassType> kindBox = new JComboBox<>(KindPassType.values());
    private final JTextField codeField = new JTextField(6);
    private final JXDatePicker limitationPicker = new JXDatePicker();
    private final JComboBox<Address> addressBox;

    public PassDialog(Pass pass, List<Address> addresses) {
        this.addressBox = new JComboBox<>(addresses.toArray(new Address[0]));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Название
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Название:"), gbc);
        gbc.gridx = 1;
        add(titleField, gbc);

        // Тип
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Тип:"), gbc);
        gbc.gridx = 1;
        add(typeBox, gbc);

        // Вид
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Вид:"), gbc);
        gbc.gridx = 1;
        add(kindBox, gbc);

        // Код
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Код:"), gbc);
        gbc.gridx = 1;
        codeField.setEditable(false);
        add(codeField, gbc);
        JButton genBtn = new JButton("Сгенерировать");
        genBtn.addActionListener(e -> codeField.setText(String.valueOf(generateCode())));
        gbc.gridx = 2;
        add(genBtn, gbc);

        // Срок действия
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Срок действия:"), gbc);
        gbc.gridx = 1;
        add(limitationPicker, gbc);

        // Адрес
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1;
        add(addressBox, gbc);

        // Инициализация значений
        if (pass != null) {
            titleField.setText(pass.name());
            typeBox.setSelectedItem(pass.passType());
            kindBox.setSelectedItem(pass.kindPassType());
            codeField.setText(String.valueOf(pass.code()));
            limitationPicker.setDate(Date.from(pass.limitation().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            limitationPicker.setEnabled(false);
            genBtn.setEnabled(false);

            for (int i = 0; i < addressBox.getItemCount(); i++) {
                if (addressBox.getItemAt(i).id().equals(pass.addressId())) {
                    addressBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            codeField.setText(String.valueOf(generateCode()));
            limitationPicker.setDate(Date.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }

    public Pass getPass() {
        String title = titleField.getText().trim();
        PassType type = (PassType) typeBox.getSelectedItem();
        KindPassType kind = (KindPassType) kindBox.getSelectedItem();
        long code = Long.parseLong(codeField.getText().trim());
        Date date = limitationPicker.getDate();
        LocalDate limitation = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Long address = ((Address) addressBox.getSelectedItem()).id();

        return new Pass( null, title, type, kind, code, limitation, address,0L);
    }


}
