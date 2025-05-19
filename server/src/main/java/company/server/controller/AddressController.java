package company.server.controller;

import company.server.db.entity.Address;
import company.server.db.facade.AddressFacade;
import java.util.List;
import java.util.Optional;

import company.server.model.AddressDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressFacade addressFacade;

    public AddressController(AddressFacade facade) {
        this.addressFacade = facade;
    }

    @GetMapping
    public List<Address> getAll() {
        return addressFacade.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AddressDto addressDto) {
        Optional<Address> optionalAddress = addressFacade.findById(id);
        if (optionalAddress.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Адрес не найден");
        }

        Address address = optionalAddress.get();
        address.setStreet(addressDto.getStreet());
        address.setEntrance(addressDto.getEntrance());
        addressFacade.save(address);

        return ResponseEntity.ok("Адрес обновлён");
    }


    @PostMapping
    public  ResponseEntity<?> create(@RequestBody AddressDto addressDto) {
        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setEntrance(addressDto.getEntrance());
        addressFacade.save(address);
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь создан");
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        addressFacade.delete(id);
    }
}
