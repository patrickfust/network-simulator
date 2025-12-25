package dk.fust.networksimulator.repository;

import dk.fust.networksimulator.model.TargetSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TargetSystemRepository extends JpaRepository<TargetSystem, Long>  {

    Optional<TargetSystem> findBySystemName(String systemName);

}
