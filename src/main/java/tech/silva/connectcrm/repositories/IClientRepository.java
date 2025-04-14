package tech.silva.connectcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.silva.connectcrm.models.Client;

public interface IClientRepository extends JpaRepository<Client, Long> {
}
