package dk.fust.networksimulator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scenarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enable_scenario")
    private Boolean enableScenario = true;

    @Column(nullable = false)
    private String name;

    @Column
    private String path;

    @Column(length = 1000)
    private String description;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "timeout_ms")
    private Long timeoutMs;

    @Column(name = "follow_redirects")
    private Boolean followRedirect;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScenarioHeader> headers = new ArrayList<>();

    public Scenario(Long id, Boolean enableScenario, String name, String path, String description, Long latencyMs, Integer statusCode, String responseBody, Long timeoutMs, Boolean followRedirect) {
        this.id = id;
        this.enableScenario = enableScenario;
        this.name = name;
        this.path = path;
        this.description = description;
        this.latencyMs = latencyMs;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.timeoutMs = timeoutMs;
        this.followRedirect = followRedirect;
    }

}
