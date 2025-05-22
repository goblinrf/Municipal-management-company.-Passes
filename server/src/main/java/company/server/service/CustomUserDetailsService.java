package company.server.service;

import company.server.db.entity.Users;
import company.server.db.jpaRepository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return org.springframework.security.core.userdetails.User
                .withUsername(users.getName())
                .password(users.getPassword()) // уже захэширован
                .roles("USER")
                .build();
    }
}
