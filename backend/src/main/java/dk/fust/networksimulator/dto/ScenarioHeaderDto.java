package dk.fust.networksimulator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@Schema(description = "HTTP header entry used in scenario responses")
public class ScenarioHeaderDto {

    @Schema(description = "HTTP header name", example = "Content-Type", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 200)
    private String headerName;

    @Schema(description = "HTTP header value", example = "application/json")
    @Size(max = 2000)
    private String headerValue;

    @Schema(description = "Whether to replace existing header values when applying this header", example = "false")
    private Boolean headerReplaceValue = false;

}
