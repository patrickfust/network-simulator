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

    public Optional<List<Scenario>> findScenariosByPath(String path) {
        return scenarioRepository.findScenariosByPath(path);
    }

    public Scenario createScenario(Scenario scenario) {
        return scenarioRepository.save(scenario);
    }

    public Scenario updateScenario(Long id, Scenario scenarioDetails) {
        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));
        scenario.setEnableScenario(scenarioDetails.isEnableScenario());
        scenario.setName(scenarioDetails.getName());
        scenario.setPath(scenarioDetails.getPath());
        scenario.setDescription(scenarioDetails.getDescription());
        scenario.setLatencyMs(scenarioDetails.getLatencyMs());
        scenario.setStatusCode(scenarioDetails.getStatusCode());
        scenario.setResponseBody(scenarioDetails.getResponseBody());
        scenario.setTimeoutMs(scenarioDetails.getTimeoutMs());
        scenario.setFollowRedirect(scenarioDetails.getFollowRedirect());
        return scenarioRepository.save(scenario);
    }

    public void deleteScenario(Long id) {
        scenarioRepository.deleteById(id);
    }
}
