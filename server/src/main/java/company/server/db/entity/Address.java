package company.server.db.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Address extends BaseEntity {
    @Setter
    private String street;
    @Setter
    private String entrance;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<Pass> passes = new ArrayList<>();
}
