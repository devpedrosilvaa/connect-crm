package tech.silva.connectcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.silva.connectcrm.models.AppUser;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
}
