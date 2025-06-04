package company.desktop.model;

import company.desktop.model.enums.KindPassType;
import company.desktop.model.enums.PassType;

import java.time.LocalDate;

public record PassForReport(
        Long id,
        String name,
        PassType passType,
        KindPassType kindPassType,
        Long code,
        LocalDate limitation,
        Address address,
        Long count_update
) {
    public boolean isActive() {
        return !LocalDate.now().isAfter(limitation);
    }
}
