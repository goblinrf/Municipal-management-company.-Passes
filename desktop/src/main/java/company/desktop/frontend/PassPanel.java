package company.desktop.frontend;

import company.desktop.model.Address;
import company.desktop.model.Pass;
import company.desktop.service.AddressService;
import company.desktop.service.PassService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PassPanel extends JPanel {
    private final String token;
    private final JTable table;
    private final DefaultTableModel model;
    private final JComboBox<String> statusFilter;
    private List<Address> addresses;

    public PassPanel(String token) {
        this.token = token;
        setLayout(new BorderLayout());

        // Верхняя панель с фильтром
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusFilter = new JComboBox<>(new String[]{"Все", "Активен", "Истёк"});
        statusFilter.addActionListener(e -> loadPasses());

        topPanel.add(new JLabel("Фильтр по статусу:"));
        topPanel.add(statusFilter);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Название", "Тип", "Вид", "Код", "Срок", "Статус", "⋯", "OBJ"}, 0);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Скрываем колонку объекта Pass
        table.removeColumn(table.getColumnModel().getColumn(8));
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton("Добавить пропуск");
        addresses = AddressService.fetchAll(token);
        addBtn.addActionListener(e -> openPassDialog(null, addresses));
        add(addBtn, BorderLayout.SOUTH);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());

                if (col == 7) { // колонка действий
                    String[] actions = {"Редактировать", "Удалить", "Скопировать код"};
                    int result = JOptionPane.showOptionDialog(
                            PassPanel.this,
                            "Выберите действие:",
                            "Действия с пропуском",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            actions,
                            actions[0]
                    );
                    Pass pass = (Pass) model.getValueAt(row, 8);
                    if (result == 0) openPassDialog(pass, addresses);
                    if (result == 1) {
                        PassService.deletePass(pass.id(), token);
                        loadPasses();
                    }
                    if (result == 2) {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                new StringSelection(String.valueOf(pass.code())), null
                        );
                        JOptionPane.showMessageDialog(PassPanel.this, "Код скопирован");
                    }
                }
            }
        });

        loadPasses();
    }

    private void loadPasses() {
        List<Pass> passes = PassService.fetchAll(token);
        String selected = (String) statusFilter.getSelectedItem();

        if (!"Все".equals(selected)) {
            boolean filterActive = "Активен".equals(selected);
            passes = passes.stream()
                    .filter(p -> p.isActive() == filterActive)
                    .toList();
        }

        model.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Pass pass : passes) {
            String status = pass.isActive() ? "Активен" : "Истёк";
            model.addRow(new Object[]{
                    pass.id(),
                    pass.name(),
                    pass.passType(),
                    pass.kindPassType(),
                    pass.code(),
                    pass.limitation().format(formatter),
                    status,
                    "⋯",
                    pass
            });
        }
    }

    private void openPassDialog(Pass pass, List<Address> addresses) {
        PassDialog dialog = new PassDialog(pass, addresses);
        int result = JOptionPane.showConfirmDialog(
                this, dialog,
                pass == null ? "Добавить пропуск" : "Редактировать пропуск",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            Pass updated = dialog.getPass();
            if (pass == null) {
                PassService.createPass(updated, token);
            } else {
                PassService.updatePass(pass.id(), updated, token);
            }
            loadPasses();
        }
    }
}
