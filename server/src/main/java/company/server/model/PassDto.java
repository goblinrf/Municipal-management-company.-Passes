package company.server.model;

import company.server.model.enums.KindPassType;
import company.server.model.enums.PassType;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassDto {
    private Long addressId;
    private PassType passType;
    private LocalDate limitation;
    private KindPassType kindPassType;
    private String name;
    private Long code;

    public PassDto(
            Long addressId,
            PassType passType,
            LocalDate limitation,
            String name,
            KindPassType kindPassType,
            Long code) {
        this.addressId = addressId;
        this.passType = passType;
        this.limitation = limitation;
        this.name = name;
        this.kindPassType = kindPassType;
        this.code = code;
    }

    public PassDto(
            AddressDto address, PassType passType, LocalDate limitation, String name, KindPassType kindPassType) {
        this.addressId = addressId;
        this.passType = passType;
        this.limitation = limitation;
        this.name = name;
        this.kindPassType = kindPassType;
        this.code = 0L;
    }
    public PassDto() {}
}
