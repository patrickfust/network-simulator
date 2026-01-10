// src/main/java/dk/fust/networksimulator/dto/ScenarioDto.java
package dk.fust.networksimulator.dto.configuration;

import dk.fust.networksimulator.dto.ScenarioHeaderDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Scenario configuration used by the network simulator")
public class ConfigurationScenarioDto {

    @Schema(description = "Whether the scenario is enabled or not", example = "true")
    private Boolean enableScenario = true;

    @Schema(description = "Human readable name", example = "Slow downstream test")
    @Size(max = 200)
    private String name;

    @Schema(description = "Request path to match (prefix or exact)", example = "/api/v1/resource", required = true)
    @NotNull
    private String path;

    @Schema(description = "Description of the scenario", example = "Simulates slow responses for downloads")
    private String description;

    @Schema(description = "Artificial latency to add in milliseconds", example = "250")
    private Long latencyMs;

    @Schema(description = "HTTP status code to return", example = "200")
    private Integer statusCode;

    @Schema(description = "Response body to return", example = "Some body")
    private String bodyToReturn;

    @Schema(description = "Response timeout in milliseconds", example = "5000")
    private Long timeoutMs;

    @Schema(description = "Follow HTTP redirects", example = "false")
    private Boolean followRedirect;

    @Schema(description = "Headers to include in the response")
    private List<ScenarioHeaderDto> headers = new ArrayList<>();

    @Schema(description = "Target system name", example = "payment-gateway")
    private String targetSystemName;

    @Schema(description = "Throttle response bandwidth in bytes per second", example = "1024")
    private Long responseBytesPerSecond;

}
