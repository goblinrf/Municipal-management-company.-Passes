package company.desktop.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import company.desktop.model.Address;
import company.desktop.model.PassForReport;

import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportService {
    public static void createPdfReport(Map<LocalDate, List<PassForReport>> grouped, Map<Long, Address> addressCache, String path) throws Exception {
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
