package dk.fust.networksimulator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ScenarioDto {

    private Long id;
    private Boolean enableScenario = true;
    private String name;
    private String path;
    private String description;
    private Long latencyMs;
    private Integer statusCode;
    private String responseBody;
    private Long timeoutMs;
    private Boolean followRedirect;
    private List<ScenarioHeaderDto> headers = new ArrayList<>();
    private Long targetSystemId;

}
