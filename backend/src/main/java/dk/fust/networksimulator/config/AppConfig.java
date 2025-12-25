package dk.fust.networksimulator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "dk.fust.networksimulator.repository")
public class AppConfig {
}
