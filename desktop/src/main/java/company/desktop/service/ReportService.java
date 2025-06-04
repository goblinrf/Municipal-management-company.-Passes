package company.desktop.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import company.desktop.model.Address;
import company.desktop.model.Pass;
import company.desktop.model.PassForReport;

import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportService {
    public static void createPdfReport(Map<LocalDate, List<PassForReport>> grouped, Map<Long, Address> addressCache, String path, String sessionToken) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        BaseFont baseFont = BaseFont.createFont("desktop/src/main/resources/font/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(baseFont, 18, Font.BOLD);
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(baseFont, 14, Font.BOLD);
        com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(baseFont, 12);
        com.itextpdf.text.Font passFont = new com.itextpdf.text.Font(baseFont, 12);

        document.add(new Paragraph("Отчёт по пропускам", titleFont));
        document.add(new Paragraph(" "));

        // Подсчёт статистики
        List<Pass> list_passes = PassService.fetchAll(sessionToken);
        int total = list_passes.size();
        int deactivated = 0;
        for (Pass pass : list_passes) {
            if (pass.count_update() == -1){
                deactivated +=1;
            }
        }

        int totalExpiring = 0;

        document.add(new Paragraph("Общая статистика:", headerFont));
        document.add(new Paragraph("Всего пропусков: " + total + " (100%)", normalFont));
        document.add(new Paragraph("Деактивировано: " + deactivated + " (" + percent(deactivated, total) + "%)", normalFont));
        document.add(new Paragraph(" "));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Map.Entry<LocalDate, List<PassForReport>> entry : grouped.entrySet()) {
            List<PassForReport> passes = entry.getValue();
            int dailyCount = passes.size();
            totalExpiring += dailyCount;

            document.add(new Paragraph("Дата: " + entry.getKey().format(formatter) + " — " + dailyCount + " шт. (" + percent(dailyCount, total) + "%)", headerFont));

            for (PassForReport pass : passes) {
                Address addr = pass.address();

                String addrStr = addr != null
                        ? String.format("%s, подъезд %s", addr.street(), addr.entrance())
                        : "Адрес не найден";

                String line = String.format("- %s (тип: %s, вид: %s, код: %s, адрес: %s)",
                        pass.name(), pass.passType(), pass.kindPassType(), pass.code(), addrStr);

                document.add(new Paragraph(line, passFont));
            }
            document.add(new Paragraph(" "));
        }

        document.add(new Paragraph("Суммарно истекает за период: " + totalExpiring + " (" + percent(totalExpiring, total) + "%)", normalFont));
        document.add(new Paragraph(" "));

        document.close();
    }

    private static String percent(int part, int total) {
        if (total == 0) return "0.0";
        return String.format("%.1f", (part * 100.0) / total);
    }

}