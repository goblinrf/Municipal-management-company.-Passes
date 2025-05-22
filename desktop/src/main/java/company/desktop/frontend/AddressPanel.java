package company.desktop.frontend;

import company.desktop.model.Address;
import company.desktop.service.AddressService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AddressPanel extends JPanel {
    private final DefaultTableModel model;
    private final String token;
    private final JTable table;

    public AddressPanel(String token) {
        this.token = token;
        setLayout(new BorderLayout());

        // Добавлена колонка "ID" в начало
        model = new DefaultTableModel(new Object[]{"ID", "Улица", "Подъезд", "Действия", "OBJ"}, 0);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

// Удаляем визуальное отображение колонки "OBJ"
        table.removeColumn(table.getColumnModel().getColumn(4));

        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton("Добавить адрес");
        addBtn.addActionListener(e -> openAddressDialog(null));
        add(addBtn, BorderLayout.SOUTH);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());

                // Столбец с действиями теперь индекс 3
                if (col == 3) {
                    String[] actions = {"Редактировать", "Удалить"};
                    int result = JOptionPane.showOptionDialog(
                            AddressPanel.this,
                            "Выберите действие:",
                            "Действия с адресом",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            actions,
                            actions[0]
                    );
                    // Адрес берём из скрытого объекта в конце строки
                    Address address = (Address) model.getValueAt(row, 4);
                    if (result == 0) openAddressDialog(address);
                    if (result == 1) {
                        AddressService.deleteAddress(address.id(), token);
                        loadAddresses();
                    }
                }
            }
        });

        loadAddresses();
    }

    private void loadAddresses() {
        List<Address> addresses = AddressService.fetchAll(token);
        model.setRowCount(0);
        for (Address addr : addresses) {
            // Добавили ID как первый столбец
            model.addRow(new Object[]{addr.id(), addr.street(), addr.entrance(), "⋯", addr});
        }
    }

    private void openAddressDialog(Address address) {
        AddressDialog dialog = new AddressDialog(address);
        int result = JOptionPane.showConfirmDialog(
                this, dialog, address == null ? "Добавить адрес" : "Редактировать адрес",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            Address updated = dialog.getAddress();
            if (address == null) {
                AddressService.createAddress(updated, token);
            } else {
                AddressService.updateAddress(address.id(), updated, token);
            }
            loadAddresses();
        }
    }
}
