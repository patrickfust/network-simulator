package dk.fust.networksimulator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@Schema(description = "Target system configuration used by the network simulator")
public class TargetSystemDto {

    @Schema(description = "Database id of the target system", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Human readable system name", example = "Payment Gateway", requiredMode = Schema.RequiredMode.REQUIRED)
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

    @Schema(description = "Whether to follow HTTP redirects", example = "true")
    private boolean followRedirect = true;

}
