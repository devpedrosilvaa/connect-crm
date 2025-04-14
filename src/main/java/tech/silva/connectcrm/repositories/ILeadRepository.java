package tech.silva.connectcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.models.Lead;

import java.util.List;
import java.util.Optional;

public interface ILeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByEmail(String email);

    List<Lead> findAllByUser(AppUser user);
}
