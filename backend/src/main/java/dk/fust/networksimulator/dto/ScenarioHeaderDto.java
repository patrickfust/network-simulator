package dk.fust.networksimulator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScenarioHeaderDto {

    private String headerName;
    private String headerValue;
    private Boolean headerReplaceValue;

}
