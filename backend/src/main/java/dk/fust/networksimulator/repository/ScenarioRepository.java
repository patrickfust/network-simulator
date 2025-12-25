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

    @Query(value = "SELECT * FROM scenarios WHERE enable_scenario = true and (path is null or :path ~ REPLACE(REPLACE(path, '*', '.*'), '?', '.'))",
            nativeQuery = true)
    Optional<List<Scenario>> findScenariosByPath(@Param("path") String path);

}
