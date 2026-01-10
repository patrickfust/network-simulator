package dk.fust.networksimulator.service;

import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.simulations.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SimulatorService {

    private final PassThroughSimulation passThroughSimulation;

    public SimulationChain makeSimulationChain(List<Scenario> scenarios) {
        SimulationChainDecorator simulationChainDecorator = new SimulationChainDecorator();

        simulationChainDecorator.addSimulation(passThroughSimulation);
        for (Scenario scenario : scenarios) {
            addLatencySimulation(scenario, simulationChainDecorator);
            addStatusCodeSimulation(scenario, simulationChainDecorator);
            addHeaderSimulation(scenario, simulationChainDecorator);
        }

        return simulationChainDecorator;
    }

    private static void addStatusCodeSimulation(Scenario scenario, SimulationChainDecorator simulationChainDecorator) {
        if (scenario.getStatusCode() != null) {
            Simulation statusCodeSimulation = new StatusCodeSimulation(HttpStatusCode.valueOf(scenario.getStatusCode()),
                    scenario.getBodyToReturn() != null ? scenario.getBodyToReturn().getBytes() : null);
            simulationChainDecorator.addSimulation(statusCodeSimulation);
        }
    }

    private static void addLatencySimulation(Scenario scenario, SimulationChainDecorator simulationChainDecorator) {
        if (scenario.getLatencyMs() != null) {
            LatencySimulation latencySimulation = (LatencySimulation) simulationChainDecorator.getSimulationOfClass(LatencySimulation.class);
            if (latencySimulation == null) {
                latencySimulation = new LatencySimulation(scenario.getLatencyMs());
                simulationChainDecorator.addSimulation(latencySimulation);
            } else {
                latencySimulation.setLatencyMs(latencySimulation.getLatencyMs() + scenario.getLatencyMs());
            }
        }
    }

    private static void addHeaderSimulation(Scenario scenario, SimulationChainDecorator simulationChainDecorator) {
        if (scenario.getHeaders() != null && !scenario.getHeaders().isEmpty()) {
            HeaderSimulation headerSimulation = (HeaderSimulation) simulationChainDecorator.getSimulationOfClass(HeaderSimulation.class);
            if (headerSimulation == null) {
                headerSimulation = new HeaderSimulation();
                simulationChainDecorator.addSimulation(headerSimulation);
            }
            headerSimulation.addHeaders(scenario.getHeaders());
        }
    }
}
