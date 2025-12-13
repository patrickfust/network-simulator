package dk.fust.networksimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NetworkSimulatorApplication {

    static void main(String[] args) {
        SpringApplication.run(NetworkSimulatorApplication.class, args);
    }

}
