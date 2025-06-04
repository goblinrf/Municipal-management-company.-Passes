package company.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import company.desktop.model.Address;
import company.desktop.model.enums.KindPassType;
import company.desktop.model.enums.PassType;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Pass(
        Long id,
        String name,
        PassType passType,
        KindPassType kindPassType,
        Long code,
        LocalDate limitation,
        Long addressId,
        Long count_update
) {
    public int isActive() {
        if (count_update == -1) {
            return -1;
        }

        return !LocalDate.now().isAfter(limitation)?0:1;
    }
}
