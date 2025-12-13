package dk.fust.networksimulator.controller;

import dk.fust.networksimulator.model.GeneralConfiguration;
import dk.fust.networksimulator.service.GeneralConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/_/api/v1/general-configuration")
@CrossOrigin(origins = "*")
@Tag(name = "General Configuration", description = "APIs for managing the general configuration")
@AllArgsConstructor
public class GeneralConfigurationController {

    private final GeneralConfigurationService generalConfigurationService;

    @Operation(summary = "Get the general configuration")
    @GetMapping
    public GeneralConfiguration getGeneralConfiguration() {
        return generalConfigurationService.getGeneralConfiguration();
    }

    @Operation(summary = "Update the general configuration")
    @PutMapping()
    public GeneralConfiguration updateConfiguration(@RequestBody GeneralConfiguration generalConfiguration) {
        return generalConfigurationService.updateGeneralConfiguration(generalConfiguration);
    }

}
