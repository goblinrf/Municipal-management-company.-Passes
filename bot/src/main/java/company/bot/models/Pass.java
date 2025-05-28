package company.bot.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pass {
    private Long id;
    private String name;
    private LocalDate limitation;
    private Address address;
    public Pass() {

    }
    public Pass(Long id, String name, LocalDate limitation, Address address) {
        this.id = id;
        this.name = name;
        this.limitation = limitation;
        this.address = address;
    }
    public boolean isExpired() {
        return LocalDate.now().isAfter(limitation);
    }

}
