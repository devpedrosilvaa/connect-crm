package tech.silva.connectcrm.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.silva.connectcrm.enums.Role;
import tech.silva.connectcrm.exceptions.UniqueUserViolationException;
import tech.silva.connectcrm.exceptions.UsernameNotFoundException;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.repositories.IUserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AppUser findByEmail(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(String.format("User with Username= %s not found", username));
                });
    }

    @Transactional(readOnly = true)
    public Role findRoleByEmail(String username) {
        AppUser user = findByEmail(username);
        return user.getRole();
    }

    public AppUser saveUser(AppUser user){
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UniqueUserViolationException(String.format("User with email: %s already registered. Try again!", user.getEmail()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<AppUser> listAllUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AppUser findById(Long id){
        return userRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(String.format("User with Id= %s not found", id));
                }
        );
    }
}
