package company.desktop.frontend;

import company.desktop.model.Address;
import company.desktop.model.Pass;
import company.desktop.model.enums.KindPassType;
import company.desktop.service.AddressService;
import company.desktop.service.PassService;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PassPanel extends JPanel {
    private final String token;
    private final JTable table;
    private final DefaultTableModel model;
    private final JComboBox<String> statusFilter;
    private JButton deactivateBtn;
    private JButton applyBtn;
    private JButton cancelBtn;
    private  JButton refreshBtn;
    private boolean selectionMode = false;
    private List<Address> addresses;

    public PassPanel(String token) {
        this.token = token;
        setLayout(new BorderLayout());

        // Верхняя панель с фильтром
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusFilter = new JComboBox<>(new String[]{"Все", "Активен", "Истёк", "Деактивирован"});
        statusFilter.addActionListener(e -> loadPasses());

        topPanel.add(new JLabel("Фильтр по статусу:"));
        topPanel.add(statusFilter);

        deactivateBtn = new JButton("Деактивировать");
        applyBtn = new JButton("Применить");
        cancelBtn = new JButton("Отмена");
        refreshBtn = new JButton("Обновить");

        applyBtn.setVisible(false);
        cancelBtn.setVisible(false);


        model = new DefaultTableModel(new Object[]{"ID", "Название", "Тип", "Вид", "Код", "Срок", "Статус", "⋯", "OBJ"}, 0);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        deactivateBtn.addActionListener(e -> {
            selectionMode = true;
            table.setRowSelectionAllowed(true);
            deactivateBtn.setVisible(false);
            applyBtn.setVisible(true);
            cancelBtn.setVisible(true);
        });
        refreshBtn.addActionListener(e -> loadPasses());
        applyBtn.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            List<Long> idsToDeactivate = new ArrayList<>();
            for (int row : selectedRows) {
                Pass p = (Pass) model.getValueAt(row, 8);
                if (KindPassType.ONE_TIME.equals(p.kindPassType()) && p.count_update() != -1) {
                    idsToDeactivate.add(p.id());
                }
            }
            if (!idsToDeactivate.isEmpty()) {
                PassService.deactivatePasses(idsToDeactivate, token);
                loadPasses();
            }
            resetSelectionMode();
        });

        cancelBtn.addActionListener(e -> resetSelectionMode());

        topPanel.add(deactivateBtn);
        topPanel.add(applyBtn);
        topPanel.add(cancelBtn);
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        // Скрываем колонку объекта Pass
        table.removeColumn(table.getColumnModel().getColumn(8));
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton("Добавить пропуск");
        addBtn.addActionListener(e -> {
            List<Address> freshAddresses = AddressService.fetchAll(token);
            openPassDialog(null, freshAddresses);
        });
        add(addBtn, BorderLayout.SOUTH);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());

                if (col == 7) { // колонка действий
                    Pass pass = (Pass) model.getValueAt(row, 8);

                    String[] actions;
                    if (pass.count_update() != null && pass.count_update() == -1) {
                        // Пропуск деактивирован — только удаление
                        actions = new String[]{"Удалить"};
                    } else {
                        actions = new String[]{"Редактировать", "Удалить", "Скопировать код", "Продлить"};
                    }

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

                    if (pass.count_update() != null && pass.count_update() == -1) {
                        // Деактивирован — только удаление
                        if (result == 0) {
                            PassService.deletePass(pass.id(), token);
                            loadPasses();
                        }
                    } else {
                        // Активен или истёк — все действия
                        if (result == 0) {
                            List<Address> freshAddresses = AddressService.fetchAll(token);
                            openPassDialog(pass, freshAddresses);
                        }
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
                        if (result == 3) {
                            PassRenewDialog renewDialog = new PassRenewDialog(pass);
                            int res = JOptionPane.showConfirmDialog(
                                    PassPanel.this, renewDialog,
                                    "Продление пропуска",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.PLAIN_MESSAGE
                            );
                            if (res == JOptionPane.OK_OPTION) {
                                PassService.extendPass(pass.id(), renewDialog.getUpdatedPass(), token);
                                loadPasses();
                            }
                        }
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
            int filterActive;
            if ("Деактивирован".equals(selected)) {
                filterActive = -1;
            } else {
                filterActive = "Активен".equals(selected) ? 0 : 1;
            }
            passes = passes.stream()
                    .filter(p -> p.isActive() == filterActive)
                    .toList();
        }

        model.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Pass pass : passes) {
            String status;

            if (pass.count_update() != null && pass.count_update() == -1) {
                status = "Деактивирован";
            } else {
                status = pass.isActive() == 0 ? "Активен" : "Истёк";
                if (pass.count_update() != null && pass.count_update() > 0) {
                    status += " (Обновлён " + pass.count_update() + "-раз)";
                }
            }
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

    private void resetSelectionMode() {
        selectionMode = false;
        table.clearSelection();
        table.setRowSelectionAllowed(false);
        deactivateBtn.setVisible(true);
        applyBtn.setVisible(false);
        cancelBtn.setVisible(false);
    }
}
