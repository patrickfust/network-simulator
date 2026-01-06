package dk.fust.networksimulator.service;

import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.repository.ScenarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ScenarioService {

    private ScenarioRepository scenarioRepository;

    public List<Scenario> getAllScenarios() {
        return scenarioRepository.findAll();
    }

    public Optional<Scenario> getScenarioById(Long id) {
        return scenarioRepository.findById(id);
    }

    public Optional<List<Scenario>> findScenariosByPath(String path, Long targetSystemId) {
        return scenarioRepository.findScenariosByPath(path, targetSystemId);
    }

    public Scenario createScenario(Scenario scenario) {
        // Before saving, establish the bidirectional relationship
        if (scenario.getHeaders() != null) {
            scenario.getHeaders().forEach(header -> header.setScenario(scenario));
        }
        return scenarioRepository.save(scenario);
    }

    public Scenario updateScenario(Long id, Scenario scenarioDetails) {
        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));
        scenario.setEnableScenario(scenarioDetails.getEnableScenario());
        scenario.setName(scenarioDetails.getName());
        scenario.setPath(scenarioDetails.getPath());
        scenario.setDescription(scenarioDetails.getDescription());
        scenario.setLatencyMs(scenarioDetails.getLatencyMs());
        scenario.setStatusCode(scenarioDetails.getStatusCode());
        scenario.setResponseBody(scenarioDetails.getResponseBody());
        scenario.setTimeoutMs(scenarioDetails.getTimeoutMs());
        scenario.setFollowRedirect(scenarioDetails.getFollowRedirect());
        scenario.setTargetSystem(scenarioDetails.getTargetSystem());
        updateHeaders(scenarioDetails, scenario);
        return scenarioRepository.save(scenario);
    }

    private static void updateHeaders(Scenario scenarioDetails, Scenario scenario) {
        // Update headers
        if (scenarioDetails.getHeaders() == null) {
            if (scenario.getHeaders() != null) {
                scenario.getHeaders().clear();
            }
        } else {
            // Remove headers that are no longer present
            scenario.getHeaders().removeIf(existingHeader ->
                    scenarioDetails.getHeaders().stream()
                            .noneMatch(newHeader -> newHeader.getId() != null &&
                                    newHeader.getId().equals(existingHeader.getId()))
            );

            // Update existing headers or add new ones
            scenarioDetails.getHeaders().forEach(newHeader -> {
                if (newHeader.getId() != null) {
                    // Update existing header
                    scenario.getHeaders().stream()
                            .filter(h -> h.getId().equals(newHeader.getId()))
                            .findFirst()
                            .ifPresent(existing -> {
                                existing.setHeaderName(newHeader.getHeaderName());
                                existing.setHeaderValue(newHeader.getHeaderValue());
                                existing.setHeaderReplaceValue(newHeader.getHeaderReplaceValue());
                            });
                } else {
                    // Add new header
                    newHeader.setScenario(scenario);
                    scenario.getHeaders().add(newHeader);
                }
            });
        }
    }

    public void deleteScenario(Long id) {
        scenarioRepository.deleteById(id);
    }
}
