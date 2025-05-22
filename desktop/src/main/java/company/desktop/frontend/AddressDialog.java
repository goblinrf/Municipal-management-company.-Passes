package company.desktop.frontend;

import company.desktop.model.Address;

import javax.swing.*;
import java.awt.*;

public class AddressDialog extends JPanel {
    private final JTextField streetField = new JTextField(20);
    private final JTextField entranceField = new JTextField(5);

    public AddressDialog(Address address) {
        setLayout(new GridLayout(2, 2, 10, 10));
        add(new JLabel("Улица:"));
        add(streetField);
        add(new JLabel("Подъезд:"));
        add(entranceField);

        if (address != null) {
            streetField.setText(address.street());
            entranceField.setText(address.entrance());
        }
    }

    public Address getAddress() {
        return new Address(null, streetField.getText().trim(), entranceField.getText().trim());
    }
}
