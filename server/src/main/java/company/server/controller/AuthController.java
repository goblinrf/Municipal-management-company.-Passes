package company.server.controller;

import company.server.db.entity.Users;
import company.server.db.jpaRepository.UsersRepository;
import company.server.model.UsersDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsersDto userDto, HttpSession session) {
        Optional<Users> user = userRepository.findByName(userDto.username());
        System.out.println(userDto.rawPassword()+userDto.username());
        if (user.isEmpty() || !passwordEncoder.matches(userDto.rawPassword(), user.get().getPassword())) {
            return ResponseEntity.status(401).body("Неверный логин или пароль");
        }

        session.setAttribute("user", userDto.username());
        return ResponseEntity.ok("Успешный вход");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Вы вышли из системы");
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {
        String username = (String) session.getAttribute("user");
        if (username == null) {
            return ResponseEntity.status(401).body("Не авторизован");
        }
        return ResponseEntity.ok(username);
    }

}
