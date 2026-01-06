package dk.fust.networksimulator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TargetSystemDto {

    private Long id;
    private String systemName;
    private String targetBaseUrl;
    private long timeoutMs;
    private boolean followRedirect = true;

}
