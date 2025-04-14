package tech.silva.connectcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.models.Client;

import java.util.List;
import java.util.Optional;

public interface IClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    List<Client> findAllByUser(AppUser user);
}
