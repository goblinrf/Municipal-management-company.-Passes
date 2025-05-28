package company.desktop.frontend;

import com.itextpdf.text.pdf.BaseFont;
import company.desktop.model.Address;

import company.desktop.model.PassForReport;
import company.desktop.service.PassService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Font;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
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

            List<PassForReport> allPasses = PassService.fetchAllPassesForReport(sessionCookie,start,end);
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
                    System.out.println(pass.address());
                    Long addrId = pass.address().id();
                    System.out.println(pass.address().id());
                    if (addrId != null && !addressCache.containsKey(addrId)) {
                        Address addr = fetchAddressById(addrId,sessionCookie);
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
            createPdfReport(grouped, addressCache, path);

            JOptionPane.showMessageDialog(this, "PDF-отчёт сохранён: " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при создании отчёта: " + ex.getMessage());
        }
    }

    private Address fetchAddressById(Long id, String sessionCookie ) {
        try {
            URL url = new URL("http://localhost:8081/api/addresses/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Cookie", sessionCookie);
            if (conn.getResponseCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(conn.getInputStream(), Address.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createPdfReport(Map<LocalDate, List<PassForReport>> grouped, Map<Long, Address> addressCache, String path) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        BaseFont baseFont = BaseFont.createFont("desktop/src/main/resources/font/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(baseFont, 18, Font.BOLD);
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(baseFont, 14, Font.BOLD);
        com.itextpdf.text.Font passFont = new com.itextpdf.text.Font(baseFont, 12);

        document.add(new Paragraph("Отчёт по пропускам", titleFont));
        document.add(new Paragraph(" "));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Map.Entry<LocalDate, List<PassForReport>> entry : grouped.entrySet()) {
            document.add(new Paragraph("Дата: " + entry.getKey().format(formatter), headerFont));

            for (PassForReport pass : entry.getValue()) {

                Address addr = addressCache.get(pass.address().id());

                String addrStr = addr != null
                        ? String.format("%s, подъезд %s", addr.street(), addr.entrance())
                        : "Адрес не найден";

                String line = String.format("- %s (тип: %s, вид: %s, код: %s, адрес: %s)",
                        pass.name(), pass.passType(), pass.kindPassType(), pass.code(), addrStr);

                document.add(new Paragraph(line, passFont));
            }
            document.add(new Paragraph(" "));
        }

        document.close();
    }
}
