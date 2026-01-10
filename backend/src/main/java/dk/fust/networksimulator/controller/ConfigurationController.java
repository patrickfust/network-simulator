package dk.fust.networksimulator.controller;

import dk.fust.networksimulator.dto.configuration.ConfigurationDto;
import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.model.TargetSystem;
import dk.fust.networksimulator.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/configuration")
@CrossOrigin(origins = "*")
@Tag(name = "Configuration", description = "APIs for managing configuration")
@AllArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @Operation(summary = "Retrieve the entire configuration")
    @GetMapping
    public ResponseEntity<ConfigurationDto> getConfiguration() {
        return ResponseEntity.ok(configurationService.getConfiguration());
    }

    @Operation(summary = "Updates entire configuration")
    @PostMapping
    public ResponseEntity<String> updateConfiguration(@RequestBody ConfigurationDto configurationDto,
                          @RequestParam(required = false) @Parameter(name = "deleteBeforeUpdate", description = "Deletes all existing target systems and scenarios will remove existing scenarios and target systems that are not in the configuration") Boolean deleteBeforeUpdate) {
        log.info("Updating configuration with {} target systems and {} scenarios. DeleteBeforeUpdate: {}",
                configurationDto.getTargetSystems().size(),
                configurationDto.getScenarios().size(),
                deleteBeforeUpdate);
        configurationService.updateConfiguration(configurationDto, deleteBeforeUpdate != null && deleteBeforeUpdate);
        return ResponseEntity.ok("Configuration updated successfully");
    }

}
