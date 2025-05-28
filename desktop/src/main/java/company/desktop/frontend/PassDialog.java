package company.desktop.frontend;


import company.desktop.model.Address;
import company.desktop.model.Pass;
import company.desktop.model.enums.KindPassType;
import company.desktop.model.enums.PassType;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PassDialog extends JPanel {
    private final JTextField titleField = new JTextField(15);
    private final JComboBox<PassType> typeBox = new JComboBox<>(PassType.values());
    private final JComboBox<KindPassType> kindBox = new JComboBox<>(KindPassType.values());
    private final JTextField codeField = new JTextField(6);
    private final JTextField limitationField = new JTextField(10);
    private final JComboBox<Address> addressBox;


    public PassDialog(Pass pass, List<Address> addresses) {
        this.addressBox = new JComboBox<>(addresses.toArray(new Address[0]));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Название:"), gbc);
        gbc.gridx = 1;
        add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Тип:"), gbc);
        gbc.gridx = 1;
        add(typeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Вид:"), gbc);
        gbc.gridx = 1;
        add(kindBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Код:"), gbc);
        gbc.gridx = 1;
        codeField.setEditable(false);
        add(codeField, gbc);
        JButton genBtn = new JButton("Сгенерировать");
        genBtn.addActionListener(e -> codeField.setText(generateCode()));
        gbc.gridx = 2;
        add(genBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Срок действия (yyyy-MM-dd hh:mm):"), gbc);
        gbc.gridx = 1;
        add(limitationField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1;
        add(addressBox, gbc);

        if (pass != null) {
            titleField.setText(pass.name());
            typeBox.setSelectedItem(pass.passType());
            kindBox.setSelectedItem(pass.kindPassType());
            codeField.setText(String.valueOf(pass.code()));
            limitationField.setText(String.valueOf(pass.limitation()));
            // Выбрать адрес по id, если доступен
            for (int i = 0; i < addressBox.getItemCount(); i++) {
                if (addressBox.getItemAt(i).id().equals(pass.addressId())) {
                    addressBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            codeField.setText(generateCode());
            limitationField.setText(String.valueOf(LocalDate.now().plusDays(7)));
        }
    }

    public Pass getPass() {
        String title = titleField.getText().trim();
        PassType type = (PassType) typeBox.getSelectedItem();
        KindPassType kind = (KindPassType) kindBox.getSelectedItem();
        long code = Long.parseLong(codeField.getText().trim());
        LocalDate limitation = LocalDate.parse(limitationField.getText().trim());
        Long address = ((Address) addressBox.getSelectedItem()).id();

        return new Pass(null, title, type, kind, code, limitation, address);
    }

    private String generateCode() {
        return String.valueOf((int) (100000 + Math.random() * 900000));
    }
}
