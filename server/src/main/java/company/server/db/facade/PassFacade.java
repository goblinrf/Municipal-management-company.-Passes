package company.server.db.facade;

import company.server.db.entity.Address;
import company.server.db.entity.Pass;
import company.server.db.jpaRepository.AddressRepository;
import company.server.db.jpaRepository.PassRepository;
import company.server.model.PassDto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PassFacade {

    private final PassRepository passRepository;
    private final AddressRepository addressRepository;

    public PassFacade(PassRepository passRepository, AddressRepository addressRepository) {
        this.passRepository = passRepository;
        this.addressRepository = addressRepository;
    }

    public List<Pass> getAll() {
        return passRepository.findAll();
    }

    @Transactional
    public Pass save(PassDto dto) {
        Address address = addressRepository
                .findById(dto.getAddressId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Address with id " + dto.getAddressId() + " not found"));

        Pass pass = new Pass();
        pass.setAddress(address);
        pass.setPassType(dto.getPassType());
        pass.setLimitation(dto.getLimitation());
        pass.setKindPassType(dto.getKindPassType());
        pass.setName(dto.getName());
        pass.setCode(dto.getCode());

        return passRepository.save(pass);
    }

    public void delete(Long id) {
        passRepository.deleteById(id);
    }
}
