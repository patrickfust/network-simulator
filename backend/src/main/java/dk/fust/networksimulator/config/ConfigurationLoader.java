package dk.fust.networksimulator.config;

import dk.fust.networksimulator.dto.configuration.ConfigurationDto;
import dk.fust.networksimulator.service.ConfigurationService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
@AllArgsConstructor
public class ConfigurationLoader {

    private final ConfigurationService configurationService;

    @PostConstruct
    public void loadConfiguration() throws IOException {
        String networkSimulatorConfiguration = System.getenv("NETWORK_SIMULATOR_CONFIGURATION");
        if (networkSimulatorConfiguration != null) {
            log.info("Loading configuration from environment");
            configurationService.updateConfiguration(ConfigurationDto.fromJson(networkSimulatorConfiguration), true);
        }
    }
}
