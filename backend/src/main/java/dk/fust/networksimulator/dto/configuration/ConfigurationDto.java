package dk.fust.networksimulator.dto.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO used for the entire configuration of the Network Simulator")
public class ConfigurationDto {

    private List<ConfigurationTargetSystemDto> targetSystems;
    private List<ConfigurationScenarioDto> scenarios;

    public static ConfigurationDto fromJson(String configurationFile) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(configurationFile, ConfigurationDto.class);
    }

}
