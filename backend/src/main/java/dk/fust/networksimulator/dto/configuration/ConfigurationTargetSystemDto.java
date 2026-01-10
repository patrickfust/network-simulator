package dk.fust.networksimulator.dto.configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Target system configuration used by the network simulator")
public class ConfigurationTargetSystemDto {

    @Schema(description = "Human readable system name", example = "payment-gateway", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 200)
    private String systemName;

    @Schema(description = "Base URL of the target system", example = "https://api.example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 2000)
    private String targetBaseUrl;

    @Schema(description = "Request timeout in milliseconds", example = "5000")
    @Min(0)
    private long timeoutMs;

    @Schema(description = "Whether to follow HTTP redirects or not", example = "true")
    private boolean followRedirect;

}
