//package company.server.controller;
//
//import company.server.model.ReportDto;
//import company.server.service.ReportService;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/reports")
//public class ReportController {
//
//    private final ReportService reportService;
//
//    public ReportController(ReportService service) {
//        this.reportService = service;
//    }
//
//    @PostMapping
//    public ResponseEntity<String> generate(@RequestBody ReportDto reportDto) {
//        String result = reportService.generateReport(reportDto);
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping("/export")
//    public ResponseEntity<byte[]> export(@RequestBody ReportDto reportDto) {
//        String reportText = reportService.generateReport(reportDto);
//        byte[] fileContent = reportService.export(reportText);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.txt")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(fileContent);
//    }
//}
