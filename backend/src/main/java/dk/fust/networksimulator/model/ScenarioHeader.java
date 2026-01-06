package dk.fust.networksimulator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "scenario_headers")
@Data
@NoArgsConstructor
@ToString(exclude = "scenario")
public class ScenarioHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "header_name", nullable = false)
    private String headerName;

    @Column(name = "header_value", nullable = false)
    private String headerValue;

    @Column(name = "header_replace_value", nullable = false)
    private Boolean headerReplaceValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    @JsonIgnore
    private Scenario scenario;

}
