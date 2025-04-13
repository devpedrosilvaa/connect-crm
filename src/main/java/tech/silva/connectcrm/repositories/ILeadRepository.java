package tech.silva.connectcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.silva.connectcrm.models.Lead;

import java.util.Optional;

public interface ILeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByEmail(String email);
}
