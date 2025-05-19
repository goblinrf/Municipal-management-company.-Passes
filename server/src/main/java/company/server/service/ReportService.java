//package company.server.service;
//
//import company.server.model.ReportDto;
//import org.springframework.stereotype.Service;
//
//import java.time.format.DateTimeFormatter;
//
//@Service
//public class ReportService {
//
//    public String generateReport(ReportDto dto) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//        String start = dto.getStartDate() != null ? dto.getStartDate().format(formatter) : "не указано";
//        String end = dto.getEndDate() != null ? dto.getEndDate().format(formatter) : "не указано";
//
//        return "Отчёт\n------\nДата начала: " + start + "\nДата окончания: " + end;
//    }
//
//    public byte[] export(String reportContent) {
//
//        return reportContent.getBytes(); // UTF-8 по умолчанию
//    }
//}
