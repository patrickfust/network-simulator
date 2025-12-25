package dk.fust.networksimulator.simulations;

import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
@Data
public class LatencySimulation implements Simulation {

    private long latencyMs;

    @Override
    public void doSimulation(ProxyRequest proxyRequest, ProxyResponse proxyResponse, SimulationChain chain) throws IOException {
        try {
            log.debug("Simulating latency to {} ms", latencyMs);
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while simulating latency", e);
        }
        chain.doSimulation(proxyRequest, proxyResponse);
    }

}
