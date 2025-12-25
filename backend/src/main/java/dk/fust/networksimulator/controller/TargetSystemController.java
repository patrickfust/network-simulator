package dk.fust.networksimulator.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import dk.fust.networksimulator.model.TargetSystem;
import dk.fust.networksimulator.service.ProxyService;
import dk.fust.networksimulator.service.TargetSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/target-systems")
@CrossOrigin(origins = "*")
@Tag(name = "Target Systems", description = "APIs for managing target systems")
@AllArgsConstructor
public class TargetSystemController {

    private final TargetSystemService targetSystemService;
    private final ProxyService proxyService;

    @Operation(summary = "List all target systems")
    @GetMapping
    public List<TargetSystem> getAllTargetSystems() {
        return targetSystemService.getAllTargetSystems();
    }

    @Operation(summary = "Get a specific target system")
    @GetMapping("/{id}")
    public ResponseEntity<TargetSystem> getTargetSystemById(@PathVariable Long id) {
        return targetSystemService.getTargetSystemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a specific target system, by name")
    @GetMapping("/name/{systemName}")
    public ResponseEntity<TargetSystem> getTargetSystemByName(@PathVariable String systemName) {
        return targetSystemService.getTargetSystemByName(systemName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a target system",
            responses = {@ApiResponse(responseCode = "201", description = "Target system successfully created")})
    @PostMapping
    public ResponseEntity<TargetSystem> createTargetSystem(@RequestBody TargetSystem targetSystem) {
        targetSystem.setId(null); // Ensure ID is null for new entity
        TargetSystem createdTargetSystem = targetSystemService.createTargetSystem(targetSystem);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTargetSystem.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTargetSystem);
    }

    @Operation(summary = "Update a specific target system")
    @PutMapping("/{id}")
    public ResponseEntity<TargetSystem> updateTargetSystem(@PathVariable Long id, @RequestBody TargetSystem targetSystemDetails) {
        try {
            TargetSystem updatedTargetSystem = targetSystemService.updateTargetSystem(id, targetSystemDetails);
            return ResponseEntity.ok(updatedTargetSystem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a specific target system")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTargetSystem(@PathVariable Long id) {
        targetSystemService.deleteTargetSystem(id);
        return ResponseEntity.noContent().build();
    }

}
