package dk.fust.networksimulator.repository;

import dk.fust.networksimulator.model.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    @Query("SELECT s FROM Scenario s WHERE s.enableScenario = true AND (s.targetSystem IS NULL OR s.targetSystem.id = :targetSystemId)")
    List<Scenario> findEnabledScenariosByTargetSystem(@Param("targetSystemId") Long targetSystemId);

    Optional<Scenario> findByName(String name);

}
