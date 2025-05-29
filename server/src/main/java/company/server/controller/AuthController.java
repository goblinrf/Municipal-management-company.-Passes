package company.server.controller;

import company.server.db.entity.Users;
import company.server.db.jpaRepository.UsersRepository;
import company.server.model.UsersDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            log.warn("Ошибка входа: пользователь '{}' ввёл неверные данные", userDto.username());
            return ResponseEntity.status(401).body("Неверный логин или пароль");
        }

        session.setAttribute("user", userDto.username());
        log.info("Успешный вход пользователя '{}'", userDto.username());
        return ResponseEntity.ok("Успешный вход");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Вы вышли из системы");
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Не авторизован");
        }

        String username = authentication.getName();
        log.info("Сессия продолжается для пользователя '{}'", username);
        return ResponseEntity.ok(username);
    }

}
