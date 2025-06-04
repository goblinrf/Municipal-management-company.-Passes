package company.bot.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pass {
    private Long id;
    private String name;
    private LocalDate limitation;
    private Address address;
    private Long count_update;

    public Pass() {}

    public Pass(Long id, String name, LocalDate limitation, Address address) {
        this.id = id;
        this.name = name;
        this.limitation = limitation;
        this.address = address;
    }

    public int isExpired() {
        if (count_update == -1) {
            return -1;
        }
        return !LocalDate.now().isAfter(limitation)?0:1;
    }
}
