package company.server.db.entity;

import company.server.model.enums.KindPassType;
import company.server.model.enums.PassType;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pass extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private PassType passType;

    private LocalDate limitation;

    @Enumerated(EnumType.STRING)
    private KindPassType kindPassType;

    private String name;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    private long code;
}
