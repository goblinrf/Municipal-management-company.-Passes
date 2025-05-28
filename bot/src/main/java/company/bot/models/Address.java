package company.bot.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    private Long id;
    private String street;
    private String entrance;

    public Address(Long id, String street, String entrance) {
        this.id = id;
        this.street = street;
        this.entrance = entrance;
    }
    public Address(){}
}
