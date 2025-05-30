package company.server.db.facade;

import company.server.db.entity.Address;
import company.server.db.jpaRepository.AddressRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AddressFacade {
    private final AddressRepository repository;

    public AddressFacade(AddressRepository repository) {
        this.repository = repository;
    }

    public List<Address> getAll() {
        return repository.findAll();
    }

    public Address save(Address address) {
        return repository.save(address);
    }

    public Optional<Address> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
