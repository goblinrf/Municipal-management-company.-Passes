package company.server.db.facade;

import company.server.db.entity.Users;
import company.server.db.jpaRepository.UsersRepository;
import company.server.model.UsersDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Users create(UsersDto dto) {
        if (userRepository.findByName(dto.username()).isPresent()) {
            throw new IllegalArgumentException("User already exists: " + dto.username());
        }

        Users user = new Users();
        user.setName(dto.username());
        user.setPassword(passwordEncoder.encode(dto.rawPassword())); // Шифруем пароль

        return userRepository.save(user);
    }

    public Optional<Users> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<Users> findAll() {
        return userRepository.findAll();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
