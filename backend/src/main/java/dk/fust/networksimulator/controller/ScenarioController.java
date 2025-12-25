package dk.fust.networksimulator.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.service.ProxyService;
import dk.fust.networksimulator.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/scenarios")
@CrossOrigin(origins = "*")
@Tag(name = "Scenarios", description = "APIs for managing scenarios")
@AllArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final ProxyService proxyService;

    @Operation(summary = "List all scenarios")
    @GetMapping
    public List<Scenario> getAllScenarios() {
        return scenarioService.getAllScenarios();
    }

    @Operation(summary = "Get a specific scenario")
    @GetMapping("/{id}")
    public ResponseEntity<Scenario> getScenarioById(@PathVariable Long id) {
        return scenarioService.getScenarioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a scenario",
            responses = {@ApiResponse(responseCode = "201", description = "Scenario successfully created")})
    @PostMapping
    public ResponseEntity<Scenario> createScenario(@RequestBody Scenario scenario) {
        scenario.setId(null); // Ensure ID is null for new entity
        Scenario createdScenario = scenarioService.createScenario(scenario);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdScenario.getId())
            .toUri();
        return ResponseEntity.created(location).body(createdScenario);
    }

    @Operation(summary = "Update a specific scenario")
    @PutMapping("/{id}")
    public ResponseEntity<Scenario> updateScenario(@PathVariable Long id, @RequestBody Scenario scenarioDetails) {
        try {
            Scenario updatedScenario = scenarioService.updateScenario(id, scenarioDetails);
            return ResponseEntity.ok(updatedScenario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Enables a specific scenario")
    @GetMapping("/{id}/activate")
    public ResponseEntity<Scenario> activateScenario(@PathVariable Long id) {
        try {
            Scenario scenario = scenarioService.getScenarioById(id).orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));
            scenario.setEnableScenario(true);
            Scenario updatedScenario = scenarioService.updateScenario(id, scenario);
            return ResponseEntity.ok(updatedScenario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Disables a specific scenario")
    @GetMapping("/{id}/deactivate")
    public ResponseEntity<Scenario> deactivateScenario(@PathVariable Long id) {
        try {
            Scenario scenario = scenarioService.getScenarioById(id).orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));
            scenario.setEnableScenario(false);
            Scenario updatedScenario = scenarioService.updateScenario(id, scenario);
            return ResponseEntity.ok(updatedScenario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a specific scenario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScenario(@PathVariable Long id) {
        scenarioService.deleteScenario(id);
        return ResponseEntity.noContent().build();
    }

}
