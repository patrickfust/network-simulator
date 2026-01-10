package dk.fust.networksimulator.service;

import dk.fust.networksimulator.dto.configuration.ConfigurationDto;
import dk.fust.networksimulator.dto.configuration.ConfigurationScenarioDto;
import dk.fust.networksimulator.dto.configuration.ConfigurationTargetSystemDto;
import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.model.TargetSystem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ConfigurationService {

    private final ModelMapper modelMapper;
    private final TargetSystemService targetSystemService;
    private final ScenarioService scenarioService;

    public ConfigurationDto getConfiguration() {
        List<ConfigurationTargetSystemDto> targetSystemDtos = targetSystemService.getAllTargetSystems().stream()
                .map(targetSystem -> modelMapper.map(targetSystem, ConfigurationTargetSystemDto.class))
                .toList();
        List<ConfigurationScenarioDto> scenarioDtos = scenarioService.getAllScenarios()
                .stream()
                .map(scenario -> modelMapper.map(scenario, ConfigurationScenarioDto.class))
                .toList();
        return new ConfigurationDto(targetSystemDtos, scenarioDtos);
    }

    public void updateConfiguration(ConfigurationDto configurationDto, boolean deleteBeforeUpdate) {
        if (deleteBeforeUpdate) {
            log.info("Deleting all existing scenarios and target systems before updating configuration");
            scenarioService.deleteAllScenarios();
            targetSystemService.deleteAllTargetSystems();
        }
        log.info("Updating target systems");
        List<TargetSystem> targetSystems = updateTargetSystems(configurationDto.getTargetSystems());
        log.info("Updating scenarios");
        updateScenarios(targetSystems, configurationDto.getScenarios());;
    }

    private void updateScenarios(List<TargetSystem> targetSystems, List<ConfigurationScenarioDto> scenarioDtos) {
        Map<String, TargetSystem> targetSystemByName = targetSystems.stream()
                .collect(java.util.stream.Collectors.toMap(TargetSystem::getSystemName, ts -> ts));
        for (ConfigurationScenarioDto scenarioDto : scenarioDtos) {
            Optional<Scenario> existing = scenarioService.findScenarioByName(scenarioDto.getName());
            Scenario scenario = modelMapper.map(scenarioDto, Scenario.class);
            scenario.setTargetSystem(targetSystemByName.get(scenarioDto.getTargetSystemName()));
            if (existing.isPresent()) {
                scenarioService.updateScenario(existing.get().getId(), scenario);
            } else {
                scenarioService.createScenario(scenario);
            }
        }
    }

    private List<TargetSystem> updateTargetSystems(List<ConfigurationTargetSystemDto> targetSystemDtos) {
        List<TargetSystem> allTargetSystems = new ArrayList<>();
        for (ConfigurationTargetSystemDto targetDto : targetSystemDtos) {
            Optional<TargetSystem> existing = targetSystemService.getTargetSystemByName(targetDto.getSystemName());
            TargetSystem targetSystem = modelMapper.map(targetDto, TargetSystem.class);
            if (existing.isPresent()) {
                allTargetSystems.add(targetSystemService.updateTargetSystem(existing.get().getId(), targetSystem));
            } else {
                allTargetSystems.add(targetSystemService.createTargetSystem(targetSystem));
            }
        }
        return allTargetSystems;
    }

}
