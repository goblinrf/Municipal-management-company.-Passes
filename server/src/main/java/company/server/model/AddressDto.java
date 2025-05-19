package company.server.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private String street;
    private String entrance;

    public AddressDto(String address, String entrance) {
        this.street = address;
        this.entrance = entrance;
    }
}
