package company.server.model;

import company.server.db.entity.Address;
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

    public AddressDto() {

    }
}
