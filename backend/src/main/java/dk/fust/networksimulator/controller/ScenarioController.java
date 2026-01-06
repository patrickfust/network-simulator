package dk.fust.networksimulator.controller;

import dk.fust.networksimulator.dto.CreateScenarioDto;
import dk.fust.networksimulator.dto.ScenarioDto;
import org.modelmapper.ModelMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/scenarios")
@CrossOrigin(origins = "*")
@Tag(name = "Scenarios", description = "APIs for managing scenarios")
@AllArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final ModelMapper modelMapper;

    @Operation(summary = "List all scenarios")
    @GetMapping
    public ResponseEntity<List<ScenarioDto>> getAllScenarios() {
        return ResponseEntity.ok(
                scenarioService.getAllScenarios()
                        .stream()
                        .map(scenario -> modelMapper.map(scenario, ScenarioDto.class))
                        .collect(Collectors.toList()));
    }

    @Operation(summary = "Get a specific scenario")
    @GetMapping("/{id}")
    public ResponseEntity<ScenarioDto> getScenarioById(@PathVariable Long id) {
        return scenarioService.getScenarioById(id)
                .map(scenario -> ResponseEntity.ok(modelMapper.map(scenario, ScenarioDto.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a scenario",
            responses = {@ApiResponse(responseCode = "201", description = "Scenario successfully created")})
    @PostMapping
    public ResponseEntity<ScenarioDto> createScenario(@RequestBody CreateScenarioDto scenarioDto) {
        Scenario scenario = modelMapper.map(scenarioDto, Scenario.class);
        Scenario createdScenario = scenarioService.createScenario(scenario);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdScenario.getId())
            .toUri();
        return ResponseEntity.created(location).body(modelMapper.map(createdScenario, ScenarioDto.class));
    }

    @Operation(summary = "Update a specific scenario")
    @PutMapping("/{id}")
    public ResponseEntity<ScenarioDto> updateScenario(@PathVariable Long id, @RequestBody ScenarioDto scenarioDetailsDto) {
        try {
            Scenario scenarioDetails = modelMapper.map(scenarioDetailsDto, Scenario.class);
            if (!id.equals(scenarioDetails.getId())) {
                return ResponseEntity.badRequest().build();
            }
            Scenario updatedScenario = scenarioService.updateScenario(id, scenarioDetails);
            return ResponseEntity.ok(modelMapper.map(updatedScenario, ScenarioDto.class));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Enables a specific scenario")
    @GetMapping("/{id}/activate")
    public ResponseEntity<ScenarioDto> activateScenario(@PathVariable Long id) {
        try {
            Scenario scenario = scenarioService.getScenarioById(id).orElseThrow(() ->
                    new RuntimeException("Scenario not found with id: " + id));
            scenario.setEnableScenario(true);
            Scenario updatedScenario = scenarioService.updateScenario(id, scenario);
            return ResponseEntity.ok(modelMapper.map(updatedScenario, ScenarioDto.class));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Disables a specific scenario")
    @GetMapping("/{id}/deactivate")
    public ResponseEntity<ScenarioDto> deactivateScenario(@PathVariable Long id) {
        try {
            Scenario scenario = scenarioService.getScenarioById(id).orElseThrow(() ->
                    new RuntimeException("Scenario not found with id: " + id));
            scenario.setEnableScenario(false);
            Scenario updatedScenario = scenarioService.updateScenario(id, scenario);
            return ResponseEntity.ok(modelMapper.map(updatedScenario, ScenarioDto.class));
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
