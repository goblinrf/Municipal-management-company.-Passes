package company.server.controller;

import company.server.db.entity.Users;
import company.server.db.jpaRepository.UsersRepository;
import company.server.model.UsersDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UsersDto userDto) {


        Users user = new Users();
        user.setName(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.rawPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь создан");
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UsersDto userDto) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDto.username());
            user.setPassword(passwordEncoder.encode(userDto.rawPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("Пользователь обновлён");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("Пользователь удалён");
    }
}
