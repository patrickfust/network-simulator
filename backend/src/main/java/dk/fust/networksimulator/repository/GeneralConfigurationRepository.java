package dk.fust.networksimulator.repository;

import dk.fust.networksimulator.model.GeneralConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralConfigurationRepository extends JpaRepository<GeneralConfiguration, Long> {

    GeneralConfiguration findFirstBy();

}
