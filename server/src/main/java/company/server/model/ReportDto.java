package company.server.model;

import java.time.LocalDateTime;

public record ReportDto(LocalDateTime startDate, LocalDateTime endDate) {}
