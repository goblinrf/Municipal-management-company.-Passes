package company.desktop.frontend;

import company.desktop.model.Address;
import company.desktop.model.PassForReport;
import company.desktop.service.AddressService;
import company.desktop.service.PassService;
import company.desktop.service.ReportService;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ReportPanel extends JPanel {
    private final String sessionCookie;
    private final JXDatePicker startDatePicker;
    private final JXDatePicker endDatePicker;

    public ReportPanel(String sessionCookie) {
        this.sessionCookie = sessionCookie;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Формирование отчёта по истекающим пропускам");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 0, 20, 0));

        form.add(new JLabel("Дата начала:"));
        startDatePicker = new JXDatePicker();
        form.add(startDatePicker);

        form.add(new JLabel("Дата конца:"));
        endDatePicker = new JXDatePicker();
        form.add(endDatePicker);

        JButton generateBtn = new JButton("Сформировать PDF-отчёт");
        generateBtn.addActionListener(e -> generateReport());

        form.add(new JLabel());
        form.add(generateBtn);

        add(form, BorderLayout.CENTER);
    }

    private void generateReport() {
        try {
            Date startDate = startDatePicker.getDate();
            Date endDate = endDatePicker.getDate();

            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(this, "Выберите обе даты.");
                return;
            }

            LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            List<PassForReport> allPasses = PassService.fetchAllPassesForReport(sessionCookie, start, end);
            Map<LocalDate, List<PassForReport>> grouped = allPasses.stream()
                    .filter(p -> {
                        LocalDate date = p.limitation();
                        return !date.isBefore(start) && !date.isAfter(end);
                    })
                    .collect(Collectors.groupingBy(PassForReport::limitation,
                            TreeMap::new, Collectors.toList()));

            Map<Long, Address> addressCache = new HashMap<>();
            for (List<PassForReport> list : grouped.values()) {
                for (PassForReport pass : list) {
                    Long addrId = pass.address().id();
                    if (addrId != null && !addressCache.containsKey(addrId)) {
                        Address addr = AddressService.fetchAddressById(addrId, sessionCookie);
                        if (addr != null) {
                            addressCache.put(addrId, addr);
                        }
                    }
                }
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("pass_report.pdf"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            String path = chooser.getSelectedFile().getAbsolutePath();
            ReportService.createPdfReport(grouped, addressCache, path,sessionCookie);

            JOptionPane.showMessageDialog(this, "PDF-отчёт сохранён: " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при создании отчёта: " + ex.getMessage());
        }
    }


}
