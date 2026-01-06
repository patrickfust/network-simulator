package dk.fust.networksimulator.controller;

import dk.fust.networksimulator.dto.CreateTargetSystemDto;
import dk.fust.networksimulator.dto.TargetSystemDto;
import dk.fust.networksimulator.model.TargetSystem;
import dk.fust.networksimulator.service.TargetSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/target-systems")
@CrossOrigin(origins = "*")
@Tag(name = "Target Systems", description = "APIs for managing target systems")
@AllArgsConstructor
public class TargetSystemController {

    private final TargetSystemService targetSystemService;
    private final ModelMapper modelMapper;

    @Operation(summary = "List all target systems")
    @GetMapping
    public ResponseEntity<List<TargetSystemDto>> getAllTargetSystems() {
        List<TargetSystem> allTargetSystems = targetSystemService.getAllTargetSystems();
        return ResponseEntity.ok(
                allTargetSystems.stream()
                        .map(targetSystem -> modelMapper.map(targetSystem, TargetSystemDto.class))
                        .collect(Collectors.toList())
        );
    }

    @Operation(summary = "Get a specific target system")
    @GetMapping("/{id}")
    public ResponseEntity<TargetSystemDto> getTargetSystemById(@PathVariable Long id) {
        return targetSystemService.getTargetSystemById(id)
                .map(targetSystem -> ResponseEntity.ok(modelMapper.map(targetSystem, TargetSystemDto.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a specific target system, by name")
    @GetMapping("/name/{systemName}")
    public ResponseEntity<TargetSystemDto> getTargetSystemByName(@PathVariable String systemName) {
        return targetSystemService.getTargetSystemByName(systemName)
                .map(targetSystem -> ResponseEntity.ok(modelMapper.map(targetSystem, TargetSystemDto.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a target system",
            responses = {@ApiResponse(responseCode = "201", description = "Target system successfully created")})
    @PostMapping
    public ResponseEntity<TargetSystemDto> createTargetSystem(@RequestBody CreateTargetSystemDto createTargetSystemDto) {
        TargetSystem targetSystem = modelMapper.map(createTargetSystemDto, TargetSystem.class);
        TargetSystem createdTargetSystem = targetSystemService.createTargetSystem(targetSystem);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTargetSystem.getId())
                .toUri();
        return ResponseEntity.created(location).body(modelMapper.map(createdTargetSystem, TargetSystemDto.class));
    }

    @Operation(summary = "Update a specific target system")
    @PutMapping("/{id}")
    public ResponseEntity<TargetSystemDto> updateTargetSystem(@PathVariable Long id, @RequestBody TargetSystemDto targetSystemDto) {
        try {
            TargetSystem targetSystemDetails = modelMapper.map(targetSystemDto, TargetSystem.class);
            TargetSystem updatedTargetSystem = targetSystemService.updateTargetSystem(id, targetSystemDetails);
            return ResponseEntity.ok(modelMapper.map(updatedTargetSystem, TargetSystemDto.class));
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
