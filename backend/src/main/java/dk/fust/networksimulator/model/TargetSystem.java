package dk.fust.networksimulator.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "target_systems")
@Data
@NoArgsConstructor
public class TargetSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_name", nullable = false, unique = true)
    private String systemName;

    @Column(name = "target_base_url", nullable = false)
    private String targetBaseUrl;

    @Column(name = "timeout_ms")
    private long timeoutMs;

    @Column(name = "follow_redirects")
    private boolean followRedirect = true;

}
