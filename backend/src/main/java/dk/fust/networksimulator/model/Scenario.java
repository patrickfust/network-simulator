package dk.fust.networksimulator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "scenarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enable_scenario")
    private boolean enableScenario = true;

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

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "follow_redirects")
    private Boolean followRedirect = true;

}
