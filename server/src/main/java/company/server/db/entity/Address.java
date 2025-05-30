package company.server.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Address extends BaseEntity {
    @Setter
    private String street;

    @Setter
    private String entrance;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pass> passes = new ArrayList<>();
}
