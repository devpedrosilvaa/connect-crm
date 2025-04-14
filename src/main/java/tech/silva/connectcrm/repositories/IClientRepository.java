package tech.silva.connectcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.silva.connectcrm.models.Client;

import java.util.Optional;

public interface IClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
}
