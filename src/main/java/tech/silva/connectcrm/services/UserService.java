package tech.silva.connectcrm.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.silva.connectcrm.enums.Role;
import tech.silva.connectcrm.exceptions.UniqueUserViolationException;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.repositories.IUserRepository;

@Service
@Transactional
public class UserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser findByEmail(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("User not found");
                });
    }

    public Role findRoleByEmail(String username) {
        AppUser user = findByEmail(username);
        return user.getRole();
    }

    public AppUser saveUser(AppUser user){
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UniqueUserViolationException(String.format("User with email: %s already registered. Try again!", user.getEmail()));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
